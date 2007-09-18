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

package ja.centre.util.measurer;

public class TimeMeasurer {
    private long startMillis;
    private long deltaMillis;

    public TimeMeasurer() {
        this( true );
    }

    public TimeMeasurer( boolean startImmediately ) {
        if ( startImmediately ) {
            start();
        }
    }

    public long getDeltaMillis() {
        stop();
        return deltaMillis;
    }

    public double getDeltaSeconds() {
        return getDeltaMillis() / 1000d;
    }

    public void stop() {
        if ( startMillis != 0 ) {
            deltaMillis += System.currentTimeMillis() - startMillis;
            startMillis = 0;
        }
    }

    public void start() {
        startMillis = System.currentTimeMillis();
    }

    public String toString() {
        return getDeltaSeconds() + " seconds";
    }
}
