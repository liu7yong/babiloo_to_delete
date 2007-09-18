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

package ja.lingo.engine.util;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.io.Files;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class EngineFiles {
    private static final Log LOG = LogFactory.getLog( EngineFiles.class );

    private static final int COPY_BUFFER_SIZE = 32768;

    public static long tempFileSuffixCounter = System.currentTimeMillis(); // TODO not very robust generator

    private EngineFiles() {
    }

    public static String calculateInWorking( String fileName ) {
        Arguments.assertNotNull( "fileName", fileName );

        return calculateWorking() + File.separator + fileName;
    }

    public static String calculateWorking() {
        return Files.calculateFileNameInUserHome( ".jalingo" );
    }

    public static String createTemp( String prefix ) throws IOException {
        return createTempFile( prefix ).getAbsolutePath();
    }
    public static File createTempFile( String prefix ) throws IOException {
        String tempDir = calculateTemp();

        Files.ensureDirectoryExists( tempDir );

        String fileName = tempDir + File.separator + prefix + "." + tempFileSuffixCounter++;

        File file = new File( fileName ).getAbsoluteFile();
        file.deleteOnExit();
        return file;
    }

    private static String calculateTemp() {
        return calculateInWorking( "temp" );
    }

    public static void appendFileAndDelete( String fileName, FileOutputStream fos ) throws IOException {
        appendFile( fileName, fos, false );
    }

    public static void appendFileLengthWithContentAndDelete( String fileName, FileOutputStream fos ) throws IOException {
        appendFile( fileName, fos, true );
    }

    private static void appendFile( String fileName, FileOutputStream fos, boolean prependWithLength ) throws IOException {
        FileInputStream fis = new FileInputStream( fileName );
        FileChannel fic = fis.getChannel();
        FileChannel foc = fos.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate( COPY_BUFFER_SIZE );

        // put header: length (1 int = 4 bytes)
        if ( prependWithLength ) {
            buffer.putInt( (int) new File( fileName ).length() );
        }

        // put body
        do  {
            buffer.flip();
            foc.write( buffer );
            buffer.clear();
        } while ( fic.read( buffer ) != -1 );
        fic.close();
        // NOTE: do not close 'foc'

        Files.delete( fileName );
    }

    public static void cleanTemp() {
        String temp = calculateTemp();

        LOG.info( "Cleaning temp directory \"" + temp + "\"" );
        try {
            Files.deleteRecusrsively( temp );
        } catch ( IOException e ) {
            LOG.warn( "Could not clean temp directory  \"" + temp + "\"" );
        }
    }
}
