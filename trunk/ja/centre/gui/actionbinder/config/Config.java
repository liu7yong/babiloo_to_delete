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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class Config {
    private List<Listener> listeners = new ArrayList<Listener>();
    private Class guiClass;

    public static Config fromAnnotations( Class guiClass ) throws ConfigException {
        return new Config( guiClass );
    }

    public List<Listener> getListeners() {
        return Collections.unmodifiableList( listeners );
    }

    void addListener( Listener listener ) {
        Arguments.assertNotNull( "listener", listener );
        listeners.add( listener );
    }
    void removeListener( Listener listener ) {
        Arguments.assertNotNull( "listener", listener );
        listeners.remove( listener );
    }

    private Config( Class guiClass ) throws ConfigException {
        Arguments.assertNotNull( "guiClass", guiClass );
        this.guiClass = guiClass;

        for ( Field field: Arrays.asList( guiClass.getDeclaredFields() ) ) {
            if ( field.isAnnotationPresent( NListenerGroup.class ) ) {
                if ( field.isAnnotationPresent( NListener.class ) ) {
                    ConfigException.doThrow( "There are both @NListenerGroup and @NListener annotations" +
                            " for field \"" + field.getName() + "(...)\". Should be only one of them declared per method." +
                            " Alternatively, any number of @NListener annotations may be nested in @NListenerGroup",
                            guiClass );
                }

                for ( NListener listenerAnn : field.getAnnotation( NListenerGroup.class ).value() ) {
                    addListener( guiClass, field, listenerAnn );
                }
            } else if ( field.isAnnotationPresent( NListener.class ) ) {
                addListener( guiClass, field, field.getAnnotation( NListener.class ));
            }
        }
    }

    private void addListener( Class guiClass, Field field, NListener listenerAnn ) throws ConfigException {
        String component = field.getName();
        if ( listenerAnn.property().length() != 0 ) {
            component = component + "." + listenerAnn.property();
        }

        addListener( createListener( component, listenerAnn.type(), listenerAnn.mappings(), guiClass ) );
    }
    private Listener createListener( String component, Class listenerType, String[] mappings, Class guiClass ) throws ConfigException {
        Listener listener = new Listener();
        listener.setComponent( component );
        listener.setType( listenerType.getName() );

        if ( mappings.length == 0 ) {
            ConfigException.doThrow( "There are no any mappings", guiClass, component, listenerType );
        }

        for ( String mapping : mappings ) {
            String[] mappingTokens = mapping.split( "\\>" );

            if ( mappingTokens.length < 2 ) {
                ConfigException.doThrow( "There are " + mappingTokens.length + " item(s) in mapping. Should be 2 at least (e.g. \"actionPerformed > show\")",
                        guiClass, component, listenerType, mapping );
            }

            listener.addEvent( createEvent( mappingTokens ) );
        }
        return listener;
    }
    private Event createEvent( String[] mappingTokens ) {
        Event event = null;
        ActionContainer lastActionContainer = null;
        for ( int i = 0; i < mappingTokens.length; i++ ) {
            String mappingToken = mappingTokens[i].trim();

            if ( i == 0 ) {
                event = new Event();
                event.setMethod( mappingToken );
                lastActionContainer = event;
            } else {
                Action action;
                if ( i == mappingTokens.length - 1 ) {
                    action = Action.logic();
                } else {
                    action = Action.condition();
                }

                action.setMethod( mappingToken );
                lastActionContainer.setAction( action );
                lastActionContainer = action;
            }
        }
        return event;
    }


    public String toString() {
        return "Config{" + guiClass + '}';
    }
}
