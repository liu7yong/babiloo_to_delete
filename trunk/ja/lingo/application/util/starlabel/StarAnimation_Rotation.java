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

import java.awt.geom.AffineTransform;

public class StarAnimation_Rotation extends StarAnimation {
    protected double theta;

    public StarAnimation_Rotation( int frameCount ) {
        super( frameCount );

        theta = Math.PI / frameCount;

        for ( int i = 0; i < frameCount / 2; i++ ) {
            sprites[i].transform(
                    AffineTransform.getRotateInstance( theta * i ) );

            sprites[frameCount - i - 1].transform(
                    AffineTransform.getRotateInstance( -theta * i ) );
        }
    }
}
