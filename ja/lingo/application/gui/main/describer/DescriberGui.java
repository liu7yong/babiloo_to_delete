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

package ja.lingo.application.gui.main.describer;

import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.centre.gui.actionbinder.config.NListenerGroup;
import ja.centre.gui.util.CardPanel;
import ja.centre.util.assertions.States;
import ja.centre.util.search.TextSearcher;
import ja.lingo.application.gui.actions.Actions;
import ja.lingo.application.gui.drophandler.DropHandler;
import ja.lingo.application.gui.main.describer.panels.ArticleNotFoundPanel;
import ja.lingo.application.gui.main.describer.panels.ArticlePanel;
import ja.lingo.application.gui.main.describer.panels.WelcomePanel;
import ja.lingo.application.gui.main.describer.find.FindGui;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.model.Preferences;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.beans.IArticle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

public class DescriberGui {
    private static final Log LOG = LogFactory.getLog( DescriberGui.class );

    @NListenerGroup({
        @NListener(property = "editorPane", type = MouseListener.class, mappings = "mouseClicked > mouseClickedOnEditorPane"),
        @NListener(property = "editorPane", type = KeyListener.class,   mappings = "keyTyped > keyTypedOnEditorPane")
    })
    private ArticlePanel articlePanel;

    private FindGui findGui;

    @NListenerGroup({
        @NListener(property = "saveAsItem", type = ActionListener.class, mappings = "actionPerformed > doSaveAs"),
        @NListener(property = "popOutItem", type = ActionListener.class, mappings = "actionPerformed > popOut")
    })
    private DescriberMenu menu;

    @NListenerGroup({
        @NListener( property = "copyItem",              type = ActionListener.class, mappings = "actionPerformed > copySelected" ),
        @NListener( property = "copyIntoSearchField",   type = ActionListener.class, mappings = "actionPerformed > copyIntoSearchField" ),
        @NListener( property = "translateItem",         type = ActionListener.class, mappings = "actionPerformed > translateSelected" ),
        @NListener( property = "popOutItem",            type = ActionListener.class, mappings = "actionPerformed > popOutSelected" ),
        @NListener( property = "translateFirstItem",    type = ActionListener.class, mappings = "actionPerformed > translateFirstSelected" ),
        @NListener( property = "suggestItem",           type = ActionListener.class, mappings = "actionPerformed > suggestSelected" )
    })
    private DescriberMenuOnSelect menuOnSelect;

    private Model model;
    private IEngine engine;

    private CardPanel cardPanel;

    private JPanel describerPanel;
    private ArticleNotFoundPanel articleNotFoundPanel;
    private WelcomePanel welcomePanel;


    public DescriberGui( Model model, Actions actions, IEngine engine, DropHandler dropHandler ) {
        this.model = model;
        this.engine = engine;

        this.model.addApplicationModelListener( new ModelAdapter() {
            public void initialize( Preferences preferences ) {
                cardPanel.show( welcomePanel );
            }
            public void translate( IArticle article, String highlight ) {
                LOG.info( "translating \"" + article.getTitle() + "\"..." );
                articlePanel.setArticle( article, highlight );
                cardPanel.show( describerPanel );
            }
            public void translateNotFound( String articleTitle ) {
                articleNotFoundPanel.update( articleTitle, getClosestArticleTitleFor( articleTitle ),
                        !DescriberGui.this.engine.getFinder().isEmpty() );
                cardPanel.show( articleNotFoundPanel );
            }

            public void find( String text, boolean fromStart, boolean forwardDirection, boolean caseSensetive, boolean wholeWordsOnly ) {
                DescriberGui.this.find( text, fromStart, forwardDirection, caseSensetive, wholeWordsOnly );
            }
            public void find_show() {
                showFindGui();
            };
        } );

        articlePanel = new ArticlePanel( engine, false );

        findGui = new FindGui( model );

        menu = new DescriberMenu( actions );
        menuOnSelect = new DescriberMenuOnSelect( engine );

        describerPanel = new JPanel( new BorderLayout() );
        describerPanel.add( articlePanel.getGui(), BorderLayout.CENTER );
        describerPanel.add( findGui.getGui(), BorderLayout.SOUTH );

        articleNotFoundPanel = new ArticleNotFoundPanel( model );
        welcomePanel = new WelcomePanel( actions );

        cardPanel = new CardPanel();
        cardPanel.add( describerPanel );
        cardPanel.add( articleNotFoundPanel );
        cardPanel.add( welcomePanel );

        cardPanel.getGui().setBorder( BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder( 0, 0, 0, 1 )
        ) );
        
        articlePanel.getEditorPane().setTransferHandler( dropHandler );
        articleNotFoundPanel.getGui().setTransferHandler( dropHandler );
        welcomePanel.getGui().setTransferHandler( dropHandler );

        ActionBinder.bind( this );
    }

    private void showFindGui() {
        findGui.showAndRequestFocus();
        // TODO if in search mode, copy search subject
    }

    public void mouseClickedOnEditorPane( MouseEvent e ) {
        if ( !articlePanel.hasArticle() ) {
            return;
        }

        if ( SwingUtilities.isLeftMouseButton( e ) && e.getClickCount() > 1 ) {
            if ( getSelectedTextTrimmed().length() > 0 ) {
                Rectangle rectangle;
                try {
                    rectangle = articlePanel.getEditorPane().modelToView( articlePanel.getEditorPane().getSelectionStart() );
                } catch ( BadLocationException e1 ) {
                    throw new RuntimeException( e1 );
                }

                showMenuOnSelect( e.getX(), (int) (rectangle.getY() + rectangle.getHeight()) );
            }
            return;
        }

        if ( SwingUtilities.isRightMouseButton( e ) ) {
            if ( getSelectedTextTrimmed().length() > 0 ) {
                showMenuOnSelect( e.getX(), e.getY() );
            } else {
                menu.show( articlePanel, e.getX(), e.getY() );
            }
        }
    }

    private void showMenuOnSelect( int x, int y ) {
        menuOnSelect.show( articlePanel, x, y, getSelectedTextTrimmed() );
    }

    public void keyTypedOnEditorPane( KeyEvent e ) {
        if ( KeyEvent.VK_ESCAPE == e.getKeyChar() ) {
            model.requestFocusInNavigator();
        }
    }

    public void popOut() {
        model.popOut( articlePanel.getArticle(), articlePanel.getHighlight() );
    }
    public void doSaveAs() {
        model.export( articlePanel.getArticle() );
    }

    public void copySelected() {
        //articlePanel.getEditorPane().copy(); // TODO JEditorPane.copy() doesn't work. Why? Tested with 1.5.0_05.
        StringSelection selection = new StringSelection( articlePanel.getEditorPane().getSelectedText() );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( selection, selection );
    }
    public void copyIntoSearchField() {
        model.navigate( getSelectedTextTrimmed() );
    }
    public void translateSelected() {
        model.navigateAndTranslate( getSelectedTextTrimmed() );
    }
    public void popOutSelected() {
        model.popOut( getSelectedTextTrimmed() );
    }
    public void translateFirstSelected() {
        model.navigateAndTranslate( engine.getFinder().findFirstStartsWith( getSelectedTextTrimmed() ).getTitle() );
    }
    public void suggestSelected() {
        model.suggest( getSelectedTextTrimmed() );
    }

    public JComponent getGui() {
        return cardPanel.getGui();
    }

    private void find( String searchText, boolean fromStart, boolean forwardDirection, boolean caseSensetive, boolean wholeWordsOnly ) {
        JEditorPane editorPane = articlePanel.getEditorPane();

        String article;
        try {
            article = editorPane.getDocument().getText( 0, editorPane.getDocument().getLength() );
        } catch ( BadLocationException e ) {
            throw States.shouldNeverReachHere( e );
        }

        if ( fromStart ) {
            editorPane.select( 0, 0 );
        }

        // try search twice: after caret, before caret (the order depends on search direction)
        int attempt1 = forwardDirection ? editorPane.getSelectionEnd() : editorPane.getSelectionStart() - 1;
        int attempt2 = forwardDirection ? 0 : article.length();

        int index = TextSearcher.indexOf( forwardDirection, article, searchText, attempt1, caseSensetive, wholeWordsOnly );
        if ( index == -1 ) {
            index = TextSearcher.indexOf( forwardDirection, article, searchText, attempt2, caseSensetive, wholeWordsOnly );

            if ( index == -1 ) {
                // reset selction
                editorPane.select( 0, 0 );
                model.find_sendFeedback( false );
                return;
            }
        }

        // highlight on success
        editorPane.select( index, index + searchText.length() );
        editorPane.getCaret().setSelectionVisible( true );
        model.find_sendFeedback( true );
    }

    private String getClosestArticleTitleFor( String title ) {
        IArticle article = engine.getFinder().findFirstLike( title );
        return article == null ? "" : article.getTitle();
    }

    private String getSelectedTextTrimmed() {
        String text = articlePanel.getEditorPane().getSelectedText();
        return text != null ? text.trim() : "";
    }
}
