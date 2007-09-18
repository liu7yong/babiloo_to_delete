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

package ja.lingo.engine.mergedindex;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.centre.util.io.intio.IIntReader;
import ja.centre.util.io.intio.IntBufferIntReader;
import ja.centre.util.io.nio.MappedByteBufferWrapper;
import ja.centre.util.io.Files;
import ja.lingo.engine.beans.IArticle;
import ja.lingo.engine.dictionaryindex.reader.IDictionaryIndex;
import ja.lingo.engine.util.slice.ByteBufferSliceReader;
import ja.lingo.engine.util.slice.ISliceReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ChannelMergedIndex implements IMergedIndex {
    private static final Log LOG = LogFactory.getLog( ChannelMergedIndex.class );

    private static final int SLICE_READER_INT_ID_TO_READER_ID_MAP   = 0;
    private static final int SLICE_GROUPS_INDEX_LENGTH_LIST         = 1;
    private static final int SLICE_GROUPS_LIST                      = 2;

    private String fileName;

    private MappedByteBufferWrapper fisMappedByteBufferWrapper;

    private IIntReader groupsIndexLengthIntReader;
    private IIntReader groupsIntReader;

    private Map<Integer, IDictionaryIndex> readerIntIdToReaderMap;

    private boolean closed;

    public ChannelMergedIndex( String fileName, Map<String, IDictionaryIndex> indexFileNameToReaderMap ) throws IOException {
        Arguments.assertNotNull( "fileName", fileName );
        Arguments.assertNotNull( "indexFileNameToReaderMap", indexFileNameToReaderMap );

        this.fileName = fileName;

        fisMappedByteBufferWrapper = new MappedByteBufferWrapper( fileName );

        ISliceReader sliceReader = new ByteBufferSliceReader( fisMappedByteBufferWrapper.getMappedByteBuffer() );


        // read position + fill readerIntIdToReaderMap
        {
            ByteBuffer buffer = sliceReader.getSlice( SLICE_READER_INT_ID_TO_READER_ID_MAP );
            byte[] bytes = new byte[buffer.limit()];
            buffer.position( 0 );
            buffer.get( bytes );

            try {
                readerIntIdToReaderMap = _deserializeReaderIdToReaderMap(
                        new DataInputStream( new ByteArrayInputStream( bytes ) ),
                        indexFileNameToReaderMap );
            } catch ( IOException e ) {
                Files.close( fisMappedByteBufferWrapper );
                throw e;
            }
        }

        groupsIndexLengthIntReader  = new IntBufferIntReader( sliceReader.getSlice( SLICE_GROUPS_INDEX_LENGTH_LIST ).asIntBuffer() );
        groupsIntReader             = new IntBufferIntReader( sliceReader.getSlice( SLICE_GROUPS_LIST ).asIntBuffer() );
    }

    public IArticle getArticle( int index ) {
        assertNotClosed();

        int groupsIndex  = groupsIndexLengthIntReader.get( index * 2 );
        int groupsLength = groupsIndexLengthIntReader.get( index * 2 + 1 );

        Article article = new Article( groupsLength );

        for ( int i = 0; i < groupsLength; i++ ) {
            int readerId      = groupsIntReader.get( ( i + groupsIndex ) * 2 );
            int inReaderIndex = groupsIntReader.get( ( i + groupsIndex ) * 2 + 1 );

            IDictionaryIndex reader = (IDictionaryIndex) readerIntIdToReaderMap.get( new Integer( readerId ) );

            if ( reader == null ) {
                States.doThrow( "Could not retrieve reader for id=" + readerId );
            }

            article.set( i, reader, inReaderIndex );
        }

        return article;
    }

    public String getArticleTitle( int index ) {
        assertNotClosed();

        int groupsIndex  = groupsIndexLengthIntReader.get( index * 2 );

        int readerId    = groupsIntReader.get( ( 0 + groupsIndex ) * 2 );
        int inReaderIndex = groupsIntReader.get( ( 0 + groupsIndex ) * 2 + 1 );

        IDictionaryIndex reader = (IDictionaryIndex) readerIntIdToReaderMap.get( readerId );

        return reader.getTitle( inReaderIndex );
    }

    public int size() {
        assertNotClosed();

        return groupsIndexLengthIntReader.size() / 2;
    }

    public void close() throws IOException {
        assertNotClosed();

        LOG.info( "Closing \"" + fileName + "\"..." );
        fisMappedByteBufferWrapper.close();
        fisMappedByteBufferWrapper = null;

        closed = true;
    }

    private void assertNotClosed() {
        States.assertFalse( closed, "Expected to be not closed" );
    }

    private static Map<Integer, IDictionaryIndex> _deserializeReaderIdToReaderMap( DataInputStream dis, Map<String, IDictionaryIndex> indexFileNameToReaderMap ) throws IOException {
        Map<Integer, IDictionaryIndex> readerIdToReaderMap = new HashMap<Integer, IDictionaryIndex>();

        int readerCount = dis.readInt();

        for ( int i = 0; i < readerCount; i++ ) {
            int    readerId         = dis.readInt();
            String indexFileName    = dis.readUTF();

            IDictionaryIndex reader = indexFileNameToReaderMap.get( indexFileName );

            if ( reader == null ) {
                throw new IOException( "Unable to retrieve reader with readerId=\""
                        + readerId + "\" and indexFileName=\"" + indexFileName + "\"" );
            }

            readerIdToReaderMap.put( readerId, reader );
        }

        return readerIdToReaderMap;
    }
}
