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

import ja.lingo.application.gui.main.describer.panels.ArticlePanel;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.MenuItems;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.beans.IArticle;

import javax.swing.*;

class DescriberMenuOnSelect {
    private IEngine engine;

    private JPopupMenu menu;

    private JMenuItem translateItem;
    private JMenuItem popOutItem;
    private JMenuItem copyItem;
    private JMenuItem copyIntoSearchField;
    private JMenuItem translateFirstItem;
    private JMenuItem suggestItem;

    public DescriberMenuOnSelect( IEngine engine ) {
        this.engine = engine;

        translateItem = MenuItems.translateNoAccel();
        popOutItem = MenuItems.popOutNoAccel();
        translateFirstItem = MenuItems.translateFirstNoAccel();
        suggestItem = MenuItems.suggestNoAccel();

        copyItem = MenuItems.copyNoAccel();
        copyIntoSearchField = MenuItems.copyIntoSearchField();

        menu = Components.popupMenu();
        menu.add( translateItem );
        menu.add( popOutItem );
        menu.add( translateFirstItem );
        menu.add( suggestItem );
        menu.addSeparator();
        menu.add( copyItem );
        menu.add( copyIntoSearchField );
    }

    public void show( ArticlePanel articlePanel, int x, int y, String title ) {
        boolean isArticleAvailable = engine.getFinder().contains( title );

        translateItem.setVisible( isArticleAvailable );
        popOutItem.setVisible( isArticleAvailable );

        if ( isArticleAvailable ) {
            translateFirstItem.setVisible( false );
        } else {
            IArticle article = engine.getFinder().findFirstStartsWith( title );

            if ( article != null ) {
                translateFirstItem.setText( article.getTitle() );
                translateFirstItem.setVisible( true );
            } else {
                translateFirstItem.setVisible( false );
            }
        }

        menu.show( articlePanel.getEditorPane(), x, y );
    }
}
