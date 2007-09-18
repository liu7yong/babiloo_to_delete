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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds listener proxy
 */
class Builder {
    private Class listenerClass;

    private MutableInvocationHandler mutableInvocationHandler;

    public Builder( String listenerClassName ) throws BuilderException {
        Arguments.assertNotNull( "listenerClassName", listenerClassName );

        this.listenerClass = getInterfaceClassByName( listenerClassName );
        this.mutableInvocationHandler = new MutableInvocationHandler();
    }


    public void bindListenerEventToLogicAction( String listenerEventMethodName, Invoker methodInvoker ) throws BuilderException {
        Arguments.assertNotNull( "listenerEventMethod", listenerEventMethodName );
        Arguments.assertNotNull( "methodInvoker", methodInvoker );

        mutableInvocationHandler.bindListenerMapToActionInvoker( listenerEventMethodName, methodInvoker );
    }

    public Object getListener() {
        return Proxy.newProxyInstance( getClass().getClassLoader(),
                new Class[] { listenerClass }, mutableInvocationHandler );
    }

    private Class getInterfaceClassByName( String listenerClassName ) throws BuilderException {
        Class interfaceClass;
        try {
            interfaceClass = getClass().getClassLoader().loadClass( listenerClassName );
        } catch ( ClassNotFoundException e ) {
            throw new BuilderException( e );
        }

        if ( !interfaceClass.isInterface() ) {
            throw new BuilderException( "Class \"" + listenerClassName + "\" should be interface." );
        }

        return interfaceClass;
    }

    private static class MutableInvocationHandler implements InvocationHandler {
        private Map<String, Invoker> listenerEventToActionInvokerMap;

        public MutableInvocationHandler() {
            this.listenerEventToActionInvokerMap = new HashMap<String, Invoker>();
        }

        public void bindListenerMapToActionInvoker( String listenerEvent, Invoker actionInvoker ) throws IllegalArgumentException {
            Arguments.assertNotEmpty( "listenerEvent", listenerEvent );
            Arguments.assertNotNull( "actionInvoker", actionInvoker );

            if ( listenerEventToActionInvokerMap.containsKey( listenerEvent ) ) {
                Arguments.doThrow( "Listener event \"" + listenerEvent + "\" is already registered" );
            }
            ;

            listenerEventToActionInvokerMap.put( listenerEvent, actionInvoker );
        }

        public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
            String listenerEvent = method.getName();

            // check, whether binding exists. If so, force action call
            if ( listenerEventToActionInvokerMap.containsKey( listenerEvent ) ) {
                Invoker actionInvoker = (Invoker) listenerEventToActionInvokerMap.get( listenerEvent );
                return actionInvoker.invoke( args );
            }

            // silentlty ignore all calls
            return null;
        }
    }
}
