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

package ja.centre.gui.actionbinder;

import java.lang.reflect.Method;

/**
 * Tries to suggest method by name
 */
class Guesser {
    public Method guessMethod( Object instance, String methodName, Class possibleEventArgumentClass ) throws GuesserException {
        // case 1: try to retrieve method with one "possibleEventArgumentClass" argument
        try {
            Method logicActionMethod = instance.getClass().getMethod( methodName, possibleEventArgumentClass );
            logicActionMethod.setAccessible( true );
            return logicActionMethod;
        } catch ( NoSuchMethodException e ) {
            // do nothing: proceed to next case
        }

        // case 2: try to retrieve method with no arguments
        try {
            Method logicActionMethod = instance.getClass().getMethod( methodName );
            logicActionMethod.setAccessible( true );
            return logicActionMethod;
        } catch ( NoSuchMethodException e ) {
            throw new GuesserException( "There is no action method \"" + methodName
                    + "\" in class \"" + instance.getClass().getName() + "\" which has either nor arguments nor 1 argument of class \"" + possibleEventArgumentClass.getName() + "\"" );
        }
    }

}
