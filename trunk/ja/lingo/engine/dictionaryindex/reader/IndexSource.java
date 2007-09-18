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

package ja.lingo.engine.dictionaryindex.reader;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.centre.util.io.intio.IntBufferIntReader;
import ja.centre.util.io.nio.MappedByteBufferWrapper;
import ja.lingo.engine.util.slice.ByteBufferSliceReader;
import ja.lingo.engine.util.slice.ISliceReader;

import java.io.IOException;

public class IndexSource implements IIndexSource {
    private static final int SLICE_TITLE  = 0;
    private static final int SLICE_BODIES = 1;

    private MappedByteBufferWrapper wrapper;

    private ITokenReader titlesTokenReader;
    private ITokenReader bodiesTokenReader;

    public IndexSource( MappedByteBufferWrapper wrapper ) {
        Arguments.assertNotNull( "wrapper", wrapper );
        this.wrapper = wrapper;

        ISliceReader sliceReader = new ByteBufferSliceReader( wrapper.getMappedByteBuffer() );

        titlesTokenReader = new TokenReader( new IntBufferIntReader( sliceReader.getSlice( SLICE_TITLE ).asIntBuffer() ) );
        bodiesTokenReader = new TokenReader( new IntBufferIntReader( sliceReader.getSlice( SLICE_BODIES ).asIntBuffer() ) );
    }

    public void close() throws IOException {
        States.assertNotNull( wrapper, "IndexSource expected to be not closed" );
        wrapper.close();
        wrapper = null;
    }

    public ITokenReader getTitlesTokenReader() {
        return titlesTokenReader;
    }
    public ITokenReader getBodiesTokenReader() {
        return bodiesTokenReader;
    }

    public String toString() {
        return "IndexSource{" +
                "wrapper=" + wrapper +
                '}';
    }
}
