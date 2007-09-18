package ja.centre.gui.util;

import ja.centre.util.assertions.Arguments;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class SelectAllOnEscapeListener extends KeyAdapter {
    private static KeyAdapter INSTANCE = new SelectAllOnEscapeListener();

    public static void register( JTextField field ) {
        if ( Arrays.asList( field.getKeyListeners() ).contains( INSTANCE ) ) {
            Arguments.doThrow( "SelectAllOnEscapeListener is already attached to this JTextField" );
        }

        field.addKeyListener( INSTANCE );
    }

    public void keyPressed( KeyEvent e ) {
        if ( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
            ((JTextField) e.getSource()).selectAll();
        }
    }
}
