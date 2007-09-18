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

package ja.lingo.application.util.plaf.theme;

import com.incors.plaf.ColorUIResource2;
import com.incors.plaf.kunststoff.GradientTheme;

import javax.swing.plaf.ColorUIResource;

public class RainyGradientTheme implements GradientTheme {
    private final ColorUIResource componentGradientColorReflection      = new ColorUIResource2( 0x88FBFBFD, true );
    private final ColorUIResource componentGradientColorShadow          = new ColorUIResource2( 0x00000000, true );

    private final ColorUIResource textComponentGradientColorReflection  = new ColorUIResource2( 0x20000000, true );
    private final ColorUIResource textComponentGradientColorShadow      = new ColorUIResource2( 0x00000000, true );

    private final int backgroundGradientShadow = 16;

    public ColorUIResource getComponentGradientColorReflection() {
        return componentGradientColorReflection;
    }
    public ColorUIResource getComponentGradientColorShadow() {
        return componentGradientColorShadow;
    }
    public ColorUIResource getTextComponentGradientColorReflection() {
        return textComponentGradientColorReflection;
    }
    public ColorUIResource getTextComponentGradientColorShadow() {
        return textComponentGradientColorShadow;
    }
    public int getBackgroundGradientShadow() {
        return backgroundGradientShadow;
    }
}
