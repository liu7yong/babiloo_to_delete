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

import info.clearthought.layout.TableLayout;
import ja.centre.gui.concurrent.EdtWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

class ProgressComponent {
    private JLabel label;
    private JProgressBar progressBar;

    private JPanel gui;

    private ITitledMonitor controller;

    public ProgressComponent() {
        label = new JLabel();

        progressBar = new JProgressBar();
        progressBar.setStringPainted( true );

        final IMonitor delegate = new ProgressBarMonitor( progressBar );

        controller = (ITitledMonitor) EdtWrapper.nonWaiting( new ITitledMonitor() {
            public void setTitle( String title ) {
                label.setText( title );
            }
            public void setText( String text ) {
                progressBar.setString( text );
            }

            public void start( int minimum, int maximum ) {
                delegate.start( minimum, maximum );
            }
            public void update( int value ) {
                delegate.update( value );
            }
            public void finish() {
                delegate.finish();
            }
        } );

        gui = new JPanel() {
            public Dimension getPreferredSize() {
                return new Dimension( 300, (int) super.getPreferredSize().getHeight() );
            }

            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D) g;

                Rectangle bounds = getBounds();

                RoundRectangle2D.Double round = new RoundRectangle2D.Double( 0, 0,
                        bounds.getWidth() - 1, bounds.getHeight() - 1,
                        10, 10 );

                Object antialiasing = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

                g2.setColor( getBackground() );
                g2.fill( round );
                g2.setColor( Color.GRAY );
                g2.draw( round );

                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, antialiasing );
            }
        };

        gui.setLayout( new TableLayout( new double[][] {
                { TableLayout.FILL },
                { TableLayout.PREFERRED, 5, TableLayout.PREFERRED },
        } ) );

        gui.add( label, "0, 0" );
        gui.add( progressBar, "0, 2" );

        gui.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        gui.setOpaque( false );
    }

    public JComponent getGui() {
        return gui;
    }

    public ITitledMonitor getController() {
        return controller;
    }

    public static void main( String[] args ) {
        ProgressComponent progress = new ProgressComponent();

        JPanel content = new JPanel( new TableLayout( new double[][] {
                { TableLayout.FILL },
                { TableLayout.FILL }
        }) );
        content.add( progress.getGui(), "0, 0, center, center" );
        content.setBackground( Color.RED );

        JFrame frame = new JFrame( "123" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( content );
        frame.setSize( 400, 100 );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );

        progress.getController().setTitle( "123" );
    }
}

