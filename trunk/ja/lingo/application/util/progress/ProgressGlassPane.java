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

package ja.lingo.application.util.progress;

import ja.centre.util.assertions.Arguments;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProgressGlassPane extends JComponent {
    private static final MouseListener STUB_MOUSE_LISTENER = new MouseAdapter() {};
    private static final MouseMotionListener STUB_MOUSE_MOTION_LISTENER = new MouseMotionAdapter() {};
    private static final KeyListener STUB_KEY_LISTENER = new KeyAdapter() {};

    private static final Color BACKGROUND = new Color( 255, 255, 255, 150 );

    private Component oldGlassPane;
    private WindowListener[] oldWindowListeners;
    private int oldDefaultCloseOperation;

    public ProgressGlassPane( Component content ) {
        Arguments.assertNotNull( "content", content );

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        setLayout( new GridBagLayout() );

        add( content );
    }

    public void setVisible( boolean newVisible ) {
        setOpaque( false );
        if ( newVisible ) {
            addMouseListener( STUB_MOUSE_LISTENER );
            addMouseMotionListener( STUB_MOUSE_MOTION_LISTENER );
            addKeyListener( STUB_KEY_LISTENER );

            //timer.start();
        } else {
            //timer.stop();

            removeMouseListener( STUB_MOUSE_LISTENER );
            removeMouseMotionListener( STUB_MOUSE_MOTION_LISTENER );
            removeKeyListener( STUB_KEY_LISTENER );
        }
        super.setVisible( newVisible );
    }

    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor( BACKGROUND );
        g2.fill( getBounds() );
    }

    public Component getOldGlassPane() {
        return oldGlassPane;
    }
    public void setOldGlassPane( Component oldGlassPane ) {
        this.oldGlassPane = oldGlassPane;
    }

    public WindowListener[] getOldWindowListeners() {
        return oldWindowListeners;
    }
    public void setOldWindowListeners( WindowListener[] oldWindowListeners ) {
        this.oldWindowListeners = oldWindowListeners;
    }

    public int getOldDefaultCloseOperation() {
        return oldDefaultCloseOperation;
    }
    public void setOldDefaultCloseOperation( int oldDefaultCloseOperation ) {
        this.oldDefaultCloseOperation = oldDefaultCloseOperation;
    }

    public static void install( JFrame frame, Component content ) {
        install( new FrameAdapter( frame ), content );
    }
    public static void install( JDialog dialog, Component content ) {
        install( new DialogAdapter( dialog ), content );
    }

    public static void install( IWindowAdapter window, Component content ) {
        ProgressGlassPane pane = new ProgressGlassPane( content );
        pane.setOldGlassPane( window.getGlassPane() );
        pane.setOldWindowListeners( window.getWindowListeners() );
        pane.setOldDefaultCloseOperation( window.getDefaultCloseOperation() );

        for ( WindowListener windowListener : pane.getOldWindowListeners() ) {
            window.removeWindowListener( windowListener );
        }

        window.setGlassPane( pane );
        window.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        pane.setVisible( true );
    }


    public static void restore( IWindowAdapter window ) {
        ProgressGlassPane pane = (ProgressGlassPane) window.getGlassPane();
        pane.setVisible( false );

        window.setGlassPane( pane.getOldGlassPane() );
        window.setDefaultCloseOperation( pane.getOldDefaultCloseOperation() );
        for ( WindowListener windowListener : pane.getOldWindowListeners() ) {
            window.addWindowListener( windowListener );
        }
    }


    public static void restore( JDialog dialog ) {
        stop( new DialogAdapter( dialog ) );
    }
    public static void restore( JFrame frame ) {
        stop( new FrameAdapter( frame ) );
    }

    private static void stop( final ProgressGlassPane.IWindowAdapter window ) {
        if ( SwingUtilities.isEventDispatchThread() ) {
            stop0( window );
        } else {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    stop0( window );
                }
            } );
        }
    }

    private static void stop0( ProgressGlassPane.IWindowAdapter window ) {
        ProgressGlassPane.restore( window );
    }

    private static interface IWindowAdapter {
        void setGlassPane( Component glassPane );
        Component getGlassPane();

        int getDefaultCloseOperation();
        void setDefaultCloseOperation( int i );

        WindowListener[] getWindowListeners();
        void addWindowListener( WindowListener listener );
        void removeWindowListener( WindowListener listener );
    }

    private static class DialogAdapter implements IWindowAdapter {
        private JDialog dialog;

        public DialogAdapter( JDialog dialog ) {
            Arguments.assertNotNull( "dialog", dialog );
            this.dialog = dialog;
        }

        public int getDefaultCloseOperation() {
            return dialog.getDefaultCloseOperation();
        }
        public void setDefaultCloseOperation( int operation ) {
            dialog.setDefaultCloseOperation( operation );
        }

        public Component getGlassPane() {
            return dialog.getGlassPane();
        }
        public void setGlassPane( Component glassPane ) {
            dialog.setGlassPane( glassPane );
        }

        public WindowListener[] getWindowListeners() {
            return dialog.getWindowListeners();
        }
        public void addWindowListener( WindowListener l ) {
            dialog.addWindowListener( l );
        }
        public void removeWindowListener( WindowListener l ) {
            dialog.removeWindowListener( l );
        }
    }
    private static class FrameAdapter implements IWindowAdapter {
        private JFrame frame;

        public FrameAdapter( JFrame frame ) {
            Arguments.assertNotNull( "frame", frame );
            this.frame = frame;
        }

        public int getDefaultCloseOperation() {
            return frame.getDefaultCloseOperation();
        }
        public void setDefaultCloseOperation( int operation ) {
            frame.setDefaultCloseOperation( operation );
        }

        public Component getGlassPane() {
            return frame.getGlassPane();
        }
        public void setGlassPane( Component glassPane ) {
            frame.setGlassPane( glassPane );
        }

        public WindowListener[] getWindowListeners() {
            return frame.getWindowListeners();
        }
        public void addWindowListener( WindowListener l ) {
            frame.addWindowListener( l );
        }
        public void removeWindowListener( WindowListener l ) {
            frame.removeWindowListener( l );
        }
    }
}
