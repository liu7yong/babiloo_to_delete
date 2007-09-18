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

package ja.centre.gui.components.enhancedwindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class EnhancedDialog extends JDialog implements IEnhancedWindow<JDialog> {
    public EnhancedDialog( Frame owner ) {
        this( owner, false );
    }
    public EnhancedDialog( Frame owner, boolean modal ) {
        super( owner, modal );
        init();
    }

    public EnhancedDialog( Dialog owner ) {
        this( owner, false );
    }
    public EnhancedDialog( Dialog owner, boolean modal ) {
        super( owner, modal );
        init();
    }

    private void init() {
        setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
    }

    protected JRootPane createRootPane() {
        return EnhancedWindowHelper.attachCloseOnEscape( super.createRootPane(), this );
    }

    public void setLocationRelativeTo( Component c ) {
        super.setLocationRelativeTo( EnhancedWindowHelper.filterIconifiedFrame( c ) );
    }

    // NOTE: hack to make it public
    public void processWindowEvent( WindowEvent event ) {
        super.processWindowEvent( event );
    }

    public JDialog getWindow() {
        return this;
    }
}
