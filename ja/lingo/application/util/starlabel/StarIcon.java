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

package ja.lingo.application.util.starlabel;

import ja.centre.gui.mediator.WindowDraggerMediator;
import ja.lingo.application.util.JaLingoColors;
import ja.lingo.application.util.starlabel.figures.IFigure;
import ja.lingo.application.util.starlabel.figures.StarWhirlFigure;
import ja.lingo.application.util.starlabel.figures.WaveUpFigure;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class StarIcon {
    private int iconEdge;
    private int iconEdgeHalf;

    public StarIcon( int iconEdge ) {
        this.iconEdge = iconEdge;
        this.iconEdgeHalf = iconEdge / 2;
    }

    void drawIcon( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        // bakcground
        g2.setColor( JaLingoColors.COLOR );
        g2.fillRect( 0, 0, iconEdge, iconEdge );

        // wave
        g2.setPaint( StarLabel.calculateWaveGradient( iconEdge ) );
        WaveUpFigure wave = new WaveUpFigure();
        wave.transform( AffineTransform.getScaleInstance( iconEdge * 2, iconEdge ) );

        AffineTransform transform = g2.getTransform();
        g2.transform( AffineTransform.getTranslateInstance( -iconEdge / 2, iconEdge * 2 / 3 ) );
        wave.draw( g2 );
        g2.setTransform( transform );

        // stars
        IFigure whirl = new StarWhirlFigure();
        whirl.transform( AffineTransform.getScaleInstance( iconEdge, iconEdge ) );
        whirl.transform( AffineTransform.getTranslateInstance( iconEdgeHalf * 0.9, iconEdgeHalf * 0.9 ) );
        whirl.draw( g2 );

        // border
        g2.setColor( JaLingoColors.COLOR );
        g2.drawRect( 0, 0, iconEdge - 1, iconEdge - 1 );
    }

    public static void main( String[] args ) {
        int iconEdge1 = 32;

        final StarIcon starIcon = new StarIcon( iconEdge1 );
        final JComponent component = new JComponent() {
            protected void paintComponent( Graphics g ) {
                starIcon.drawIcon( g );
            }
        };
        component.setPreferredSize( new Dimension( iconEdge1, iconEdge1 ) );

        JFrame frame = new JFrame( "JaLingo" );
        frame.setContentPane( component );
        frame.setUndecorated( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null );
        frame.pack();
        frame.setVisible( true );

        new WindowDraggerMediator( component );
    }
}
