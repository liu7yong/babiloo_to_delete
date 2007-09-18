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

package ja.centre.util.propertytool;

import ja.centre.util.assertions.Arguments;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PropertyToolRequest {

    private static final Object[] EMPTY_ARRAY = { };

    private Object o;
    private String property;

    private Object value;

    public PropertyToolRequest( Object o, String property ) throws PropertyToolException {
        Arguments.assertNotNull( "o", o );
        Arguments.assertNotNull( "property", property );

        this.o = o;
        this.property = property;

        // split nested properties
        String[] nestedProperties;
        if ( property.indexOf( '.' ) == -1 ) {
            // no dots - simple property
            nestedProperties = new String[] { property };
        } else {
            // at least 1 dot - nested property
            nestedProperties = property.split( "\\." );
        }

        // retrieve property values into deep, step-by-step
        for ( int i = 0; i < nestedProperties.length; i++ ) {
            String nestedProperty = nestedProperties[i];

            o = extractNextProperty( o, nestedProperty );

            // check for null if there are more nested properties to dig (if the end is not reached)
            if ( o == null && (i != nestedProperties.length - 1) ) {
                throw new PropertyToolException( this.o, this.property,
                        "Could not retrieve next nested property value - property \"" + nestedProperties + "\" is null" );
            }
        }
        this.value = o;
    }

    private Object extractNextProperty( Object nextObject, String nextProperty ) throws PropertyToolException {
        // get bean info
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo( nextObject.getClass() );
        } catch ( IntrospectionException e ) {
            throw new PropertyToolException( o, property, "Could not retrieve bean info for property \"" + nextProperty + "\"", e );
        }

        // find property descriptor
        PropertyDescriptor propertyDescriptor = findPropertyDescriptorByName( beanInfo, nextProperty );

        if ( propertyDescriptor != null ) {
            // get read method
            Method readMethod = propertyDescriptor.getReadMethod();
            if ( readMethod != null ) {
                // extract the value
                try {
                    readMethod.setAccessible( true );
                    return readMethod.invoke( nextObject, EMPTY_ARRAY );
                } catch ( IllegalAccessException e ) {
                    throw new PropertyToolException( o, property, "Could not invoke read method", e );
                } catch ( InvocationTargetException e ) {
                    throw new PropertyToolException( o, property, "Could not invoke read method", e );
                }
            }
        }

        try {
            Field field = nextObject.getClass().getDeclaredField( nextProperty );
            field.setAccessible( true );
            return field.get( nextObject );
        } catch ( NoSuchFieldException e ) {
            throw new PropertyToolException( o, property, "Could not retrieve field or getter method for property \"" + nextProperty + "\"" );
        } catch ( IllegalAccessException e ) {
            throw new PropertyToolException( o, property, "Could not retirve field value", e );
        }
    }

    private PropertyDescriptor findPropertyDescriptorByName( BeanInfo beanInfo, String nextProperty ) throws PropertyToolException {
        for ( PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors() ) {
            if ( nextProperty.equals( propertyDescriptor.getName() ) ) {
                return propertyDescriptor;
            }
        }
        return null;
    }

    public Object getValue() {
        return value;
    }
}
