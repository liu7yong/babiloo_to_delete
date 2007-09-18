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

package ja.lingo.application.util.plaf;

import com.incors.plaf.kunststoff.KunststoffToggleButtonUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class JaLingoToggleButtonUI extends KunststoffToggleButtonUI {
    private final static JaLingoToggleButtonUI buttonUI = new JaLingoToggleButtonUI();

    public static ComponentUI createUI( JComponent c ) {
        return buttonUI;
    }

    // NOTE: jalingo: added to draw border
    protected void paintButtonPressed( Graphics g, AbstractButton b ) {
        if ( b.isContentAreaFilled() ) {
            Dimension size = b.getSize();
            g.setColor( getSelectColor() );
            g.fillRect( 0, 0, size.width, size.height );

            UIManager.getBorder( "ToggleButton.border" ).paintBorder( b, g, 0, 0, b.getWidth(), b.getHeight() );
        }
    }
}
