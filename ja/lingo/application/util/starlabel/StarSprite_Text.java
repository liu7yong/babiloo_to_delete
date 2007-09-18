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

import java.awt.*;

public class StarSprite_Text extends StarSprite {
    private Shape textShape;
    private Rectangle textShapeBounds;
    private float textYOffset;

    public StarSprite_Text( int frame, StarAnimation animation, Shape textShape, float textYOffset ) {
        super( frame, animation );
        this.textShape = textShape;
        this.textShapeBounds = textShape.getBounds();
        this.textYOffset = textYOffset;

        initialize();
    }

    protected void initialize() {
        // NOTE optimization hint: possible freezes or event infinity-loops (on no text)
        while ( true ) {
            x = Math.random() * textShapeBounds.getWidth();
            y = Math.random() * textShapeBounds.getHeight();

            if ( textShape.contains( x, y - textYOffset ) ) {
                break;
            }
        }
    }
}
