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

import ja.centre.gui.resources.Resources;

import javax.swing.*;

public class MenuItems {
    private static final Resources RESOURCES = Resources.forProperties( MenuItems.class );

    private MenuItems() {
    }

    public static JMenuItem cut()               { return createTextIconAccelerator( "cut" ); }
    public static JMenuItem copy()              { return createTextIconAccelerator( "copy" ); }
    public static JMenuItem copyNoAccel()       { return createTextIcon( "copy" ); }
    public static JMenuItem paste()             { return createTextIconAccelerator( "paste" ); }

    public static JMenuItem copyIntoSearchField() { return createText( "copyIntoSearchField"); }

    public static JMenuItem saveAs()            { return createTextIcon( "saveAs" ); }
    public static JMenuItem saveAll()           { return createTextIcon( "saveAll" ); }
    public static JMenuItem clearHistory()      { return createTextIcon( "clearHistory" ); }

    public static JMenuItem translate()         { return createTextIconAccelerator( "translate" ); }
    public static JMenuItem translateNoAccel()  { return createTextIcon( "translate" ); }
    public static JMenuItem popOut()            { return createTextIconAccelerator( "popOut" ); }
    public static JMenuItem popOutNoAccel()     { return createTextIcon( "popOut" ); }
    public static JMenuItem translateFirstNoAccel() { return createTextIcon( "translateFirst" ); }
    public static JMenuItem suggest()           { return createTextIconAccelerator( "suggest" ); }
    public static JMenuItem suggestNoAccel()    { return createTextIcon( "suggest" ); }

    // drop zone
    public static JMenuItem showOrHideMain()    { return createText( "showOrHideMain" ); }
    public static JMenuItem exit()              { return createText( "exit" ); }

    private static JMenuItem createTextIconAccelerator( String key ) {
        return setText( key, setIcon( key, setAccelerator( key, new JMenuItem() ) ) );
    }
    private static JMenuItem createTextIcon( String key ) {
        return setText( key, setIcon( key, new JMenuItem() ) );
    }
    private static JMenuItem createText( String key ) {
        JMenuItem menuItem = setText( key, new JMenuItem() );
        menuItem.setIcon( RESOURCES.icon( "transparent") );
        return menuItem;
    }

    private static JMenuItem setText( String key, JMenuItem item ) {
        item.setText( RESOURCES.text( key ) );
        return item;
    }
    private static JMenuItem setIcon( String key, JMenuItem item ) {
        item.setIcon( RESOURCES.icon( key ) );
        return item;
    }
    private static JMenuItem setAccelerator( String key, JMenuItem item ) {
        item.setAccelerator( RESOURCES.stroke( key ) );
        return item;
    }

}
