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

import java.nio.IntBuffer;

class LazyNode extends ANode {
    private IntBuffer buffer;
    private int offset;

    // TODO probably, leave initialization of array for later (to save RAM)??
    // TODO rewrite with weak (or soft?) references
    private INode[] children;

    public LazyNode( IntBuffer byteBuffer, int offset ) {
        this.buffer = byteBuffer;
        this.offset = offset;

        //int size = byteBuffer.get( offset );
        int childrenCount = byteBuffer.get( offset + 1 );
        key = (char) byteBuffer.get( offset + 2 );
        value = byteBuffer.get( offset + 3 );

        children = new INode[childrenCount];
    }

    public int childrenCount() {
        return children.length;
    }

    public INode getChild( int index ) {
        if ( children[index] == null ) {
            children[index] = spawnChild( index );
        }

        return children[index];
    }

    private LazyNode spawnChild( int index ) {
        int childOffset = offset;

        childOffset += 4; // points to first child

        while ( index > 0 ) {
            childOffset += buffer.get( childOffset ) >> 2;
            index--;
        }

        return new LazyNode( buffer, childOffset );
    }
}
