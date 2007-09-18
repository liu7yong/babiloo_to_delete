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

package ja.centre.util.sort.external;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;

import java.io.IOException;

class StepReader<T> {
    private IReader<T> reader;
    private T last;

    public StepReader( IReader<T> reader ) {
        Arguments.assertNotNull( "reader", reader );

        this.reader = reader;
    }

    public boolean hasLast() {
        return last != null;
    }

    public boolean hasNext() throws IOException {
        return reader.hasNext();
    }

    public T getLast() {
        States.assertTrue( hasLast(), "There is no current value. Call \"readNext()\" once at least." );

        return last;
    }

    public T readNext() throws IOException {
        States.assertTrue( hasNext(), "There is no next value" );

        return last = reader.next();
    }

    public void close() throws IOException {
        reader.close();
    }
}
