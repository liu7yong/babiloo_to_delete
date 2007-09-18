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

abstract class ANode implements INode {
    protected char key;
    protected int value;

    public boolean isLeaf() {
        return childrenCount() == 0;
    }

    // TODO probably, rewrite in LazyNode for better performance
    public INode findChild( char key ) {
        for ( int i = 0; i < childrenCount(); i++ ) {
            if ( getChild( i ).getKey() == key ) {
                return getChild( i );
            }
        }
        return null;
    }

    public char getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public boolean equals( Object obj ) {
        if ( obj == this ) {
            return true;
        }

        if ( !(obj instanceof INode) ) {
            return false;
        }

        INode node = (INode) obj;

        return key == node.getKey() && value == node.getValue();
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + key;
        result = 37 * result + value;
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append( value )
                .append( ": " );
        INode node = this;
        for( ;; ) {
            builder.append( node.getKey() );
            if ( node.isLeaf() ) {
                break;
            }
            node = node.getChild( 0 );
        }
        return builder.toString();
    }
}
