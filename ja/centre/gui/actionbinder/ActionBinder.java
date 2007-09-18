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

import ja.centre.gui.actionbinder.config.*;
import ja.centre.gui.actionbinder.config.Config;
import ja.centre.util.assertions.Arguments;
import ja.centre.util.propertytool.PropertyTool;
import ja.centre.util.propertytool.PropertyToolException;

import java.lang.reflect.Method;

public class ActionBinder {
    private Config config;
    private Object gui;
    private Object logic;

    private Guesser guesser;

    public ActionBinder( Config config, Object guiAndLogic ) {
        this( config, guiAndLogic, guiAndLogic );
    }

    public ActionBinder( Config config, Object gui, Object logic ) {
        Arguments.assertNotNull( "config", config );
        Arguments.assertNotNull( "gui", gui );
        Arguments.assertNotNull( "logic", logic );

        this.config     = config;
        this.gui        = gui;
        this.logic      = logic;

        this.guesser = new Guesser();
    }

    public void bind() throws ActionBinderException {
        bindListeners();
    }

    private void bindListeners() throws ActionBinderException {
        for ( Listener listener : config.getListeners() ) {
            // extract component from GUI by its specification
            Object componentInstance;
            try {
                componentInstance = PropertyTool.getValue( gui, listener.getComponent() );
            } catch ( PropertyToolException e ) {
                throw new ActionBinderException( config, e );
            }

            if ( componentInstance == null ) {
                Arguments.doThrowNull( "Got null for component \"" + listener.getComponent() + "\", gui class \"" + gui.getClass().getName() + "\"" );
            }

            // create proxy-listener and tune listeners
            Builder builder = createListenerBuilder( listener.getType() );

            // go through all event's and weave them into the listener (which is under construction)
            Class listenerClass = getInterfaceClassByName( listener.getType() );
            for ( Event event : listener.getEvents() ) {
                addEventToBuilder( builder, listener, event, extractPossibleEventArgumentClass( listenerClass, event.getMethod() ) );
            }

            Object listenerInstance = builder.getListener();

            // register listener in the component
            Registrator registrator = new Registrator();
            try {
                registrator.register( componentInstance, listenerInstance );
            } catch ( RegistrationException e ) {
                throw new ActionBinderException( config, e );
            }
        }
    }

    private Builder createListenerBuilder( String listenerType ) throws ActionBinderException {
        try {
            return new Builder( listenerType );
        } catch ( BuilderException e ) {
            throw new ActionBinderException( config, e );
        }
    }

    private void addEventToBuilder( Builder builder, Listener listener, Event event, Class possibleEventArgumentClass ) throws ActionBinderException {
        Invoker methodInvoker = null;
        try {
            methodInvoker = buildMethodInvokerFromGenericAction( event.getAction(), possibleEventArgumentClass );
        } catch ( GuesserException e ) {
            throw new ActionBinderException( config, e );
        }

        try {
            builder.bindListenerEventToLogicAction( event.getMethod(), methodInvoker );
        } catch ( BuilderException e ) {
            throw new ActionBinderException( config, "Error occured when tried to bind gui event method \""
                    + event.getMethod() + "\" for component \"" + listener.getComponent() + "\"", e );
        }
    }

    private Invoker buildMethodInvokerFromGenericAction( Action action, Class possibleEventArgumentClass ) throws GuesserException {
        // TODO refactor with usage of Visitor (applied to IAction)

        // process condition (gui)
        if ( action.hasAction() ) {
            return Invokers.condition(
                    gui, guesser.guessMethod( gui, action.getMethod(), possibleEventArgumentClass ),
                    buildMethodInvokerFromGenericAction( action.getAction(), possibleEventArgumentClass ) );
        }

        // process action (logic)
        return Invokers.action(
                logic, guesser.guessMethod( logic, action.getMethod(), possibleEventArgumentClass ) );
    }

    private Class extractPossibleEventArgumentClass( Class listenerClass, String listenerEventMethodName ) throws ActionBinderException {
        Method listenerEventMethod = findListenerEventMethod( listenerClass, listenerEventMethodName );
        return listenerEventMethod.getParameterTypes()[0];
    }

    private Method findListenerEventMethod( Class listenerClass, String listenerEventMethodName ) throws ActionBinderException {
        for ( Method method : listenerClass.getMethods() ) {
            if ( listenerEventMethodName.equals( method.getName() ) ) {
                if ( method.getParameterTypes().length != 1 ) {
                    throw new ActionBinderException( config, "There is a listener event method with name \"" + listenerEventMethodName
                            + "\", but number of parameters don't march: expected 1, actual" + method.getParameterTypes().length );
                }

                return method;
            }
        }
        throw new ActionBinderException( config, "There is no listener event method with name \"" + listenerEventMethodName + "\"" );
    }

    private Class getInterfaceClassByName( String listenerClassName ) throws ActionBinderException {
        Class interfaceClass;
        try {
            interfaceClass = getClass().getClassLoader().loadClass( listenerClassName );
        } catch ( ClassNotFoundException e ) {
            throw new ActionBinderException( config, e );
        }

        if ( !interfaceClass.isInterface() ) {
            throw new ActionBinderException( config, "Class \"" + listenerClassName + "\" should be interface." );
        }

        return interfaceClass;
    }

    public static void bind( Object o ) throws RuntimeException {
        Arguments.assertNotNull( "o", o );
        try {
            Config config = Config.fromAnnotations( o.getClass() );
            new ActionBinder( config, o ).bind();
        } catch ( ConfigException e ) {
            throw new RuntimeException( e );
        } catch ( ActionBinderException e ) {
            throw new RuntimeException( e );
        }
    }
}
