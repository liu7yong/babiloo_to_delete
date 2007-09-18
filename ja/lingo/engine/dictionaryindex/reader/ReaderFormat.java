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
import ja.centre.util.io.ByteArray;
import ja.centre.util.io.Files;
import ja.centre.util.io.nio.MappedByteBufferWrapper;
import ja.lingo.engine.dictionaryindex.Token;

import java.io.IOException;

public class ReaderFormat {
    private IDataSource dataSource;

    private boolean closed;

    private DataResolver titleDataResolver;
    private DataResolver bodyDataResolver;
    private IIndexSource indexSource;

    public ReaderFormat( String indexFileName, String dataFileName ) throws IOException {
        // TODO close opened channels (close index, if data raised an exception)
        this(
                new IndexSource( new MappedByteBufferWrapper( indexFileName ) ),
                new DataSource( dataFileName ) );
    }
    public ReaderFormat( IIndexSource indexSource, IDataSource dataSource ) {
        Arguments.assertNotNull( "indexSource", indexSource );
        Arguments.assertNotNull( "dataSource", dataSource );

        this.indexSource = indexSource;
        this.dataSource = dataSource;

        titleDataResolver = new DataResolver( indexSource.getTitlesTokenReader(), dataSource );
        bodyDataResolver = new DataResolver( indexSource.getBodiesTokenReader(), dataSource );
    }

    public boolean isClosed() {
        return closed;
    }
    public void close() throws IOException {
        States.assertFalse( closed, "ReaderFormat expected to be not closed" );

        Files.closeQuietly( indexSource );
        Files.closeQuietly( dataSource );

        indexSource = null;
        dataSource = null;
        closed = true;
    }

    public int size() {
        return titleDataResolver.size();
    }

    public void readTitle( int index, ByteArray byteArray ) {
        titleDataResolver.getData( index, byteArray );
    }
    public void readBody( int index, ByteArray byteArray ) {
        bodyDataResolver.getData( index, byteArray );
    }
    public Token getTitleToken( int index ) {
        return indexSource.getTitlesTokenReader().getToken( index );
    }
    public Token getBodyToken( int index ) {
        return indexSource.getBodiesTokenReader().getToken( index );
    }

    public String toString() {
        return "ReaderFormat{" +
                "indexSource=" + indexSource +
                '}';
    }
}
