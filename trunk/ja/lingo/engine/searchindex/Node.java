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

import ja.centre.util.arrays.ArrayUtil;

class Node extends ANode implements IMutableNode {
    protected INode[] children;
    protected static final INode[] NO_CHILDREN = new INode[0];

    private Node( char key, int value ) {
        this.key = key;
        this.value = value;
        this.children = NO_CHILDREN;
    }

    public void addChild( INode child ) {
        INode[] newChildren = new INode[children.length + 1]; // TODO optimize: produces lots of garbage
        System.arraycopy( children, 0, newChildren, 0, children.length );

        newChildren[newChildren.length - 1] = child;

        children = newChildren;
    }
    public void removeChild( INode child ) {
        int index = ArrayUtil.indexOf( children, child );
        if ( index != -1 ) {
            INode[] newChildren = new INode[children.length - 1]; // TODO optimize: produces lots of garbage

            if ( index != 0 ) {
                System.arraycopy( children, 0, newChildren, 0, index );
            }

            if ( index != children.length - 1 ) {
                System.arraycopy( children, index + 1, newChildren, index, newChildren.length - index );
            }

            children = newChildren;
        }
    }

    public int childrenCount() {
        return children.length;
    }

    public INode getChild( int index ) {
        return children[index];
    }



    public static IMutableNode createRoot() {
        return new Node( '\u0000', 0 );
    }

    public static IMutableNode create( char key, int value ) {
        return new Node( key, value );
    }
}
