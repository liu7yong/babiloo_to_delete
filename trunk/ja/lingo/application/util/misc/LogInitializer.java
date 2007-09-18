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

package ja.lingo.application.util.misc;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

public class LogInitializer {
    private static boolean initialized;

    public static final char[] SPACES = new char[35];
    private static final char DOT = '.';

    static {
        for ( int i = 0; i < SPACES.length; i++ ) {
            SPACES[i] = ' ';
        }
    }

    /**
     * Configures JDK logger
     */
    public static void initialize() {
        if ( initialized ) {
            return;
        }

        // create cute formatter
        final Formatter formatter = new Formatter() {
            public String format( LogRecord record ) {
                StringBuilder sb2 = new StringBuilder();
                // class or logger
                if ( record.getSourceClassName() != null ) {
                    String sourceClassName = record.getSourceClassName();
                    int dotLastIndex = sourceClassName.lastIndexOf( DOT );
                    if ( dotLastIndex > 1 ) {
                        dotLastIndex = sourceClassName.lastIndexOf( DOT, dotLastIndex - 1 );
                    }

                    sb2.append( sourceClassName.substring( dotLastIndex + 1 ) );
                } else {
                    sb2.append( record.getLoggerName() );
                }

                // method
                //if ( record.getSourceMethodName() != null ) {
                //    sb2.append( " " );
                //    sb2.append( record.getSourceMethodName() );
                //}
                String source = sb2.toString();

                // full message
                StringBuilder sb = new StringBuilder();
                sb.append( record.getLevel().getLocalizedName().charAt( 0 ) );
                sb.append( ":[" );
                sb.append( source );
                if ( source.length() < SPACES.length ) {
                    sb.append( SPACES, 0, SPACES.length - source.length() );
                }
                sb.append( "] " );

                sb.append( formatMessage( record ) );
                sb.append( "\n" );

                // exception
                if ( record.getThrown() != null ) {
                    try {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter( sw );
                        record.getThrown().printStackTrace( pw );
                        pw.close();
                        sb.append( sw.toString() );
                    } catch ( Exception ex ) {
                    }
                }
                return sb.toString();
            }
        };


        // disable all handlers of root logger
        Logger logger = LogManager.getLogManager().getLogger( "" );
        for ( Handler handler1 : logger.getHandlers() ) {
            logger.removeHandler( handler1 );
        }

        // set altered version of console handler which uses custom formatter and prints to SDOUT
        logger.addHandler( new ConsoleHandler() {
            public void setFormatter( Formatter newFormatter ) throws SecurityException {
                super.setFormatter( formatter );
            }
            protected synchronized void setOutputStream( OutputStream out ) throws SecurityException {
                super.setOutputStream( System.out );
            }
        } );

        initialized = true;
    }
}
