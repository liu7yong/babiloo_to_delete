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

import ja.centre.util.assertions.Arguments;
import ja.centre.util.sort.external.IReader;
import ja.centre.util.sort.external.MergingMultipartReader;
import ja.lingo.engine.dictionaryindex.reader.IDictionaryIndex;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.monitor.NullMonitor;
import ja.lingo.engine.util.comparators.CollatingStringComparator;
import ja.lingo.engine.util.scorer.ProgressScorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergedIndexMerger {
    private List<IDictionaryIndex> indexes = new ArrayList<IDictionaryIndex>();

    private CollatingStringComparator csc = new CollatingStringComparator();
    private IMonitor monitor;

    public MergedIndexMerger() {
        this( new NullMonitor() );
    }

    public MergedIndexMerger( IMonitor monitor ) {
        Arguments.assertNotNull( "monitor", monitor );

        this.monitor = monitor;
    }

    public void addReaderContents( IDictionaryIndex reader ) {
        indexes.add( reader );
    }

    public void build( IMergedIndexBuilder builder ) throws IOException {
        builder.writeReaders( indexes );

        IReader<BuildArticle> multipartReader = new MergingMultipartReader<BuildArticle>( convertToReaders( indexes, csc ),
                new Comparator<BuildArticle>() {
                    public int compare( BuildArticle o1, BuildArticle o2 ) {
                        return o1.compareTo( o2 );
                    }
                } ); // TODO remove comparator?

        ProgressScorer scorer = createScorer( indexes, monitor );

        BuildArticle previousArticle = null;
        while ( multipartReader.hasNext() ) {
            scorer.increase();
            BuildArticle article = multipartReader.next();

            if ( previousArticle == null ) {
                // initialize
                previousArticle = article;
            } else if ( previousArticle.compareTo( article ) == 0 ) {
                // merge
                previousArticle.append( article );
            } else {
                //flush
                builder.write( previousArticle );
                previousArticle = article;
            }
        }

        // flush last
        if ( previousArticle != null ) {
            builder.write( previousArticle );
        }

        builder.close();
        monitor.finish();
    }

    private ProgressScorer createScorer( List<IDictionaryIndex> indexes, IMonitor monitor ) {
        int articlesCount = 0;
        for ( IDictionaryIndex index : indexes ) {
            articlesCount += index.size();
        }
        return new ProgressScorer( monitor, articlesCount );
    }

    private static List<IReader<BuildArticle>> convertToReaders( List<IDictionaryIndex> readers, CollatingStringComparator csc ) {
        List<IReader<BuildArticle>> readerAdapters = new ArrayList<IReader<BuildArticle>>();
        for ( IDictionaryIndex reader : readers ) {
            readerAdapters.add( new MergedIndexReaderAdapter( reader, csc ) );
        }
        return readerAdapters;
    }
}
