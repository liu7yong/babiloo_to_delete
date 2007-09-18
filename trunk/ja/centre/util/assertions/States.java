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

import javax.swing.*;

public class States {
    private States() {
    }

    public static void assertNotNull( Object o, String message ) {
        Arguments.assertNotNull( "message", message );

        if ( o == null ) {
            doThrow( message );
        }
    }

    public static void assertNull( Object o, String message ) {
        Arguments.assertNotNull( "message", message );

        if ( o != null ) {
            doThrow( message );
        }
    }

    public static void assertTrue( boolean condition, String message ) {
        if ( !condition ) {
            doThrow( message );
        }
    }

    public static void assertFalse( boolean condition, String message ) {
        if ( condition ) {
            doThrow( message );
        }
    }

    public static void assertEquals( String message, int expected, int actual ) {
        Arguments.assertNotNull( "message", message );

        if ( expected != actual ) {
            doThrow( message + ": expected " + expected + ", but actual is " + actual );
        }
    }

    public static void assertNonZero( int i, String message ) {
        Arguments.assertNotNull( "message", message );

        if ( i == 0 ) {
            doThrow( message );
        }
    }

    public static IllegalStateException doThrow( String message ) {
        throw new IllegalStateException( message );
    }

    public static IllegalStateException doThrow( String message, Throwable cause ) {
        throw new IllegalStateException( message, cause );
    }

    // TODO move another "non-states" class, say Assertions
    public static RuntimeException shouldNeverReachHere( Throwable t ) {
        throw new RuntimeException( t );
    }
    public static RuntimeException shouldNeverReachHere( String message ) {
        throw new RuntimeException( message );
    }
    public static void shouldNeverReachHere( String message, Throwable cause ) {
        throw new RuntimeException( message, cause );
    }

    // TODO move another "non-states" class, say Assertions
    public static UnsupportedOperationException notlmplemented() {
        throw new UnsupportedOperationException( "Not implemented" );
    }

    // TODO consider moving to another more Swing-related class
    public static void assertIsEDT() {
        if ( !SwingUtilities.isEventDispatchThread() ) {
            doThrow( "Expected to be called from EDT. Was \""
                        + Thread.currentThread().getClass().getName()
                        + "\" with name \"" + Thread.currentThread().getName()
                        + "\"" );
        }
    }
}
