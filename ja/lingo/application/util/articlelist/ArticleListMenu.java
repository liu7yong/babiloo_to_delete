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

package ja.lingo.application.util.articlelist;

import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.lingo.application.model.Model;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.MenuItems;
import ja.lingo.engine.beans.IArticle;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ArticleListMenu {
    @NListener( type = MouseListener.class, mappings = "mousePressed > mousePressedOnList" )
    private JList list;

    private JPopupMenu menu;

    @NListener( type = ActionListener.class, mappings = "actionPerformed > translateSelected" )
    private JMenuItem translateItem;

    @NListener( type = ActionListener.class, mappings = "actionPerformed > popOutSelected" )
    private JMenuItem popOutItem;

    @NListener( type = ActionListener.class, mappings = "actionPerformed > suggest" )
    private JMenuItem suggestItem;

    @NListener( type = ActionListener.class, mappings = "actionPerformed > copySelected" )
    private JMenuItem copyItem;

    @NListener( type = ActionListener.class, mappings = "actionPerformed > saveAs" )
    private JMenuItem saveAsItem;

    @NListener( type = ActionListener.class, mappings = "actionPerformed > saveAll" )
    private JMenuItem saveAllItem;

    private JPopupMenu naMenu;

    @NListener( type = ActionListener.class, mappings = "actionPerformed > copySelected" )
    private JMenuItem naCopyItem;

    private Model model;
    private String highlight;

    private boolean bookmark;

    public ArticleListMenu( JList list, Model model, boolean enableAccels, boolean bookmark ) {
        this.list = list;
        this.model = model;

        this.bookmark = bookmark;

        translateItem = enableAccels ? MenuItems.translate() : MenuItems.translateNoAccel();
        popOutItem = enableAccels ? MenuItems.popOut() : MenuItems.popOutNoAccel();
        suggestItem = enableAccels ? MenuItems.suggest() : MenuItems.suggestNoAccel();

        copyItem = MenuItems.copyNoAccel();

        saveAsItem = MenuItems.saveAs();
        saveAllItem = MenuItems.saveAll();

        menu = Components.popupMenu();
        menu.add( translateItem );
        menu.add( popOutItem );
        menu.add( suggestItem );
        menu.addSeparator();
        menu.add( copyItem );
        menu.addSeparator();
        menu.add( saveAsItem );
        menu.add( saveAllItem );

        // build menu for non-available article
        naCopyItem = MenuItems.copyNoAccel();
        naMenu = Components.popupMenu();
        naMenu.add( naCopyItem );

        ActionBinder.bind( this );
    }

    public void mousePressedOnList( MouseEvent e ) {
        if ( SwingUtilities.isRightMouseButton( e ) ) {
            JList list = (JList) e.getSource();
            int index = list.locationToIndex( e.getPoint() );
            if ( index != -1 ) {
                list.setSelectedIndex( index );

                if ( getSelectedArticle() != null ) {
                    menu.show( list, e.getX(), e.getY() );
                } else {
                    naMenu.show( list, e.getX(), e.getY() );
                }
            }
        }
    }

    public void copySelected() {
        StringSelection selection = new StringSelection( getSelectedArticleTitle() );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( selection, selection );
    }
    public void translateSelected() {
        if ( getSelectedArticle() == null ) {
            model.translateNotFound( getSelectedArticleTitle() );
        } else {
            if ( bookmark ) {
                model.bookmark( getSelectedArticle() );
            }
            model.navigate( getSelectedArticleTitle() );
            model.translate( getSelectedArticle(), highlight );
        }
    }
    public void popOutSelected() {
        if ( getSelectedArticle() == null ) {
            model.translateNotFound( getSelectedArticleTitle() );
        } else {
            model.popOut( getSelectedArticle(), highlight );
        }
    }
    public void suggest() {
        model.suggest( getSelectedArticleTitle() );
    }
    public void saveAs() {
        model.export( getSelectedArticle() );
    }
    public void saveAll() {
        model.export( ( (IArticleListMenuSupport) list.getModel() ).getArticleListForExport() );
    }

    private String getSelectedArticleTitle() {
        // TODO remove toString hack?
        return list.getSelectedValue().toString();
    }
    private IArticle getSelectedArticle() {
        return ( (IArticleListMenuSupport) list.getModel() ).getSelectedArticle();
    }

    public void addSeparator() {
        menu.addSeparator();
    }
    public void add( JMenuItem item ) {
        menu.add( item );
    }
    public void setHighlight( String highlight ) {
        this.highlight = highlight;
    }
}
