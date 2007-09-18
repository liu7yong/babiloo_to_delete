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

public abstract class StarSprite {
    protected double x;
    protected double y;

    protected int frame;
    protected StarAnimation animation;

    protected StarSprite( int frame, StarAnimation animation ) {
        this.frame = frame;
        this.animation = animation;
    }

    public void draw( Graphics2D g2 ) {
        g2.translate( x, y );
        animation.draw( g2, frame );
        g2.translate( -x, -y );
    }

    public void nextFrame() {
        frame++;
        if ( frame == animation.getFrameCount() ) {
            frame = 0;
            initialize();
        }
    }

    protected abstract void initialize();
}
