package ja.centre.gui.concurrent;

import ja.centre.util.assertions.States;
import ja.centre.util.lang.Reflector;

import javax.swing.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class EdtWrapper {
    /**
     * Non-blocking version of "waiting".
     * Calling on proxy methods will always get null.
     * Nevertheless, execution of delegate's methods is schdeduled to EDT for late.
     *
     * @param delegate
     * @return proxy
     */
    public static Object nonWaiting( final Object delegate ) {
        InvocationHandler handler = new InvocationHandler() {
            public Object invoke( Object proxy, Method method, final Object[] args ) throws Throwable {
                SwingUtilities.invokeLater( new Call( method, delegate, args ) );
                return null;
            }
        };
        return createProxy( delegate, handler );
    }

    /**
     * Creates a proxy which implements all interfaces of the delegate and
     * that transforms all calls for delegate to be executed in EDT.
     *
     * During execution, the caller thread is blocked.
     * The result returned by delegate's methods is awaited and returned to caller thread.
     *
     * @param delegate
     * @return proxy
     */
    public static Object waiting( final Object delegate ) {
        InvocationHandler handler = new InvocationHandler() {
            public Object invoke( Object proxy, final Method method, final Object[] args ) throws Throwable {
                Call call = new Call( method, delegate, args );
                SwingUtilities.invokeAndWait( call );
                return call.getResult();
            }
        };
        return createProxy( delegate, handler );
    }

    private EdtWrapper() {
    }

    private static Object createProxy( Object delegate, InvocationHandler handler ) {
        //noinspection unchecked
        return Proxy.newProxyInstance(
                EdtWrapper.class.getClassLoader(),
                Reflector.collectInterfaces( delegate.getClass() ),
                handler
        );
    }

    private static class Call implements Runnable {
        private final Method method;
        private final Object delegate;
        private final Object[] args;

        private Object result;

        public Call( Method method, Object delegate, Object[] args ) {
            this.method = method;
            this.delegate = delegate;
            this.args = args;
        }
        public void run() {
            try {
                result = method.invoke( delegate, args );
            } catch ( IllegalAccessException e ) {
                States.doThrow( "Could not call delegate", e );
            } catch ( InvocationTargetException e ) {
                States.doThrow( "Could not call delegate", e );
            }
        }
        public Object getResult() {
            return result;
        }
    }
}
