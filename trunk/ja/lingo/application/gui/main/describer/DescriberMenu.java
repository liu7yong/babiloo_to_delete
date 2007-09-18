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

import ja.lingo.application.gui.actions.Actions;
import ja.lingo.application.gui.main.describer.panels.ArticlePanel;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.MenuItems;

import javax.swing.*;

class DescriberMenu {
    private JPopupMenu menu;

    private JMenuItem saveAsItem;
    private JMenuItem popOutItem;

    public DescriberMenu( Actions actions ) {
        popOutItem = MenuItems.popOutNoAccel();
        saveAsItem = MenuItems.saveAs();

        menu = Components.popupMenu();
        menu.add( actions.getHistoryBackAction().item() );
        menu.add( actions.getHistoryForwardAction().item() );
        menu.addSeparator();
        menu.add( popOutItem );
        menu.addSeparator();
        menu.add( saveAsItem );
    }

    public void show( ArticlePanel articlePanel, int x, int y ) {
        menu.show( articlePanel.getEditorPane(), x, y );
    }
}
