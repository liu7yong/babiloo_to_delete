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

package ja.centre.gui.util;

import ja.centre.util.assertions.Arguments;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.IdentityHashMap;

public class CardPanel implements IGui {
    private JPanel panel = new JPanel( new CardLayout() );

    private Map<Object, String> componentToName = new IdentityHashMap<Object, String>();
    private int counter;

    public void add( JComponent c ) {
        Arguments.assertNotNull( "c", c );
        panel.add( registerNameFor( c ), c );
    }
    public void show( JComponent c ) {
        Arguments.assertNotNull( "c", c );

        String name = componentToName.get( c );

        if ( name == null ) {
            Arguments.doThrow( "Could not find CardLayout ID for component instance of class \""
                    + c.getClass().getName() + "\". Ensure that you added it before." );
        }

        ((CardLayout) panel.getLayout()).show( panel, name );
    }

    public void add( IGui gui ) {
        Arguments.assertNotNull( "gui", gui );
        add( gui.getGui() );
    }
    public void show( IGui gui ) {
        Arguments.assertNotNull( "gui", gui );
        show( gui.getGui() );
    }

    public JComponent getGui() {
        return panel;
    }

    private String registerNameFor( JComponent c ) {
        String name = "card_" + counter++;
        componentToName.put( c, name );
        return name;
    }
}
