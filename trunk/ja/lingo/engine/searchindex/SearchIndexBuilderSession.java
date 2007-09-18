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
import ja.lingo.engine.util.KeyConvertor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class SearchIndexBuilderSession {
    private static final Log LOG = LogFactory.getLog( SearchIndexBuilderSession.class );

    private NodeOptimizer optimizer;

    private IMutableNode rootNode;
    private int size;

    private boolean optimized;

    public SearchIndexBuilderSession() {
        optimizer = new NodeOptimizer();

        reset();
    }

    public void add( String text, int value ) {
        States.assertFalse( optimized, "Expected to be not optimized" );

        Arguments.assertNotEmpty( "text", text );
        size = -1;

        _add( rootNode, KeyConvertor.convert( text ), value, 0 );
    }

    public void optimize() {
        int notOptimizedSize = size();

        optimizer.optimize( rootNode );
        size = -1;

        LOG.info( "Search index node before/after optimization: "
                + notOptimizedSize + "/" + size() );

        optimized = true;
    }

    public int size() {
        if ( size == -1 ) {
            size = 0;
            _size( rootNode );
        }
        return size;
    }

    public INode ejectOptimizedRootNode() {
        optimize();
        INode rootNode = this.rootNode;
        reset();
        return rootNode;
    }

    private void reset() {
        rootNode = Node.createRoot();
        size = -1;
        optimized = false;
    }

    private void _add( IMutableNode node, String title, int value, int startChar ) {
        IMutableNode childNode = (Node) node.findChild( title.charAt( startChar ) );

        if ( childNode == null ) {
            childNode = Node.create( title.charAt( startChar ), value );
            node.addChild( childNode );
        }

        if ( startChar + 1 < title.length() ) {
            _add( childNode, title, value, startChar + 1 );
        }
    }

    private void _size( INode node ) {
        size++;
        if ( !node.isLeaf() ) {
            for ( int i = 0; i < node.childrenCount(); i++ ) {
                INode childNode = node.getChild( i );

                _size( childNode );
            }
        }
    }
}
