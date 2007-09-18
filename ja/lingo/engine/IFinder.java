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

package ja.lingo.engine;

import ja.lingo.engine.beans.IArticle;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.monitor.ISearchMonitor;

import java.util.List;
import java.util.Map;

public interface IFinder extends IArticleList {
    boolean contains( String title );

    IArticle get( int index );
    IArticle find( String title );
    IArticle findFirstLike( String title );
    IArticle findFirstStartsWith( String title );

    String getArticleTitle( int index );

    int indexOf( String title );
    int indexOfFirstLike( String title );

    Map<Integer, List<String>> suggest( String title, IMonitor monitor );
    void search( String text, IArticleListBuilder builder, boolean caseSensetive, ISearchMonitor monitor );
}
