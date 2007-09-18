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

package ja.centre.gui.browser;

import ja.centre.util.assertions.States;

import java.lang.reflect.Method;

public class Browser {
    public static void openUrl( String url ) {
        String osName = System.getProperty( "os.name" ).toLowerCase();
        try {
            if ( osName.contains( "windows" ) ) {
                // TODO how to open in new window???
                Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler \"" + url + "\"" );
            } else if ( osName.contains( "mac" ) ) {
                Method openURLMethod = Class.forName( "com.apple.eio.FileManager" ).getDeclaredMethod( "openURL", new Class[] { String.class } );
                openURLMethod.invoke( null, new Object[] { url } );
            } else { //assume Unix or Linux
                String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
                for ( String browser : browsers ) {
                    if ( Runtime.getRuntime().exec( new String[] {
                            "which", browser } ).waitFor() == 0 ) {
                        Runtime.getRuntime().exec( new String[] { browser, url } );
                        return;
                    }
                }
                States.shouldNeverReachHere( "Could not find web browser" );
            }
        } catch ( Exception e ) {
            // TODO better error reporting (not needed now)
            States.shouldNeverReachHere( e );
        }
    }
}
