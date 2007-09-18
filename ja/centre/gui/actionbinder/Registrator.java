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

import ja.centre.util.assertions.Arguments;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers listeners.
 *
 * Imagine, if "listenerInstance.getClass().getName()" return "java.awt.ActionListener"
 * then, the name of componentInstace's registration method will be: "add" + "ActionListener"
 * and the singature of the method will be "(" + listenerInstance.getClass() + ") : void"
 *
 * in other words, target call will look like (without reflection, sure)
 *
 * "componentInstance.addActionListener( listenerInstance )"
 *
 * NOTE: if there is no correct registration method, thow RegistrationException with
 *       corresponding message
 */
class Registrator {
    private RegistratorHelper helper;

    public Registrator() {
        this( new RegistratorHelper() );
    }

    // for test purposes only
    Registrator( RegistratorHelper helper ) {
        this.helper = helper;
    }

    public void register( Object componentInstance, Object listenerInstance ) throws RegistrationException {
        Arguments.assertNotNull( "componentInstance", componentInstance );
        Arguments.assertNotNull( "listenerInstance", listenerInstance );

        // create map of "addXXXXList" methods names calculated from listener interfaces names
        Map<String, Class> listenerMethodToInterfaceMap = new HashMap<String, Class>();
        Class[] interfaces = listenerInstance.getClass().getInterfaces();
        for ( Class interfaze : interfaces ) {
            listenerMethodToInterfaceMap.put( helper.calculateAddMethodName( interfaze.getName() ), interfaze );
        }

        // go through all component's methods, check whether method is "listener registrator"
        // and if so, attach listener by calling this method
        for ( Method method : componentInstance.getClass().getMethods() ) {
            if ( helper.isMethodInListenerMethodToInterfaceNameMap( listenerMethodToInterfaceMap, method ) ) {
                helper.attachListnerToComponent( method, componentInstance, listenerInstance );
                return;
            }
        }

        List<String> registrationMehods = new ArrayList<String>();
        for ( Class interfaceClass : interfaces ) {
            registrationMehods.add( helper.calculateAddMethodName( interfaceClass.getName() ) + "(...)" );
        }

        throw new RegistrationException( "Could not find none of registration methods "
                + registrationMehods + " in component \""
                + componentInstance.getClass().getName() + "\"" );
    }
}
