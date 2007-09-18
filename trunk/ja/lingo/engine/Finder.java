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

import ja.centre.util.assertions.Arguments;
import ja.centre.gui.concurrent.StopWatch;
import ja.lingo.application.util.misc.Strings;
import ja.lingo.engine.beans.IArticle;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.monitor.ISearchMonitor;
import ja.lingo.engine.util.KeyConvertor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Finder implements IFinder {
    private Engine engine;

    public Finder( Engine engine ) {
        Arguments.assertNotNull( "engine", engine );

        this.engine = engine;
    }

    public boolean contains( String title ) {
        return indexOf( title ) != -1;
    }

    public IArticle get( int index ) {
        return engine.getMergedIndex().getArticle( index );
    }
    public IArticle find( String title ) {
        int index = indexOf( title );
        return index != -1 ? get( index ) : null;
    }
    public IArticle findFirstLike( String title ) {
        int index = indexOfFirstLike( title );
        return index == -1 ? null : get( index );
    }
    public IArticle findFirstStartsWith( String title ) {
        IArticle firstLike = findFirstLike( title );

        if ( firstLike != null && ! KeyConvertor.startsWith( firstLike.getTitle(), title ) ) {
            firstLike = null;
        }

        return firstLike;
    }

    public String getArticleTitle( int index ) {
        return engine.getMergedIndex().getArticleTitle( index );
    }

    public int indexOf( String title ) {
        int index = indexOfFirstLike( title );

        if ( index == -1 || engine.getTitleComparator().compare( title, getArticleTitle( index ) ) != 0 ) {
            return -1;
        }

        return index;
    }
    public int indexOfFirstLike( String title ) {
        int i = engine.getSearchIndex().indexOfFirstLike( title );
        if ( i < size() ) {
            String nextTitle = getArticleTitle( i + 1 );
            if ( title.equals( nextTitle ) ) {
                i++;
            }
        }
        return i;
    }

    public int size() {
        return engine.getMergedIndex().size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Map<Integer, List<String>> suggest( String title, IMonitor monitor ) {
        Map<Integer, List<String>> distanceToTitlesMap = new HashMap<Integer, List<String>>();
        int distanceMax;
        if ( title.length() <= 4 ) {
            distanceMax = 1;
        } else if ( title.length() <= 6 ) {
            distanceMax = 2;
        } else {
            distanceMax = 3;
        }

        for ( int i = 0; i <= distanceMax; i++ ) {
            distanceToTitlesMap.put( i, new ArrayList<String>() );
        }

        StopWatch stopWatch = new StopWatch();

        monitor.start( 0, size() );

        title = KeyConvertor.convert( title );

        for ( int i = 0; i < size(); i++ ) {
            if ( stopWatch.isTimedOut() ) {
                monitor.update( i );
            }

            String text =  getArticleTitle( i );
            String textConverted =  KeyConvertor.convert( text );
            if ( textConverted.startsWith( title ) ) {
                distanceToTitlesMap.get( 0 ).add( text );
            } else if ( Math.abs( text.length() - title.length() ) <= distanceMax ) {
                int distance = Strings.getLevenshteinDistance( title, textConverted );
                if ( distance >= 0 && distance <= distanceMax ) {
                    distanceToTitlesMap.get( distance ).add( text );
                }
            }
        }
        monitor.finish();
        return distanceToTitlesMap;
    }
    public void search( String text, IArticleListBuilder builder, boolean caseSensetive, ISearchMonitor monitor ) {
        monitor.start( 0, size() );

        if ( !caseSensetive ) {
            text = text.toLowerCase();
        }
        text = Strings.escapeHtml( text );

        StopWatch stopWatch = new StopWatch();

        int size = size();
        for ( int i = 0; i < size; i++ ) {
            if ( stopWatch.isTimedOut() ) {
                if ( monitor.isCanceled() ) {
                    return;
                }
                monitor.update( i );
            }

            IArticle article = get( i );
            for ( int j = 0; j < article.size(); j++ ) {
                String body = article.getBody( j );
                if ( !caseSensetive ) {
                    body = body.toLowerCase();
                }

                if ( body.contains( text ) ) {
                    builder.add( article );
                    break;
                }
            }
        }

        monitor.finish();
    }
}
