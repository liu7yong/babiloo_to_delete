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

package ja.centre.util.beans;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class BeanPersister<T> {
    private T t;
    private String fileName;

    public BeanPersister( T t, String fileName ) {
        Arguments.assertNotNull( "t", t );
        Arguments.assertNotNull( "fileName", fileName );
        this.t = t;
        this.fileName = fileName;
    }

    public T load() {
        return load( (Class<T>) t.getClass(), fileName );
    }

    public void save( T t ) {
        save( t, fileName );
    }

    public static <T> T load( Class<T> aClass, InputStream is ) {
        XMLDecoder xmlDecoder = null;
        try {
            xmlDecoder = new XMLDecoder( is );
            return (T) xmlDecoder.readObject();
        } catch ( Exception e ) {
            return createNew( aClass );
        } finally {
            if ( xmlDecoder != null ) {
                xmlDecoder.close();
            }
        }
    }
    public static <T>void save( T t, OutputStream os ) {
        XMLEncoder xmlEncoder = null;
        try {
            xmlEncoder = new XMLEncoder( os );
            xmlEncoder.writeObject( t );
        } finally {
            if ( xmlEncoder != null ) {
                xmlEncoder.close();
            }
        }
    }

    public static <T> T load( Class<T> aClass, String fileName ) {
        Arguments.assertNotNull( "aClass", aClass );
        Arguments.assertNotNull( "fileName", fileName );

        try {
            return load( aClass, new FileInputStream( fileName ) );
        } catch ( FileNotFoundException e ) {
            return createNew( aClass );
        }
    }
    public static <T> void save( T t, String fileName ) {
        Arguments.assertNotNull( "t", t );
        Arguments.assertNotNull( "fileName", fileName );

        try {
            save( t, new FileOutputStream( fileName ) );
        } catch ( FileNotFoundException e ) {
            States.shouldNeverReachHere( e ); // TODO leave as is?
        }
    }

    private static <T>T createNew( Class<T> aClass ) {
        try {
            return aClass.newInstance();
        } catch ( InstantiationException e1 ) {
            throw States.shouldNeverReachHere( e1 ); // TODO leave as is?
        } catch ( IllegalAccessException e1 ) {
            throw States.shouldNeverReachHere( e1 ); // TODO leave as is?
        }
    }
}
