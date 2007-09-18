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

import ja.centre.util.io.Files;
import ja.lingo.application.JaLingoInfo;
import ja.lingo.engine.util.EngineFiles;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

class ErrorDumper {
    private static final Log LOG = LogFactory.getLog( ErrorDumper.class );

    private static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat( "'jalingo-error-'yyyyMMdd-HHmmss'.log'" );

    private ErrorDumper() {
    }

    public static String dump( Throwable t ) {
        LOG.error( "Internal error occured", t );

        String fileName = EngineFiles.calculateInWorking( "log"  );

        try {
            Files.ensureDirectoryExists( fileName );
        } catch ( IOException e ) {
            log( fileName, e, t );
        }
        fileName = new File( fileName, FILE_NAME_FORMAT.format( new Date() ) ).toString();

        PrintStream ps = null;
        try {
            ps = new PrintStream( new FileOutputStream( fileName ) );
            ps.println( "JaLingo Internal Error Log" );
            ps.println( "==========================");
            ps.println( "Version  : " + JaLingoInfo.VERSION );
            ps.println( "Exception: ..." );
            t.printStackTrace( ps );
        } catch ( IOException e ) {
            log( fileName, e, t );
        } finally {
            if ( ps != null ) {
                ps.close();
            }
        }

        return fileName;
    }

    private static void log( String fileName, IOException e, Throwable t ) {
        LOG.error( "Could not save internal error to file \"" + fileName + "\"", e );
        LOG.error( "Unsaved internal error", t );
    }

    public static void main( String[] args ) {
        dump( new Throwable() );
    }
}
