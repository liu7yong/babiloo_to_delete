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

package ja.lingo.application.gui.dropzone;

import ja.lingo.application.gui.actions.Actions;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.MenuItems;

import javax.swing.*;

class DropZoneMenu {
    private JPopupMenu menu;

    private JMenuItem showOrHideMainItem;
    private JMenuItem exitItem;

    public DropZoneMenu( Actions actions ) {
        showOrHideMainItem = MenuItems.showOrHideMain();
        exitItem = MenuItems.exit();

        menu = Components.popupMenu();
        menu.add( showOrHideMainItem );
        menu.addSeparator();
        menu.add( actions.getDropZoneHideAction().item() );
        menu.addSeparator();
        menu.add( actions.getPasteAndTranslateAction().item() );
        menu.add( actions.getSettingsShowAction().item() );
        menu.addSeparator();
        menu.add( exitItem );
    }

    public void show( JComponent invoker, int x, int y ) {
        menu.show( invoker, x, y );
    }
}
