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

package ja.centre.util.io.linereader;

import ja.centre.util.io.ByteArray;
import ja.centre.util.io.nio.MappedByteBufferWrapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LineReader implements ILineReader {
    private static final byte BYTE_FF = (byte) 0xFF;
    private static final byte BYTE_FE = (byte) 0xFE;

    private MappedByteBufferWrapper bufferWrapper;
    private ByteBuffer buffer;

    private int bytesRead;
    private int bytesReadSkip;

    private int shift;
    private int shift2;
    private boolean unicode;

    private ByteArray lineBytes = new ByteArray();

    public LineReader( String fileName ) throws IOException {
        bufferWrapper = new MappedByteBufferWrapper( fileName );
        init( bufferWrapper.getMappedByteBuffer() );
    }

    // for test purposes only
    LineReader( ByteBuffer buffer ) {
        init( buffer );
    }

    private void init( ByteBuffer buffer ) {
        this.buffer = buffer;

        byte b0 = buffer.get( 0 );
        byte b1 = buffer.get( 1 );

        if ( b0 == BYTE_FF && b1 == BYTE_FE ) {
            // little endian (default in notepad.exe)
            unicode = true;
            buffer.order( ByteOrder.LITTLE_ENDIAN );
        } else if ( b0 == BYTE_FE && b1 == BYTE_FF ) {
            // big endian
            unicode = true;
            //it is already BIG_ENDIAN by default in Java NIO
        } else {
            // check UTF8
        }

        shift = unicode ? 2 : 1;
        shift2 = unicode ? 4 : 2;
        bytesReadSkip = unicode ? 2 : 0;
    }

    // TODO optimize? parse(2753) - english3
    public ByteArray readLine() throws IOException {
        if ( bytesReadSkip != 0 ) {
            bytesRead += bytesReadSkip;
            bytesReadSkip = 0;
        }

        if ( bytesRead == buffer.limit() ) {
            return null;
        }

        int pointer = bytesRead;
        char b;
        buffer.position( pointer );

        while ( buffer.remaining() > 0 ) {
            // TODO optimize? parse(590) - english3
            if ( unicode ) {
                b = buffer.getChar();// TODO optimize? bottleneck for DSL parsing
                pointer += 2;
            } else {
                b = (char) buffer.get();
                pointer++;
            }

            if ( b == '\r' && buffer.remaining() > 0 ) {
                if ( buffer.get() == '\n' ) {
                    pointer += shift;
                    bytesReadSkip = shift2;
                    break;
                } else {
                    bytesReadSkip = shift;
                    pointer -= shift2;
                    break;
                }
            } else if ( b == '\n' ) {
                bytesReadSkip = shift;
                break;
            }
        }

        lineBytes.setLength( pointer - bytesRead - bytesReadSkip );
        buffer.position( bytesRead );
        buffer.get( lineBytes.getBytes(), 0, lineBytes.getLength() );

        bytesRead = pointer - bytesReadSkip;

        return lineBytes;
    }

    public int getLastLineEnd() {
        return bytesRead;
    }

    public int getNextLineStart() {
        return bytesRead + bytesReadSkip;
    }

    public void close() throws IOException {
        if ( bufferWrapper != null ) {
            bufferWrapper.close();
            bufferWrapper = null;
        }
    }


    public String toString() {
        return "LineReader{" +
                ", bytesRead=" + bytesRead +
                ", bytesReadSkip=" + bytesReadSkip +
                '}';
    }
}
