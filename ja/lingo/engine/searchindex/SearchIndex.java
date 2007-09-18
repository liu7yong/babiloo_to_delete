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

package ja.lingo.engine.searchindex;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.centre.util.io.nio.MappedByteBufferWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Comparator;

public class SearchIndex extends BaseSearchIndex {
    private static final Log LOG = LogFactory.getLog( SearchIndex.class );

    private MappedByteBufferWrapper bufferWrapper;

    private static final int PRELOAD_DEEP = 3;
    private int memoryNodesLoaded;
    private int lazyNodesLoaded;

    private int offset;

    public SearchIndex( String fileName, Comparator<String> titleComparator ) throws IOException {
        Arguments.assertNotNull( "fileName", fileName );

        bufferWrapper = new MappedByteBufferWrapper( fileName );
        rootNode = _deserialize( bufferWrapper.getMappedByteBuffer().asIntBuffer(), PRELOAD_DEEP );

        LOG.info( "Count of memory nodes: " + memoryNodesLoaded + " (within deep of " + PRELOAD_DEEP + ")" );
        LOG.info( "Count of lazy-loading nodes: " + lazyNodesLoaded );
    }

    private INode _deserialize( IntBuffer buffer, int deep ) throws IOException {
        if ( deep > 0 ) {
            memoryNodesLoaded++;

            //int size = buffer.get( offset );
            int childrenCount = buffer.get( offset + 1 );
            char key = (char) buffer.get( offset + 2 );
            int value = buffer.get( offset + 3 );

            offset += 4;

            IMutableNode node = Node.create( key, value );

            deep--;
            for ( int i = 0; i < childrenCount; i++ ) {
                node.addChild( _deserialize( buffer, deep ) );
            }
            return node;
        } else {
            lazyNodesLoaded++;
            INode node = new LazyNode( buffer, offset );

            int size = buffer.get( offset );
            offset += size >> 2;

            return node;
        }
    }

    public void close() throws IOException {
        assertNotClosed();
        bufferWrapper.close();
        bufferWrapper = null;

        rootNode = null;
    }

    protected void assertNotClosed() {
        States.assertNotNull( bufferWrapper, "Expected to be not closed" );
    }
}
