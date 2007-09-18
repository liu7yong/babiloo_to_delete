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

import ja.centre.util.assertions.Arguments;
import ja.lingo.engine.IArticleList;
import ja.lingo.engine.beans.IArticle;

import javax.swing.*;

public class ArticlesListModel extends AArticleListModel {
    private IArticleList articles;

    private ArticlesListModel( IArticleList articles, JList list ) {
        super( list );

        Arguments.assertNotNull( "articles", articles );

        this.articles = articles;
    }

    public IArticle getEntity( int index ) {
        return articles.get( index );
    }

    public int getSize() {
        return articles.size();
    }

    public static void install( JList list, IArticleList articles ) {
        list.setModel( new ArticlesListModel( articles, list ) );
    }
}
