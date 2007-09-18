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

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.lingo.application.util.progress.ITitledMonitor;

class PhazesMonitor implements ITitledMonitor {
    public static final int DELEGATE_MIN = 0;
    public static final int DELEGATE_MAX = 1000;

    private final int phazeCount;
    final int blockSize;

    private int currentMinimum;
    private int currentMaximum;

    private int phaze;
    private ITitledMonitor delegate;

    public PhazesMonitor( ITitledMonitor delegate, int phazeCount ) {
        Arguments.assertNotNull( "delegate", delegate );

        this.delegate = delegate;

        this.phazeCount = phazeCount;
        blockSize = DELEGATE_MAX / this.phazeCount;
    }

    public void start( int minimum, int maximum ) {
        delegate.start( DELEGATE_MIN, DELEGATE_MAX );
        update( minimum );

        if ( phaze >= phazeCount ) {
            States.doThrow( "Unexpected call of start(...): phaze = "
                    + phaze + ", phazeCount = " + phazeCount );
        }

        this.currentMinimum = minimum;
        this.currentMaximum = maximum;
    }


    /*
    *                       value - min
    * f(min, max, value) = (------------- + phaze) * blockSize)
    *                         max - min
    *
    */
    public void update( int value ) {
        float floatValue = ((((float) value - currentMinimum) / (currentMaximum - currentMinimum)) + phaze ) * blockSize;
        delegate.update( (int) floatValue );
    }

    public void finish() {
        if ( phaze == phazeCount ) {
            States.doThrow( "Attempt to call finish after all " +  phazeCount + " phazes passed" );
        }
        update( currentMaximum );
        phaze++;
    }

    public void setTitle( String title ) {
        delegate.setTitle( title );
    }
    public void setText( String text ) {
        delegate.setTitle( text );
    }
}
