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

package ja.centre.gui.actionbinder.config;

import ja.centre.util.assertions.Arguments;

public class ConfigException extends Exception {
    public ConfigException( Throwable cause ) {
        super( cause );
    }
    public ConfigException( String message ) {
        super( message );
    }
    public ConfigException( String message, Throwable cause ) {
        super( message, cause );
    }
    public static void doThrow( String message, Class guiClass ) throws ConfigException {
        throw new ConfigException( createMessage( message, guiClass, null, null, null ) );
    }
    public static void doThrow( String message, Class guiClass, String component, Class listenerType ) throws ConfigException {
        throw new ConfigException( createMessage( message, guiClass, component, listenerType, null ) );
    }
    public static void doThrow( String message, Class guiClass, String component, Class listenerType, String mapping ) throws ConfigException {
        throw new ConfigException( createMessage( message, guiClass, component, listenerType, mapping ) );
    }
    static String createMessage( String message, Class guiClass, String component, Class listenerType, String mapping ) {
        Arguments.assertNotNull( "message", message );
        Arguments.assertNotNull( "guiClass", guiClass );

        return message + ". Gui class \"" + guiClass.getName() + "\"" +
                (component      != null ? ", component \""      + component + "\""              : "") +
                (listenerType   != null ? ", listenerType \""   + listenerType.getName() + "\"" : "") +
                (mapping        != null ? ", mapping \""        + mapping + "\""                : "") +
                ".";
    }
}
