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

import ja.lingo.engine.util.KeyConvertor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class BaseSearchIndex implements ISearchIndex {
    private static final Log LOG = LogFactory.getLog( BaseSearchIndex.class );

    protected INode rootNode;

    public int indexOfFirstLike( String text ) {
        assertNotClosed();

        LOG.info( "Looking for: \"" + text + "\"" );

        return indexOfFirstLike0( KeyConvertor.convert( text ) );
    }

    private int indexOfFirstLike0( String text ) {
        int currentChar = 0;
        int value = -1;
        INode node = rootNode;
        while ( currentChar < text.length() ) {
            node = node.findChild( text.charAt( currentChar ) );

            if ( node == null ) {
                return value;
            }
            currentChar++;
            value = node.getValue();
        }
        return currentChar == text.length() ? value : -1;
    }

    protected abstract void assertNotClosed();
}
