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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

public class StarImageExporter {
    private StarImageExporter() {
    }

    public static void main( String[] args ) throws IOException {
        saveHeader( 800, 111 );
        saveBackground( StarLabel.WAVE_WIDTH * 2, StarLabel.WAVE_HEIGHT );

        saveIcon( 16 );
        saveIcon( 32 );
        saveIcon( 48 );
        saveIcon( 64 );
    }

    private static void saveHeader( int width, int height ) throws IOException {
        StarLabel label = new StarLabel( "JaLingo", false );
        label.setBounds( 0, 0, width, height );

        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
        label.paintComponent( (Graphics2D) image.getGraphics() );

        ImageIO.write( image, "png", new FileOutputStream( "header.png" ) );
    }

    private static void saveBackground( int width, int height ) throws IOException {
        StarLabel label = new StarLabel( "", false );
        label.setBounds( 0, 0, width, height );

        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
        label.paintComponent( (Graphics2D) image.getGraphics() );

        ImageIO.write( image, "png", new FileOutputStream( "background.png" ) );
    }

    private static void saveIcon( int iconEdge ) throws IOException {
        BufferedImage image = new BufferedImage( iconEdge, iconEdge, BufferedImage.TYPE_4BYTE_ABGR );

        int ident = 1;//iconEdge / 16;

        Graphics2D g = (Graphics2D) image.getGraphics();
        g.translate( ident, ident );

        StarIcon starIcon = new StarIcon( iconEdge - ident * 2 );
        starIcon.drawIcon( g );

        ImageIO.write( image, "png", new FileOutputStream( "jalingo" + iconEdge + "x" + iconEdge + ".png" ) );
    }
}
