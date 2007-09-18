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

package ja.lingo.readers.sdictionary;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.centre.util.io.nio.MappedByteBufferWrapper;
import ja.lingo.readers.sdictionary.compressor.Compressors;
import ja.lingo.readers.sdictionary.compressor.ICompressor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;

class SDictionaryDriver implements Closeable {
    private static final Log LOG = LogFactory.getLog( SDictionaryDriver.class );

    private static final String UTF_8 = "UTF-8";

    private MappedByteBufferWrapper wrapper;
    private MappedByteBuffer buffer;
    private String fileName;


    private String signature;
    private String inputLanguage;
    private String outputLanguage;
    private byte compression;
    private int capacity;
    private int shortIndexLength;
    private int titleUnitOffset;
    private int copyrightUnitOffset;
    private int versionUnitOffset;
    private int shortIndexOffset;
    private int fullIndexOffset;
    private int articlesOffset;

    private ICompressor compressor;

    public SDictionaryDriver( String fileName ) throws IOException {
        Arguments.assertNotNull( "fileName", fileName );
        this.fileName = fileName;

        try {
            wrapper = new MappedByteBufferWrapper( fileName );

            buffer = wrapper.getMappedByteBuffer();
            buffer.order( ByteOrder.LITTLE_ENDIAN );

            signature = readUtf8( 0x00, 4 );

            // TODO check signature

            inputLanguage = readUtf8( 0x04, 2 );
            outputLanguage = readUtf8( 0x07, 2 );
            compression = readByte( 0x0A );
            capacity = readInt( 0x0B );
            shortIndexLength = readInt( 0x0F );

            titleUnitOffset = readInt( 0x13 );
            copyrightUnitOffset = readInt( 0x17 );
            versionUnitOffset = readInt( 0x1B );
            shortIndexOffset = readInt( 0x1F );
            fullIndexOffset = readInt( 0x23 );
            articlesOffset = readInt( 0x27 );

            // bits 0-3 stands for compression method
            switch ( (byte) ( compression & 0x0F) ) {
            case 0:
                compressor = Compressors.none();
                break;
            case 1:
                compressor = Compressors.gzip();
                break;
            case 2:
                compressor = Compressors.bzip2();
                break;
            default:
                Arguments.doThrow( "Unknown compression menthod: " + compression );
            }
        } catch ( IOException e ) {
            if ( wrapper != null ) {
                try {
                    wrapper.close();
                } catch ( IOException e1 ) {
                    LOG.error( "Exception caught when tried to close wrapper", e1 );
                }
            }
            throw e;
        }
    }

    public void close() throws IOException {
        if ( wrapper != null ) {
            wrapper.close();
        }
    }

    public short readShort( int offset ) {
        return buffer.getShort( offset );
    }

    public byte[] readUnit( int offset ) {
        int length = readInt( offset );
        return read( offset + 4, length );
    }

    public String readUnitAsString( int offset ) throws IOException {
        return new String( compressor.uncompress( readUnit( offset ) ), UTF_8 );
    }

    public int readInt( int offset ) {
        return buffer.getInt( offset );
    }

    public byte readByte( int offset ) {
        return buffer.get( offset );
    }

    public String readUtf8( int offset, int length ) {
        return asUtf8( read( offset, length ) );
    }

    public String asUtf8( byte[] bytes ) {
        try {
            return new String( bytes, UTF_8 );
        } catch ( UnsupportedEncodingException e ) {
            throw States.shouldNeverReachHere( e );
        }
    }

    public byte[] read( int offset, int length ) {
        byte[] bytes = new byte[length];
        buffer.position( offset );
        buffer.get( bytes );
        return bytes;
    }

    // helper methods
    public String getTitle() throws IOException {
        return readUnitAsString( titleUnitOffset );
    }

    public String getVersion() throws IOException {
        return readUnitAsString( versionUnitOffset );
    }

    public String getCopyright() throws IOException {
        return readUnitAsString( copyrightUnitOffset );
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFullIndexOffset() {
        return fullIndexOffset;
    }

    public int getArticlesOffset() {
        return articlesOffset;
    }

    public ICompressor getCompressor() {
        return compressor;
    }
}
