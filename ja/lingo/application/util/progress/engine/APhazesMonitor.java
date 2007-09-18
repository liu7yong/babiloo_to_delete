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

package ja.lingo.application.util.progress.engine;

import ja.centre.gui.resources.Resources;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.application.util.progress.ITitledMonitor;

abstract class APhazesMonitor implements IMonitor {
    private Resources resources = Resources.forProperties( getClass() );

    private PhazesMonitor phazesDelegate;

    public APhazesMonitor( ITitledMonitor monitor, int phazeCount ) {
        phazesDelegate = new PhazesMonitor( monitor, phazeCount );
    }

    public void start( int minimum, int maximum ) {
        phazesDelegate.start( minimum, maximum );
    }

    public void update( int value ) {
        phazesDelegate.update( value );
    }
    public void finish() {
        phazesDelegate.finish();
    }

    protected void updateMessage( String key ) {
        phazesDelegate.setTitle( resources.text( key ) );
    }
}
