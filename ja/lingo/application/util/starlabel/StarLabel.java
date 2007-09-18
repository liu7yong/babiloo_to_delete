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

import ja.lingo.application.util.JaLingoColors;
import ja.lingo.application.util.starlabel.figures.IFigure;
import ja.lingo.application.util.starlabel.figures.WaveDownFigure;
import ja.lingo.application.util.starlabel.figures.WaveUpFigure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class StarLabel extends JLabel {
    private static final Color COLOR_BACKGROUND = JaLingoColors.COLOR;

    private static final float SHADOW_DISTANCE = 1f;

    private static final Color COLOR_TITLE_SHADOW = new Color( 0x88000000, true );
    private static final Color COLOR_TITLE = Color.WHITE;

    static final int WAVE_WIDTH = 250;
    static final int WAVE_HEIGHT = 110;

    public static final GradientPaint WAVE_GRADIENT = calculateWaveGradient( WAVE_HEIGHT );

    private int frame;
    private IFigure waveUp;
    private IFigure waveDown;

    private static final int STAR_COUNT = 5;
    private static final int STAR_FRAME_COUNT = 30;

    private StarAnimation textStarAnimation;
    private StarSprite_Text[] stars;

    private TextLayout textLayout;
    private Shape textShape;
    private Timer timer;
    private float textYOffset;

    private boolean alignToCenter;

    public StarLabel( String text, boolean alignToCenter ) {
        if ( text.trim().length() == 0 ) {
        } else {
            textStarAnimation = new StarAnimation_RotationScale( STAR_FRAME_COUNT );
            stars = new StarSprite_Text[STAR_COUNT];

            textLayout = new TextLayout( text,
                    new Font( "Tahoma", Font.PLAIN, 36 ),
                    new FontRenderContext( null, true, true ) );
            textShape = textLayout.getOutline( null );
            textYOffset = (float) (textLayout.getBounds().getHeight() - textLayout.getLeading());

            for ( int i = 0; i < stars.length; i++ ) {
                stars[i] = new StarSprite_Text( STAR_FRAME_COUNT * i / STAR_COUNT, textStarAnimation, textShape, textYOffset );
            }
        }

        this.alignToCenter = alignToCenter;

        setPreferredSize( new Dimension( WAVE_WIDTH, WAVE_HEIGHT ) );

        waveUp = new WaveUpFigure();
        waveDown = new WaveDownFigure();

        AffineTransform scale = AffineTransform.getScaleInstance( WAVE_WIDTH, WAVE_HEIGHT );
        waveUp.transform( scale );
        waveDown.transform( scale );

        //whirl = new StarWhirlFigure();
        //whirl.transform( AffineTransform.getScaleInstance( WAVE_HEIGHT * 0.8, WAVE_HEIGHT * 0.8 ) );

        timer = new Timer( 1000 / 20, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                frame++;

                for ( StarSprite_Text starSprite : stars ) {
                    starSprite.nextFrame();
                }

                repaint();
            }
        } );
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;

        Object savedAntoaliasingHint = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        Insets insets = getInsets();
        int x = insets.top;
        int y = insets.left;
        int width = getWidth() - insets.right - insets.left;
        int height = getHeight() - insets.bottom - insets.top;
        drawBackground( g2, x, y, width, height );
        drawWave( g2, x, y, width, height );
        drawTitle( g2, x, y, width, height );

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, savedAntoaliasingHint );
    }

    private void drawBackground( Graphics2D g2, int x, int y, int width, int height ) {
        g2.setColor( COLOR_BACKGROUND );
        g2.fillRect( x, y, width, height );
    }

    private void drawWave( Graphics2D g2, int x, int y, int width, int height ) {
        AffineTransform transform = g2.getTransform();

        g2.translate( x, height / 2 + y );

        g2.setPaint( WAVE_GRADIENT );

        IFigure nextWave = ((frame / WAVE_WIDTH) & 1) == 0 ? waveUp : waveDown;

        int xTranslate = -frame % WAVE_WIDTH;
        int translatedWidth = WAVE_WIDTH + xTranslate;
        while ( translatedWidth < width + WAVE_WIDTH ) {
            g2.translate( xTranslate, 0 );
            nextWave.draw( g2 );

            xTranslate = (int) WAVE_WIDTH;
            translatedWidth += xTranslate;

            nextWave = nextWave == waveUp ? waveDown : waveUp;
        }

        g2.setTransform( transform );
    }

    private void drawTitle( Graphics2D g2, int x, int y, int width, int height ) {
        if ( textLayout == null ) {
            return;
        }

        AffineTransform transform = g2.getTransform();

        x += alignToCenter
                ? (int) (width - textLayout.getBounds().getWidth()) / 2
                : 50;
        y += (int) (height - textLayout.getBounds().getHeight()) / 2;

        g2.translate( x, y + textYOffset );

        g2.setColor( COLOR_TITLE_SHADOW );
        textLayout.draw( g2, SHADOW_DISTANCE, SHADOW_DISTANCE );

        g2.setColor( COLOR_TITLE );
        //g2.fill( textShape );
        textLayout.draw( g2, 0, 0 );

        //g2.setColor( COLOR_BACKGROUND );
        //g2.draw( textShape );

        g2.translate( 0, -textYOffset );

        drawStars( g2 );

        g2.setTransform( transform );
    }

    private void drawStars( Graphics2D g2 ) {
        for ( StarSprite_Text starSprite : stars ) {
            starSprite.draw( g2 );
        }
    }

    public static GradientPaint calculateWaveGradient( int height ) {
        return new GradientPaint(
            0, -height / 2, new Color( 0x44FFFFFF, true ),
            0, height / 2, new Color( 0x00FFFFFF, true ) );
    }
}
