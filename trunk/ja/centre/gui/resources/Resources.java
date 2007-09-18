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

package ja.centre.gui.resources;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

public class Resources {
    public static final Log LOG = LogFactory.getLog( Resources.class );

    private static final String SUFFIX_TEXT = ".text";

    private static final String SUFFIX_KEY = ".key";

    private static final String SUFFIX_RESOURCE = ".resource";
    private static final String SUFFIX_ICON = ".icon";

    // TODO move this out, may be to some config?
    public static final String RESOURCES_PACKAGE = "/resources/";

    private Class aClass;
    private Properties properties;

    public static Resources forProperties( Class aClass ) {
        return new Resources( aClass );
    }
    public static String asString( Class aClass, String suffix ) {
        String name = aClass.getName();
        name = "/" + name.replace( ".", "/" ) + suffix;
        InputStream is = aClass.getResourceAsStream( name );

        if ( is == null ) {
            Arguments.doThrow( "Resource file file for class \"" + aClass.getName() + "\" does not exist" );
        }

        StringBuilder builder = new StringBuilder();
        try {
            byte[] buffer = new byte[32768];
            int read;
            while ( (read = is.read( buffer )) != -1 ) {
                builder.append( new String( buffer, 0, read, "UTF-8" ) );
            }
        } catch ( IOException e ) {
            States.shouldNeverReachHere( e );
        } finally {
            try {
                is.close();
            } catch ( IOException e ) {
                LOG.error( "Exception occured when tried to close html resource input stream", e );
            }
        }
        return builder.toString();
    }

    private Resources( Class aClass ) {
        Arguments.assertNotNull( "aClass", aClass );

        this.properties = new Properties();
        this.aClass = aClass;

        String path = aClass.getName();

        path = path.replace( ".", "/" );
        path = "/" + path + ".properties";

        InputStream resource = aClass.getResourceAsStream( path );
        if ( resource == null ) {
            Arguments.doThrow( "Properties file for class \"" + aClass.getName() + "\" does not exist" );
        }

        try {
            properties.load( resource );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public String text( String key ) {
        return getValue( key + SUFFIX_TEXT );
    }
    public String text( String key, Object... values ) {
        return MessageFormat.format( text( key ), values );
    }
    public URL url( String key ) {
        return getResourceAsUrl( getValue( key + SUFFIX_RESOURCE ) );
    }

    // swing
    public ImageIcon icon( String resourcePath ) {
        return new ImageIcon( getResourceAsUrl(
                getValue( resourcePath + SUFFIX_ICON ) ) );
    }
    public JLabel label( String key ) {
        JLabel label = new JLabel( text( key ) );
        label.setFocusable( false );
        return label;
    }

    public KeyStroke stroke( String key ) {
        String fullKey = key + SUFFIX_KEY;

        String value = getValue( fullKey );
        KeyStroke keyStroke = KeyStroke.getKeyStroke( value );

        if ( keyStroke == null ) {
            Arguments.doThrow( "Incorrectly formatted key \""
                    + fullKey +  "\": \"" + value + "\"" );
        }

        return keyStroke;
    }

    private URL getResourceAsUrl( String resourcePath ) {
        if ( resourcePath.startsWith( "/" ) )
            Arguments.doThrow( "Resource path \""
                    + resourcePath + "\" is invalid. It must not start with \"/\"" );

        String fullPath = RESOURCES_PACKAGE + resourcePath;
        URL url = aClass.getResource( fullPath );

        if ( url == null ) {
            Arguments.doThrow( "Could not load resource \"" + fullPath + "\" for class \""
                    + aClass.getName() + "\"");
        }

        return url;
    }
    private String getValue( String fullKey ) {
        String value = properties.getProperty( fullKey );
        if ( value == null ) {
            Arguments.doThrow( "Could not load value for key \"" + fullKey
                    + "\" for class \"" + aClass.getName() + "\"" );
        }
        return value;
    }
}
