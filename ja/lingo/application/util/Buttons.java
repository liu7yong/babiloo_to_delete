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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Buttons {
    private static final Resources resources = Resources.forProperties( Buttons.class );

    private Buttons() {
    }

    // various (text)
    public static JButton cancel()              { return text( "cancel" ); }
    public static JButton close()               { return text( "close" ); }
    public static JButton continue1()           { return text( "continue" ); }

    public static JButton stop()                { return textAcc( "stop" ); }
    public static JButton search()              { return textAcc( "search" ); }
    public static JButton searchNew()           { return textAcc( "searchNew"); }

        // various
    public static JButton exportAllToHtml()     { return toolBar( "exportAllToHtml" ); }
    public static JButton gc()                  { return toolBar( "gc" ); }

    public static JButton closeCross()          { return toolBar( "closeCross" ); }


    // find
    public static JButton findOptions()         { return toolBar( "findOptions" ); }
    public static JButton previous()            { return toolBar( "previous" ); }
    public static JButton next()                { return toolBar( "next" ); }

    // edit
    public static JButton add()                 { return toolBar( "add" ); }
    public static JButton remove()              { return toolBar( "remove" ); }
    public static JButton moveDown()            { return toolBar( "moveDown" ); }
    public static JButton moveUp()              { return toolBar( "moveUp" ); }

    private static JButton text( String key ) {
        JButton button = new JButton();
        button.setText( resources.text( key ) );
        return button;
    }
    private static JButton textAcc( String key ) {
        JButton button = text( key );
        button.setMnemonic( resources.stroke( key ).getKeyCode() );
        return button;
    }

    private static JButton toolBar( String key ) {
        final JButton button = new JButton();
        button.setFocusable( false );
        button.setIcon( resources.icon( key ) );
        button.setToolTipText( resources.text( key )
                + calculateStroke( resources.stroke( key ) ) );

        // TODO refactor with Actions.initializeButton
        button.registerKeyboardAction( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // spread event
                for ( ActionListener listener : button.getActionListeners() ) {
                    listener.actionPerformed( new ActionEvent( button, 0, "" ) );
                }
            }
        }, resources.stroke( key ), JComponent.WHEN_IN_FOCUSED_WINDOW );

        return button;
    }

    public static String calculateStroke( KeyStroke keyStroke ) {
        String accText = KeyEvent.getKeyModifiersText( keyStroke.getModifiers() );
        if ( accText.length() > 0 ) {
            accText += "+";
        }
        accText = " (" + accText + KeyEvent.getKeyText( keyStroke.getKeyCode() ) + ")";
        return accText;
    }
}
