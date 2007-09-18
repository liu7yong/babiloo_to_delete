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

package ja.lingo.application.gui.actions;

import ja.centre.gui.resources.Resources;
import ja.lingo.application.util.Buttons;
import ja.lingo.application.util.Components;

import javax.swing.*;

public abstract class AAction extends AbstractAction implements IGuiAction {
    private Resources resources = Resources.forProperties( getClass() );

    private boolean hasAccelerator;

    protected AAction() {
        this( true );
    }
    protected AAction( boolean hasAccelerator ) {
        this.hasAccelerator = hasAccelerator;
    }

    public JButton button() {
        return initializeButton( new JButton(), false );
    }

    public JButton buttonLarge() {
        return initializeButton( new JButton(), true );
    }

    public JMenuItem item() {
        JMenuItem item = new JMenuItem( this );
        item.setText( resources.text( "action" ) );
        item.setIcon( resources.icon( "action_small" ) );
        if ( hasAccelerator ) {
            item.setAccelerator( resources.stroke( "action" ) );
        }
        return item;
    }

    public JLabel rollover() {
        return Components.labelRollover(
                resources.text( "action" ),
                resources.icon( "action_large" ),
                this );
    }

    private <T extends AbstractButton> T initializeButton( T button, boolean useLargeIcon ) {
        button.setAction( this );
        button.setFocusable( false );
        button.setToolTipText( resources.text( "action" )
                + ( hasAccelerator ? getAcceleratorText() : "" ) );
        button.setIcon( resources.icon( useLargeIcon ? "action_large" : "action" ) );

        if ( hasAccelerator ) {
            // TODO refactor with Buttons.createToolBar
            String actionName = getClass().getName();
            button.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( resources.stroke( "action" ), actionName );
            button.getActionMap().put( actionName, this );
        }
        return button;
    }

    private String getAcceleratorText() {
        return Buttons.calculateStroke( resources.stroke( "action" ) );
    }
}
