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

package ja.centre.gui.util;

import java.awt.*;

public class ColorUtil {
    public static Color deriveAlpha( Color color, int alpha ) {
        return new Color( color.getRed(), color.getGreen(), color.getBlue(), alpha );
    }

    public static Color middle( Color color1, Color color2 ) {
        return new Color(
                ( color1.getRed()   + color2.getRed()   ) >> 1,
                ( color1.getGreen() + color2.getGreen() ) >> 1,
                ( color1.getBlue()  + color2.getBlue()  ) >> 1,
                ( color1.getAlpha() + color2.getAlpha() ) >> 1 // TODO alpha should be calculated with proportions, result is lesser alpha
                );
    }
}
