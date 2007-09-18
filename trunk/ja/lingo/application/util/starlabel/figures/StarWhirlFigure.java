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

import java.awt.*;
import java.awt.geom.AffineTransform;

public class StarWhirlFigure implements IFigure {
    private IFigure[] stars;

    public StarWhirlFigure() {
        final int COUNT = 16;
        final double STAR_SCALE = 0.15;

        stars = new IFigure[COUNT];
        for ( int i = 0; i < COUNT; i++ ) {
            double t = (double) i / COUNT;
            double tm = 1 - t;

            IFigure s = new StarFigure();
            s.transform( AffineTransform.getScaleInstance( STAR_SCALE * tm, STAR_SCALE * tm ) );
            s.transform( AffineTransform.getTranslateInstance( (tm - tm * STAR_SCALE)/2, 0 ) );
            s.transform( AffineTransform.getRotateInstance( -t * Math.PI * 3 + Math.PI / 2 ) );

            stars[i] = s;
        }

    }

    public void draw( Graphics2D g2 ) {
        for ( IFigure star : stars ) {
            star.draw( g2 );
        }
    }

    public void transform( AffineTransform transform ) {
        for ( IFigure star : stars ) {
            star.transform( transform );
        }
    }
}
