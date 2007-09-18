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

package ja.centre.util.assertions;

import java.util.Collection;
import java.util.Iterator;

public class Arguments {
    private Arguments() {
    }

    // TODO swap args
    public static void assertNotNull( String argumentName, Object o ) throws NullPointerException {
        if ( o == null ) {
            throw new NullPointerException( "Argument \"" + argumentName + "\" can not be null." );
        }
    }

    public static void assertNotEmpty( String argumentName, String argument ) {
        assertNotNull( argumentName, argument );

        if ( argument.length() == 0 ) {
            Arguments.doThrow( "String argument \"" + argumentName + "\" can not have zero length." );
        }
    }
    public static void assertNotEmpty( String argumentName, Collection collection ) {
        assertNotNull( argumentName, collection );

        if ( collection.isEmpty() ) {
            Arguments.doThrow( "Argument \"" + argumentName + "\" (which is a collection) can not be empty" );
        }
    }
    public static void assertNotEmpty( String argumentName, Object[] array ) {
        assertNotNull( argumentName, array );

        if ( array.length == 0 ) {
            Arguments.doThrow( "Argument \"" + argumentName + "\" (which is an array) can not be empty" );
        }
    }

    public static void assertInstanceOf( String argumentName, Object object, Class<?> requiredType ) {
        if ( !requiredType.isAssignableFrom( object.getClass() ) ) {
            Arguments.doThrow( "Argument \"" + argumentName + "\" should be of type \"" + requiredType.getName() + "\"" );
        }
    }
    public static void assertInstacesOf( String argumentName, Collection collection, Class requiredType ) {
        assertNotNull( argumentName, collection );

        for ( Iterator i = collection.iterator(); i.hasNext(); ) {
            assertInstanceOf( "item of: " + argumentName, i.next(), requiredType );
        }
    }

    public static void assertPositiveNonZero( String argumentName, int value ) {
        if ( value <= 0 ) {
            Arguments.doThrow( "Argument \"" + argumentName + "\" must be positive non zero integer. Actual value was " + value );
        }
    }

    public static void assertInBounds( String argumentName, int value, int minValue, int maxValue ) {
        if ( value < minValue || value > maxValue ) {
            throw new IndexOutOfBoundsException( "Argument \"" + argumentName + "\" must be in range [" + minValue + ";" + maxValue + "]. Actual value was " + value );
        }
    }

    public static IllegalArgumentException doThrow( String message ) throws IllegalArgumentException {
        throw new IllegalArgumentException( message );
    }
    public static void doThrow( String message, Throwable cause ) {
        throw new IllegalArgumentException( message, cause );
    }

    public static NullPointerException doThrowNull( String message ) throws NullPointerException {
        throw new NullPointerException( message );
    }
}
