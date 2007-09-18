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

package ja.lingo.application.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class Gaps {
    public static final int GAP3 = 3;
    public static final int GAP5 = 5;
    public static final int GAP7 = 7;

    private Gaps() {
    }

    public static Border border2() {
        return BorderFactory.createEmptyBorder( 2, 2, 2, 2 );
    }
    public static Border border5() {
        return BorderFactory.createEmptyBorder( GAP5, GAP5, GAP5, GAP5 );
    }
    public static Border border7() {
        return BorderFactory.createEmptyBorder( GAP7, GAP7, GAP7, GAP7 );
    }
    public static Border borderLined() {
        return BorderFactory.createLineBorder( Color.GRAY );
    }
    public static Border border7Lined7() {
        return BorderFactory.createCompoundBorder(
                Gaps.border7(),
                BorderFactory.createCompoundBorder( Gaps.borderLined(), Gaps.border7() )
        );
    }

    public static <C extends JComponent> C applyBorder2( C c ) {
        c.setBorder( border2() );
        return c;
    }
    public static <C extends JComponent> C applyBorder5( C c ) {
        c.setBorder( border5() );
        return c;
    }
    public static <C extends JComponent> C applyBorder7( C c ) {
        c.setBorder( border7() );
        return c;
    }
    public static <C extends JComponent> C applyBorder2Lined( C c ) {
        c.setBorder( BorderFactory.createCompoundBorder(
                borderLined(),
                border2()
        ) );
        return c;
    }
    public static <C extends JComponent> C applyBorder7Lined7( C c ) {
        c.setBorder( border7Lined7() );
        return c;
    }
    public static <C extends JComponent> C applyBorder( C c, int margin ) {
        c.setBorder( BorderFactory.createEmptyBorder( margin, margin, margin, margin ) );
        return c;
    }
    public static JPanel wrap( JComponent c ) {
        JPanel p = new JPanel( new BorderLayout() );
        p.add( c, BorderLayout.CENTER );
        return p;
    }

    public static <C extends JComponent> C compound( C c, Border outside, Border insisde ) {
        c.setBorder( new CompoundBorder( outside, insisde ) );
        return c;
    }
}
