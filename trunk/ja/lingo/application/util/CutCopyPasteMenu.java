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

package ja.lingo.application.util;

import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.centre.util.assertions.Arguments;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;

class CutCopyPasteMenu {
    @NListener( type = MouseListener.class, mappings = "mouseClicked > onMouseClicked")
    private JTextComponent component;

    private JPopupMenu menu;

    @NListener(type = ActionListener.class, mappings = "actionPerformed > onCut")
    private JMenuItem cutItem;

    @NListener(type = ActionListener.class, mappings = "actionPerformed > onCopy")
    private JMenuItem copyItem;

    @NListener(type = ActionListener.class, mappings = "actionPerformed > onPaste")
    private JMenuItem pasteItem;


    public CutCopyPasteMenu( JTextComponent component ) {
        Arguments.assertNotNull( "component", component );
        this.component = component;

        cutItem = MenuItems.cut();
        copyItem = MenuItems.copy();
        pasteItem = MenuItems.paste();

        menu = Components.popupMenu();
        menu.add( cutItem );
        menu.add( copyItem );
        menu.add( pasteItem );

        ActionBinder.bind( this );
    }

    public void onCut() {
        component.cut();
    }
    public void onCopy() {
        component.copy();
    }
    public void onPaste() {
        component.paste();
    }

    public void onMouseClicked( MouseEvent e ) {
        if ( !SwingUtilities.isRightMouseButton( e ) ) {
            return;
        }

        // select all if was not focused
        if ( !component.hasFocus() ) {
            component.requestFocus();
            component.selectAll();
        }

        // select all if empty read-only
        if ( component.getSelectedText() == null && !component.isEditable() ) {
            component.selectAll();
        }

        boolean readAccess = component.getSelectedText() != null;
        boolean writeAccess = component.isEnabled() && component.isEditable();

        cutItem.setEnabled( readAccess && writeAccess );
        copyItem.setEnabled( readAccess );
        pasteItem.setEnabled( writeAccess && Toolkit.getDefaultToolkit().getSystemClipboard()
                .isDataFlavorAvailable( DataFlavor.stringFlavor ) );

        // not read-ony, no write
        if ( readAccess || writeAccess ) {
            menu.show( component, e.getX(), e.getY() );
        }
    }
}
