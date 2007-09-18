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

import ja.lingo.engine.beans.IArticle;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.dictionaryindex.reader.IDictionaryIndex;

class Article implements IArticle {
    private static final int INDEX_READER = 0;
    private static final int INDEX_IN_READER_INDEX = 1;

    private Object[][] array;

    public Article( IDictionaryIndex reader, int inReaderIndex ) {
        this( 1 );
        set( 0, reader, inReaderIndex );
    }

    public Article( int groupsLength ) {
        array = new Object[groupsLength][2];
    }

    public void set( int index, IDictionaryIndex reader, int inReaderIndex ) {
        array[index][INDEX_READER] = reader;
        array[index][INDEX_IN_READER_INDEX] = inReaderIndex;
    }

    public String getTitle() {
        return getReader( 0 ).getTitle( getInReaderIndex( 0 ) );
    }

    public int size() {
        return array.length;
    }

    public String getBody( int index ) {
        return getReader( index ).getBody( getInReaderIndex( index ) );
    }

    public IInfo getReaderInfo( int index ) {
        return getReader( index ).getInfo();
    }

    public IDictionaryIndex getReader( int index ) {
        return (IDictionaryIndex) array[index][INDEX_READER];
    }

    public int getInReaderIndex( int index ) {
        return (Integer) array[index][INDEX_IN_READER_INDEX];
    }

    public Article append( IDictionaryIndex reader, int inReaderIndex ) {
        Object[][] newArray = new Object[array.length + 1][];

        System.arraycopy( array, 0, newArray, 0, array.length );

        newArray[array.length] = new Object[] { reader, inReaderIndex };

        array = newArray;

        return this;
    }

    public void append( Article article ) {
        // TODO optimize: add all at once
        for ( int i = 0; i < article.size(); i++ ) {
            append( article.getReader( i ), article.getInReaderIndex( i ) );
        }
    }

    public boolean equals( Object obj ) {
        if ( obj == this ) {
            return true;
        }

        if ( !(obj instanceof IArticle) ) {
            return false;
        }

        IArticle article = (IArticle) obj;
        return getTitle().equals( article.getTitle() );
    }

    public int hashCode() {
        return getTitle().hashCode();
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder( "Article: title=\"" + getTitle() + "\", {" );
        for ( int i = 0; i < size(); i++ ) {
            buffer.append( getBody( i ) );

            if ( i < size() - 1 ) {
                buffer.append( ", " );
            }
        }
        buffer.append( "}" );
        return buffer.toString();
    }
}
