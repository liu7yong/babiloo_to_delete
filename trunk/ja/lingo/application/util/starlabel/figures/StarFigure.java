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

package ja.lingo.application.util.starlabel.figures;

import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.awt.*;

public class StarFigure implements IFigure {
    private static final Color COLOR_STAR = new Color( 0xFFFFFF );
    private static final Color COLOR_STAR_SMALL = new Color( 0xCCCCCC );

    private GeneralPath star;
    private GeneralPath smallStar;

    public StarFigure() {
        star = createStar();
        smallStar = createSmallStar();
    }

    private static GeneralPath createStar() {
        final float EDGE = 1.3f;
        final float D = EDGE / 10;

        GeneralPath path = new GeneralPath();
        path.moveTo( EDGE, 0 );
        path.curveTo( -D,  D,  D, -D, 0, EDGE );
        path.curveTo( -D, -D,  D,  D, -EDGE, 0 );
        path.curveTo(  D, -D, -D,  D, 0, -EDGE );
        path.curveTo(  D,  D ,-D, -D, EDGE, 0 );
        return path;
    }
    private static GeneralPath createSmallStar(){
        GeneralPath star = createStar();
        star.transform( AffineTransform.getRotateInstance( Math.PI / 4, 0, 0 ));
        star.transform( AffineTransform.getScaleInstance( 0.8, 0.8 ) );
        return star;
    }


    public void draw( Graphics2D g2 ) {
        g2.setColor( COLOR_STAR_SMALL );
        g2.fill( smallStar );

        g2.setColor( COLOR_STAR );
        g2.fill( star );

    }

    public void transform( AffineTransform transform ) {
        star.transform( transform );
        smallStar.transform( transform );
    }

}
