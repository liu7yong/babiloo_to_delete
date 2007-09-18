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

package ja.lingo.engine.util.slice;

import ja.centre.util.assertions.Arguments;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ByteBufferSliceReader implements ISliceReader {
    private List<ByteBuffer> subBuffers;

    public ByteBufferSliceReader( ByteBuffer buffer ) {
        Arguments.assertNotNull( "buffer", buffer );

        subBuffers = new ArrayList<ByteBuffer>();

        int position = 0;
        while ( position < buffer.limit() ) {
            int size = buffer.getInt( position );
            position += 4;
            subBuffers.add( createSubBuffer( buffer, position, size ) );
            position += size;
        }
    }

    public ByteBuffer getSlice( int index ) {
        return (ByteBuffer) subBuffers.get( index );
    }

    private ByteBuffer createSubBuffer( ByteBuffer buffer, int offset, int size ) {
        buffer.limit( offset + size );
        buffer.position( offset );

        ByteBuffer slice = buffer.slice();

        buffer.limit( buffer.capacity() );

        return slice;
    }

}
