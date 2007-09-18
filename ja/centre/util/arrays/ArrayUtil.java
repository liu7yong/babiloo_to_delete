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

package ja.centre.util.arrays;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtil {
    private ArrayUtil() {
    }

    public static boolean contains( Object[] objects, Object o ) {
        return indexOf( objects, o ) != -1;
    }

    public static int indexOf( Object[] objects, Object o ) {
        for ( int i = 0; i < objects.length; i++ ) {
            Object object = objects[i];
            if ( object.equals( o ) )
                return i;
        }
        return -1;
    }


    /**
     * Finds sequence of <code>subject</code> in <code>array</code>.
     *
     * @param array
     * @param subject
     * @return if found, position of <code>subject</code> in <code>array</code>.
     *         if not found, then <code>-1</code>
     */
    public static final int indexOf( byte[] array, byte[] subject ) {
        return indexOf( array, array.length, subject, subject.length );
    }

    public static final int indexOf( byte[] array, int arrayLength, byte[] subject, int subjectLength ) {
        for ( int i = 0; i < arrayLength; i++ ) {
            boolean matchFound = true;
            for ( int j = 0; j < subjectLength; j++ ) {
                if ( i + j >= arrayLength || array[i + j] != subject[j] ) {
                    matchFound = false;
                    break;
                }
            }
            if ( matchFound ) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Counstruct new array: result = (prefix, subject)
     */
    public static byte[] prepend( byte[] subject, byte[] prefix ) {
        byte[] bytes = new byte[subject.length + prefix.length];
        System.arraycopy( prefix, 0, bytes, 0, prefix.length);
        System.arraycopy( subject, 0, bytes, prefix.length, subject.length);
        return bytes;
    }

    public static List<Object> asList( byte[] array ) {
        return asList( array, 0, array.length );
    }
    public static List<Object> asList( byte[] array, int offset, int length ) {
        List<Object> actualList = new ArrayList<Object>();
        for ( int i = 0; i < length; i++ ) {
            actualList.add( array[i+offset] );
        }
        return actualList;
    }
}
