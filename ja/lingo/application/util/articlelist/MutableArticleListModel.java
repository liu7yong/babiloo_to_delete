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

package ja.lingo.application.util.articlelist;

import ja.lingo.engine.beans.IArticle;
import ja.lingo.engine.IArticleListBuilder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MutableArticleListModel extends AArticleListModel implements IArticleListBuilder {
    private final List<IArticle> articles = new ArrayList<IArticle>();

    public MutableArticleListModel( JList list ) {
        super( list );
        list.setModel( this );
    }

    // from swing list model
    public IArticle getEntity( int index ) {
        synchronized ( articles ) {
            return articles.get( index );
        }
    }
    public int getSize() {
        return size();
    }

    // from article builder
    public void add( IArticle article ) {
        final int addIndex;
        synchronized ( articles ) {
            addIndex = articles.size();
            articles.add( article );
        }

        if ( SwingUtilities.isEventDispatchThread() ) {
            fireIntervalAdded( this, addIndex, addIndex );
        } else {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    fireIntervalAdded( this, addIndex, addIndex );
                }
            } );
        }
    }

    public int size() {
        synchronized ( articles ) {
            return articles.size();
        }
    }
}
