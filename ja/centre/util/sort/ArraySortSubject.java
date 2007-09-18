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

package ja.centre.util.sort;

import ja.centre.util.assertions.Arguments;

import java.util.Arrays;
import java.util.List;

public class ArraySortSubject<T> implements ISortSubject<T> {
    private T[] values;

    public ArraySortSubject( T[] values ) {
        Arguments.assertNotNull( "values", values );

        this.values = values;
    }

    public final int size() {
        return values.length;
    }

    public final T get( int index ) {
        return values[index];
    }

    public final void swap( int index0, int index1 ) {
        T value0 = values[index0];
        values[index0] = values[index1];
        values[index1] = value0;
    }

    public void set( int index, T value ) {
        Arguments.assertNotNull( "value", value );

        values[index] = value;
    }

    public List<T> asList() {
        return Arrays.asList( values );
    }

    public final String toString() {
        return asList().toString();
    }
}
