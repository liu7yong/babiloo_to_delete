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
import ja.lingo.engine.util.comparators.CollatingStringComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class SearchIndexBuilder implements ISearchIndexBuilder {
    private static final Log LOG = LogFactory.getLog( SearchIndexBuilder.class );

    private final NodeSerializer serializer;
    private final SearchIndexBuilderSession session;

    private String lastAddedText;

    private CollatingStringComparator comparator = new CollatingStringComparator();

    public SearchIndexBuilder( String indexFileName ) throws IOException {
        serializer = new NodeSerializer( indexFileName );
        session = new SearchIndexBuilderSession();
    }

    public void add( String text, int value ) throws IOException {
        text = KeyConvertor.convert( text );


/*
        if ( lastAddedText != null && lastAddedText.charAt( 0 ) != text.charAt( 0 ) ) {
            flush();
        }
*/

        if ( lastAddedText != null
                && lastAddedText.charAt( 0 ) != text.charAt( 0 ) // booster
                && 0 != comparator.compareIgnoreAccents( lastAddedText.substring( 0, 1 ), text.substring( 0, 1 ) ) // precise comparator
                ) {
            flush();
        }

        lastAddedText = text;
        session.add( text, value );
    }

    private void flush() throws IOException {
        INode node = session.ejectOptimizedRootNode();
        if ( node.isLeaf() ) {
            LOG.info( "Nothing to flush. Skipping" );
        } else {
            LOG.info( "Flushing at \"" + lastAddedText + "\"" );

            //States.assertEquals( "Invalid number of child nodes", 1, node.childrenCount() );
            serializer.serialize( node.getChild( 0 ) );
        }
    }

    public void close() throws IOException {
        flush();
        serializer.close();
    }
}
