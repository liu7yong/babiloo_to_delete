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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Invokers {
    private Invokers() {
    }

    public static Invoker action( Object instance, Method actionMethod ) {
        return new ActionMethodInvoker( instance, actionMethod );
    }

    public static Invoker condition( Object instance, Method actionMethod, Invoker delegateMethodInvoker ) {
        return new ConditionMethodInvoker( instance, actionMethod, delegateMethodInvoker );
    }

    private static class ActionMethodInvoker implements Invoker {
        private Object instance;
        private Method method;

        public ActionMethodInvoker( Object instance, Method method ) {
            Arguments.assertNotNull( "instance", instance );
            Arguments.assertNotNull( "method", method );

            this.instance = instance;
            this.method = method;
        }

        public Object invoke( Object... args ) throws InvokerException {
            // adjust arguments for no-arg case
            if ( method.getParameterTypes().length == 0 ) {
                args = new Object[] { };
            }

            // invoke
            try {
                return method.invoke( instance, args );
            } catch ( IllegalAccessException e ) {
                throw new InvokerException( instance, method, e );
            } catch ( InvocationTargetException e ) {
                throw new InvokerException( instance, method, e );
            }
        }
    }

    private static class ConditionMethodInvoker implements Invoker {
        private Object instance;

        private Method method;
        private Invoker delegateMethodInvoker;

        public ConditionMethodInvoker( Object instance, Method method, Invoker delegateMethodInvoker ) {
            Arguments.assertNotNull( "instance", instance );
            Arguments.assertNotNull( "method", method );
            Arguments.assertNotNull( "delegateMethodInvoker", delegateMethodInvoker );

            // check method returns boolean
            if ( !Boolean.class.equals( method.getReturnType() ) && !boolean.class.equals( method.getReturnType() ) ) {
                Arguments.doThrow( "Method \"" + method.getName()
                        + "(...)\" of class \"" + instance.getClass().getName()
                        + "\" expected to have return value of type \"" + Boolean.class.getName()
                        + "\" or \"boolean\", but found type of return value is \""
                        + method.getReturnType().getName() + "\"" );
            }

            this.instance = instance;
            this.method = method;
            this.delegateMethodInvoker = delegateMethodInvoker;
        }

        public Object invoke( Object... args ) throws InvokerException {
            // adjust arguments for no-arg case
            if ( method.getParameterTypes().length == 0 ) {
                args = new Object[] { };
            }

            // invoke condition
            Boolean isConditionPassed;
            try {
                isConditionPassed = (Boolean) method.invoke( instance, args );
            } catch ( IllegalAccessException e ) {
                throw new InvokerException( e );
            } catch ( InvocationTargetException e ) {
                throw new InvokerException( e );
            }

            // invoke nested action with same arguments if condition is passed
            if ( isConditionPassed.booleanValue() ) {
                return delegateMethodInvoker.invoke( args );
            }

            // do nothing
            return null;
        }
    }
}
