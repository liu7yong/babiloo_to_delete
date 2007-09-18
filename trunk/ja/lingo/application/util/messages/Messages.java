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

package ja.lingo.application.util.messages;

import ja.centre.gui.resources.Resources;
import ja.centre.util.assertions.Arguments;
import ja.lingo.engine.util.EngineFiles;

import javax.swing.*;
import java.awt.*;

/**
 * All methods may be called from any thread
 */
public class Messages {
    private static Resources resources = Resources.forProperties( Messages.class );

    private Messages() {
    }

    public static void info( Component invoker, String message ) {
        info( invoker, message, resources.text( "information" ) );
    }
    public static void info( Component invoker, String message, String title ) {
        message( invoker, message, title, JOptionPane.INFORMATION_MESSAGE, null );
    }

    public static void error( Component invoker, String message, String title ) {
        message( invoker, message, title, JOptionPane.ERROR_MESSAGE, null );
    }

    public static boolean confirm( Component invoker, String message, String title ) {
        return confirm( invoker, message, title, resources.text( "confirmation_confirm" ) );
    }
    public static boolean confirm( Component invoker, String message, String title, String optionConfirm ) {
        return confirm( invoker, message, title, optionConfirm,
                resources.text( "confirmation_cancel" ) );
    }
    public static boolean confirm( Component invoker, String message, String title, String optionConfirm, String optionCancel ) {
        Arguments.assertNotNull( "invoker", invoker );
        Arguments.assertNotNull( "message", message );
        Arguments.assertNotNull( "title", title );


        return JOptionPane.showOptionDialog(
                invoker,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{
                        optionConfirm,
                        optionCancel },
                optionConfirm ) == JOptionPane.YES_OPTION;
    }

    public static void internalError( Component invoker, Throwable t ) {
        Messages.message( invoker,
                resources.text( "internalError_message", ErrorDumper.dump( t ), EngineFiles.calculateWorking() ),
                resources.text( "internalError_title" ),
                JOptionPane.ERROR_MESSAGE,
                resources.icon( "internalError" ) );
    }

    // common messages
    public static void infoArticleNotFound( Component invoker, String articleTitle ) {
        info( invoker, resources.text( "infoArticleNotFound", articleTitle ) );
    }

    private static void message( final Component invoker, final String message, final String title, final int type, final Icon icon ) {
        if ( SwingUtilities.isEventDispatchThread() ) {
            message0( invoker, message, title, type, icon );
        } else {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    message0( invoker, message, title, type, icon );
                }
            } );
        }
    }

    private static void message0( Component invoker, String message, String title, int type, Icon icon ) {
        Arguments.assertNotNull( "invoker", invoker );
        Arguments.assertNotNull( "message", message );
        Arguments.assertNotNull( "title", title );

        JOptionPane.showMessageDialog(
                invoker,
                message,
                title,
                type,
                icon );
    }
}
