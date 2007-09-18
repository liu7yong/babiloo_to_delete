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

import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.centre.gui.actionbinder.config.NListenerGroup;
import ja.centre.gui.mediator.WindowDraggerMediator;
import ja.centre.gui.resources.Resources;
import ja.lingo.application.gui.actions.Actions;
import ja.lingo.application.gui.drophandler.DropHandler;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.model.Preferences;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class DropZoneGui {
    private Resources resources = ja.centre.gui.resources.Resources.forProperties( getClass() );

    private JDialog dialog;
    private Model model;

    @NListenerGroup( {
        @NListener( type = MouseListener.class, mappings = {
                "mouseClicked > mouseClicked",
                "mousePressed > mousePressed"
        } ),
        @NListener( type = MouseMotionListener.class, mappings = {
                "mouseDragged > mouseDragged"
        } )
    } )
    private JLabel gui;

    private boolean wasDragged;

    @NListenerGroup( {
        @NListener( property = "showOrHideMainItem",    type = ActionListener.class, mappings = "actionPerformed > showOrHideMain" ),
        @NListener( property = "exitItem",              type = ActionListener.class, mappings = "actionPerformed > exit" )
    } )
    private DropZoneMenu menu;

    public DropZoneGui( Model model, Actions actions, DropHandler dropHandler ) {
        this.model = model;

        this.model.addApplicationModelListener( new ModelAdapter() {
            public void initialize( Preferences preferences ) {
                dialog.setLocation( preferences.getDropZoneLocation() );
                if ( dialog.getX() == -1 && dialog.getY() == -1 ) {
                    dialog.setLocationRelativeTo( null );
                }
            }
            public void dropzone_hideTemporary() {
                dialog.setVisible( false );
            }
            public void settingsUpdated( Preferences preferences ) {
                dialog.setVisible( preferences.isDropZoneVisible() );
            }
            public void dispose( Preferences preferences ) {
                preferences.setDropZoneLocation( dialog.getLocation() );
                preferences.setDropZoneVisible( dialog.isVisible() );
            }
        } );

        gui = new JLabel();
        gui.setIcon( resources.icon( "flyer" ) );
        gui.setToolTipText( resources.text( "flyer.tooltip" ) );
        gui.setTransferHandler( dropHandler );

        // NOTE: workaround against coffee-cup JDialog icon
        JFrame dummyFrame = new JFrame();
        dummyFrame.setIconImage( resources.icon( "flyer" ).getImage() );

        dialog = new JDialog( dummyFrame );
        dialog.setTitle( resources.text( "flyer" ) );
        dialog.setContentPane( gui );
        dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );

        new WindowDraggerMediator( gui );

        // TODO how to make it translucent?
        //dialog.setBackground( new Color( 100, 100, 100, 100 ) );

        dialog.setAlwaysOnTop( true );
        dialog.setUndecorated( true );
        dialog.pack();

        menu = new DropZoneMenu( actions );

        ActionBinder.bind( this );
    }

    public JComponent getGui() {
        return gui;
    }

    // NOTE: workaround against "a bit dragged = dragged + clicked events" nuance
    public void mouseClicked( MouseEvent e ) {
        if ( SwingUtilities.isLeftMouseButton( e ) ) {
            if ( wasDragged ) {
                return;
            }
            showOrHideMain();
        }

        if ( SwingUtilities.isRightMouseButton( e ) ) {
            menu.show( gui, e.getX(), e.getY() );
        }
    }

    public void mousePressed() {
        wasDragged = false;
    }
    public void mouseDragged() {
        wasDragged = true;
    }

    public void showOrHideMain() {
        model.main_showOrHide();
    }
    public void exit() {
        model.dispose();
    }
}
