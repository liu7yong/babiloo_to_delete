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

package ja.centre.util.io;

import ja.centre.util.assertions.Arguments;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Files {
    private static final Log LOG = LogFactory.getLog( Files.class );
    private static final int COPY_BUFFER_SIZE = 32768;

    private Files() {
    }

    public static void ensureDirectoryExists( String directoryName ) throws IOException {
        Arguments.assertNotNull( "directoryName", directoryName );

        File dirFile = new File( directoryName );
        if ( dirFile.exists() ) {
            if ( !dirFile.isDirectory() ) {
                throw new IOException( "File '" + directoryName
                        + "' already exists and it's not a directory. It should not exist, or should be a directoryName." );
            }
        } else if ( !dirFile.mkdirs() ) {
            throw new IOException( "Unable to create directory \"" + directoryName + "\"" );
        }
    }

    public static String getCanonicalPath( String fileName ) throws IOException {
        Arguments.assertNotNull( "fileName", fileName );

        return new File( fileName ).getCanonicalPath();
    }

    public static boolean exists( String fileName ) {
        Arguments.assertNotNull( "fileName", fileName );

        return new File( fileName ).exists();
    }

    public static void delete( String fileName ) throws FileNotFoundException, IOException {
        Arguments.assertNotNull( "fileName", fileName );

        // check existance
        if ( !exists( fileName ) ) {
            throw new FileNotFoundException( "File: \"" + fileName + "\"" );
        }

        // delete
        if ( !new File( fileName ).delete() ) {
            throw new IOException( "File \"" + fileName + "\" could not be deleted due to unknown obstacle " );
        }
    }

    public static void deleteQuietly( String fileName ) {
        Arguments.assertNotNull( "fileName", fileName );

        // check existance
        if ( !exists( fileName ) ) {
            LOG.warn( "File: \"" + fileName + "\"" );
        }

        // delete
        if ( !new File( fileName ).delete() ) {
            LOG.warn( "File \"" + fileName + "\" could not be deleted due to unknown obstacle " );
        }
    }

    public static void rename( String fileName, String newFileName ) throws IOException {
        if ( !new File( fileName ).renameTo( new File( newFileName ) ) ) {
            throw new IOException( "Unable to rename \"" + fileName
                    + "\" to \"" + newFileName + "\"" + newFileName + "\" due to unknown obstacle" );
        }
    }

    public static File createTempFile( String prefix ) throws IOException {
        return createTempFile( prefix, null );
    }

    public static File createTempFile( String prefix, String suffix ) throws IOException {
        File file = File.createTempFile( prefix, suffix );
        file.deleteOnExit();
        return file;
    }

    public static long length( String fileName ) {
        return new File( fileName ).length();
    }

    public static String calculateFileNameInUserHome( String fileName ) {
        Arguments.assertNotNull( "fileName", fileName );
        return System.getProperty( "user.home" ) + File.separator + fileName;
    }

    public static void deleteRecusrsively( String filenName ) throws IOException {
        Arguments.assertNotNull( "filenName", filenName );
        deleteRecusrsively( new File( filenName ) );
    }

    public static void deleteRecusrsively( File file ) throws IOException {
        if ( !file.exists() ) {
            return;
        }

        if ( file.isDirectory() ) {
            File[] childFiles = file.listFiles();

            for ( int i = 0; i < childFiles.length; i++ ) {
                File childFile = childFiles[i];
                deleteRecusrsively( childFile );
            }
        }

        if ( !file.delete() ) {
            throw new IOException( "Can't delete file \"" + file.getAbsolutePath() + "\"" );
        }
    }

    public static void makeDirs( String fileName ) throws IOException {
        File file = new File( fileName );
        if ( !file.mkdirs() ) {
            throw new IOException( "Can't make directories \"" + file.getAbsolutePath() + "\"" );
        }
    }

    public static String removeUnacceptableSymbols( String fileName ) {
        return fileName.replaceAll( "[/\\\\:\\*\\?\"<>|]", "-" );
    }

    public static File create( String fileName ) throws FileNotFoundException {
        Arguments.assertNotNull( "fileName", fileName );

        File file = new File( fileName );
        if ( !file.exists() ) {
            throw new FileNotFoundException( fileName );
        }
        return file;
    }

    public static void copy( String source, String destination ) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream( source );
            fos = new FileOutputStream( destination );

            FileChannel fic = null;
            FileChannel foc = null;
            try {
                fic = fis.getChannel();
                foc = fos.getChannel();

                ByteBuffer buffer = ByteBuffer.allocate( COPY_BUFFER_SIZE );
                do {
                    buffer.flip();
                    foc.write( buffer );
                    buffer.clear();
                } while ( fic.read( buffer ) != -1 );
            } finally {
                closeQuietly( fic );
                closeQuietly( foc );
            }
        } finally {
            closeQuietly( fis );
            closeQuietly( fos );
        }
    }

    public static void copy( InputStream is, OutputStream os ) throws IOException {
        byte[] buffer = new byte[COPY_BUFFER_SIZE];

        int read;
        while ( (read = is.read( buffer )) != -1 ) {
            os.write( buffer, 0, read );
        }
    }

    public static void closeQuietly( Closeable closable ) {
        try {
            close( closable );
        } catch ( IOException e ) {
            LOG.error( "Could not close closable \"" + closable + "\" of class \""
                    + closable.getClass().getName() + "\"", e );
        }
    }

    public static void close( Closeable closable ) throws IOException {
        if ( closable != null ) {
            closable.close();
        }
    }

    public static byte[] readAsBytes( String fileName ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream( fileName );
            copy( fis, baos );
        } finally {
            closeQuietly( fis );
        }
        return baos.toByteArray();
    }
    public static void assertExists( String fileName ) throws FileNotFoundException {
        if ( !exists( fileName ) ) {
            throw new FileNotFoundException( fileName );
        }
    }
}
