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

package ja.centre.gui.mediator;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class WindowDraggerMediator {
    private int pressedX;
    private int pressedY;

    /**
     * Delegates drags of 'component' to its parent window.<br>
     * The result is that 'component' acts as window title.<br>
     * <br>
     * In details:<br>
     * 1. Finds first 'java.awt.Window' in parents of 'component' and remembers it.<br>
     * 2. Listens drags of 'component' and moves the window with the same (dx,dy)<br>
     *
     * @param component component, which should be listen for drags
     * @throws IllegalArgumentException if there are no parent of type 'java.awt.Window'.
     *                                  E.g. when component were not added to any window.
     * @throws NullPointerException     if 'component' is null.
     */
    public WindowDraggerMediator( final Component component ) {
        Arguments.assertNotNull( "component", component );

        final Window window = SwingUtilities.getWindowAncestor( component );

        States.assertNotNull( window, "There is no window ancestor. "
                + "Make sure you have added the component to a Window (JDialog, JFrame etc.)" );

        component.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if ( !SwingUtilities.isLeftMouseButton( e ) ) {
                    return;
                }

                pressedX = e.getX();
                pressedY = e.getY();
            }
        } );
        component.addMouseMotionListener( new MouseMotionAdapter() {
            public void mouseDragged( MouseEvent e ) {
                if ( !SwingUtilities.isLeftMouseButton( e ) ) {
                    return;
                }

                Point location = MouseInfo.getPointerInfo().getLocation();


                window.setLocation(
                        /*e.getX() + window.getX()*/ (int) location.getX() - pressedX,
                        /*e.getY() + window.getY()*/ (int) location.getY() - pressedY );

                e.consume();
            }
        } );
    }
}
