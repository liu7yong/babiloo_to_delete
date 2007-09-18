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

package ja.centre.util.lang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Reflector {
    private Reflector() {
    }

    public static <T> T invokePrivate( Object obj, String method, Object... args ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class clazz = obj.getClass();
        do {
            for ( Method m : clazz.getDeclaredMethods() ) {
                if ( m.getName().equals( method ) ) {
                    if ( m.getParameterTypes().length == args.length ) {
                        Class<?>[] methodParamTypes = m.getParameterTypes();
                        for ( int i = 0; i < methodParamTypes.length; i++ ) {
                            if ( !methodParamTypes[i].isAssignableFrom( args[i].getClass() ) ) {
                                break;
                            }
                        }
                        m.setAccessible( true );
                        return (T) m.invoke( obj, args );
                    }
                }
            }
            clazz = clazz.getSuperclass();
        } while ( clazz != null );

        throw new NoSuchMethodException( obj.getClass().getName() + ":" + method);
    }
    public static Class[] collectInterfaces( Class clazz ) {
        List<Class> interaces = new ArrayList<Class>();

        while ( clazz.getSuperclass() != null ) {
            interaces.addAll( Arrays.asList( clazz.getInterfaces() ) );
            clazz = clazz.getSuperclass();
        }

        return interaces.toArray( new Class[0] );
    }
}
