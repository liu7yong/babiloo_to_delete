/*
 * JaLingo, http://jalingo.sourceforge.net/
 *
 * Copyright (c) 2002-2006 Oleksandr Shyshko
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ja.lingo.application.gui.main.navigator;

import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.centre.gui.actionbinder.config.NListenerGroup;
import ja.centre.gui.util.IGui;
import ja.centre.gui.util.SelectAllOnEscapeListener;
import ja.centre.util.assertions.Arguments;
import ja.lingo.application.gui.drophandler.DropHandler;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.model.Preferences;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.Gaps;
import ja.lingo.application.util.articlelist.ArticlesListModel;
import ja.lingo.application.util.articlelist.ArticleListMenu;
import ja.lingo.application.util.misc.Strings;
import ja.lingo.engine.EngineListenerAdapter;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.IFinder;
import ja.lingo.engine.beans.IArticle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class NavigatorGui implements IGui {
    private JPanel gui;

    @NListenerGroup( {
        @NListener( type = KeyListener.class, mappings = "keyPressed > onKeyPressed" ),
        @NListener( property = "document", type = DocumentListener.class, mappings = {
            "insertUpdate  > onDocumentChanged",
            "removeUpdate  > onDocumentChanged",
            "changedUpdate > onDocumentChanged"
        } )
    } )
    private JTextField field;

    @NListener ( type = MouseListener.class, mappings = {
            "mousePressed  > mousePressedOrReleased",
            "mouseReleased > mousePressedOrReleased"
    } )
    private JList list;
    private boolean isNavigationEnabled = true;

    private static final DefaultListModel MODEL_EMPTY = new DefaultListModel();

    private IEngine engine;
    private Model model;

    private String lastNavigated;
    private JScrollPane scroll;

    public NavigatorGui( final Model model, IEngine engine, DropHandler dropHandler ) {
        this.engine = engine;
        this.model = model;

        this.engine.addEngineListener( new EngineListenerAdapter() {
            public void uncompiled() {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        setEmptyMergedIndex();
                    }
                } );
            }
            public void compiled() {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        setFinder( NavigatorGui.this.engine.getFinder() );
                    }
                } );
            }
        } );

        this.model.addApplicationModelListener( new ModelAdapter() {
            public void initialize( Preferences preferences ) {
                setFinder( NavigatorGui.this.engine.getFinder() );

/*
                for ( int i = 0; i < NavigatorGui.this.engine.getFinder().size(); i++ ) {
                    IArticle article = NavigatorGui.this.engine.getFinder().find( i );
                    String body = article.getBody( 0 );
                    if ( body.contains( "develop" ) ) {
                        System.out.println( "i = " + i + ", body = " + body );
                    }
                }
*/
            }

            public void navigate( String title ) {
                NavigatorGui.this.navigate( title );
            }

            public void navigateAndTranslate( String title ) {
                NavigatorGui.this.navigateAndTranslate( title );
            }
            public void requestFocusInNavigator() {
                field.requestFocus();
            }
        } );

        list = Components.list();
        list.setFocusable( false );
        list.setTransferHandler( dropHandler );

        field = Components.textField();
        SelectAllOnEscapeListener.register( field );

        scroll = Components.scrollVertical( list );

        gui = new JPanel( new BorderLayout( Gaps.GAP3, Gaps.GAP3 ) );
        gui.add( field,   BorderLayout.NORTH );
        gui.add( scroll,    BorderLayout.CENTER );

        new ArticleListMenu( list, model, true, false );

        ActionBinder.bind( this );
    }

    public void mousePressedOrReleased( MouseEvent e ) {
        if ( !list.isEnabled() || !SwingUtilities.isLeftMouseButton( e ) ) {
            return;
        }

        if ( setLastNavigatedTitle( getSelectedIndexText() ) ) {
            return;
        }

        setSearchTextWithoutNavigating( getSelectedIndexText() );
        translateSelected();
    }

    public void onDocumentChanged( DocumentEvent event ) {
        if ( !isNavigationEnabled ) {
            return;
        }

        if ( Strings.isEmpty( getSearchText() ) ) {
            return;
        }

        int index = engine.getFinder().indexOfFirstLike( getSearchText() );

        setSelectedIndex( index );
        ensureSelectedOnTop( index );
    }

    public void onKeyPressed( KeyEvent e ) {
        int selectedIndex = list.getSelectedIndex();
        int newIndex = selectedIndex;

        int rowsOnScreen = scroll.getHeight() / list.getFixedCellHeight();

        switch ( e.getKeyCode() ) {
        case KeyEvent.VK_UP:
        case KeyEvent.VK_KP_UP:
            if ( !e.isAltDown() ) {
                newIndex--;
            }
            break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_KP_DOWN:
            if ( !e.isAltDown() ) {
                newIndex++;
            }
            break;
        case KeyEvent.VK_PAGE_UP:
            newIndex -= rowsOnScreen;
            break;
        case KeyEvent.VK_PAGE_DOWN:
            newIndex += rowsOnScreen;
            break;
        case KeyEvent.VK_ENTER:
            if ( (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0 ) {
                model.suggest( field.getText().trim() );
            } else if ( (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) > 0 ) {
                model.popOut( field.getText().trim() );
            } else {
                translateSelected();
            }
            break;
        }

        // check, whether index was changed
        if ( newIndex == selectedIndex ) {
            return;
        }

        int size = list.getModel().getSize();

        if ( newIndex < 0 ) {
            newIndex = 0;
        }
        if ( newIndex >= size ) {
            newIndex = size - 1;
        }

        setSelectedIndex( newIndex );
        setSearchTextWithoutNavigating( getSelectedIndexText() );
    }

    private int getSelectedIndex() {
        return list.getSelectedIndex();
    }
    private void setSelectedIndex( int index ) {
        if ( index == -1 ) {
            list.getSelectionModel().clearSelection();
        } else if ( list.getModel().getSize() != 0 ){
            list.setSelectedIndex( index );

            list.ensureIndexIsVisible( index );
        }
    }
    private void ensureSelectedOnTop( int index ) {
        // a hack to make selected iteam apear up in list TODO rewrite hack?
        Rectangle cellBounds = list.getCellBounds( index, list.getModel().getSize() - 1 );
        list.scrollRectToVisible( cellBounds );
    }

    private String getSelectedIndexText() {
        int index = list.getSelectedIndex();
        return index == -1 ? "" : ( (ArticlesListModel) list.getModel() ).getEntity( index ).getTitle();
    }

    private String getSearchText() {
        return field.getText();
    }
    private void setSearchText( String text ) {
        field.setText( text );
        selectAll();
    }
    private void setSearchTextWithoutNavigating( String text ) {
        disableNavigation();
        field.setText( text );
        selectAll();
        enableNavigation();
    }

    private void enableNavigation() {
        isNavigationEnabled = true;
    }
    private void disableNavigation() {
        isNavigationEnabled = false;
    }

    private void selectAll() {
        field.requestFocus();
        field.selectAll();
    }

    private void setEmptyMergedIndex() {
        list.setEnabled( false );
        field.setEnabled( false );

        list.setModel( MODEL_EMPTY );
    }
    private void setFinder( IFinder finder ) {
        list.setEnabled( finder.size() > 0 );
        field.setEnabled( finder.size() > 0 );

        ArticlesListModel.install( list, finder );

        // TODO refactor: restart search without this trick, just trigger a listener
        lastNavigated = null;

        if ( field.getText().length() != 0 ) {
            navigateAndTranslate( field.getText() );
        } else {
            setSelectedIndex( 0 );
        }
    }

    private void navigate( String title ) {
        if ( setLastNavigatedTitle( title ) ) {
            return;
        }

        setSearchText( title );
    }

    private void navigateAndTranslate( String title ) {
        navigate( title );

        if ( engine.getTitleComparator().compare( title, getSelectedIndexText() ) == 0 ) {
            translateSelected();
        } else {
            lastNavigated = null;
            model.translateNotFound( title );
        }
    }

    private boolean setLastNavigatedTitle( String title ) {
        Arguments.assertNotNull( "title", title );

        boolean wasDuplicate = lastNavigated != null && lastNavigated.equals( title );

        lastNavigated = title;

        return wasDuplicate;
    }

    private void translateSelected() {
        if ( getSelectedIndex() < 0 ) {
            return;
        }

        IArticle article = engine.getFinder().get( getSelectedIndex() );

        model.bookmark( article );
        model.translate( article );

        selectAll();
    }

    public JComponent getGui() {
        return gui;
    }
}
