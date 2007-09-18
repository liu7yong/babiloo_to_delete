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

package ja.centre.util.io.nio;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.centre.util.io.Files;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Workaraund against J2SE BUG 4724038: "(fs) Add unmap method to MappedByteBuffer".
 * Method "clean" was taken from comments from post:<br>
 * <br>
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038
 */
public class MappedByteBufferWrapper implements Closeable {
    private static final Log LOG = LogFactory.getLog( MappedByteBufferWrapper.class );

    private RandomAccessFile raf;
    private MappedByteBuffer buffer;

    private String fileName;

    public MappedByteBufferWrapper( String fileName ) throws IOException {
        Arguments.assertNotNull( "fileName", fileName );

        Files.assertExists( fileName );

        LOG.info( "Opening \"" + fileName + "\"..." );
        this.fileName = fileName;

        try {
            raf = new RandomAccessFile( fileName, "rw" );

            try {
                initBuffer();
            } catch ( IOException e ) {
                LOG.info( "Could not lock file \"" + fileName + "\". Switching to non-locking mode..." );

                Files.closeQuietly( raf );
                raf = new RandomAccessFile( fileName, "r" );
                initBuffer();
            }
        } catch ( IOException e ) {
            Files.closeQuietly( raf );
            throw e;
        }
    }
    private void initBuffer() throws IOException {
        buffer = raf.getChannel().map( FileChannel.MapMode.READ_ONLY, 0, raf.getChannel().size() );
    }

    public MappedByteBuffer getMappedByteBuffer() {
        return buffer;
    }

    public void close() throws IOException {
        States.assertNotNull( buffer, "Expected to be not closed" );

        LOG.info( "Closing \"" + fileName + "\"..." );

        clean( buffer );
        buffer = null;

        //lock.release();

        raf.close();
        raf = null;
    }

    private void clean( final MappedByteBuffer buffer ) {
        AccessController.doPrivileged( new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    Method cleanerMethod = buffer.getClass().getMethod( "cleaner" );
                    cleanerMethod.setAccessible( true );
                    sun.misc.Cleaner cleaner = (sun.misc.Cleaner) cleanerMethod.invoke( buffer );
                    cleaner.clean();
                } catch ( IllegalAccessException e ) {
                    States.shouldNeverReachHere( e );
                } catch ( NoSuchMethodException e ) {
                    States.shouldNeverReachHere( e );
                } catch ( InvocationTargetException e ) {
                    States.shouldNeverReachHere( e );
                }
                return null;
            }
        } );
    }

    public String toString() {
        return "MappedByteBufferWrapper{" +
                "fileName='" + fileName + '\'' +
                '}';
    }
}
