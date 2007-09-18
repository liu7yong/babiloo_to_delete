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

package ja.lingo.engine.dictionaryindex.builder.sorter;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.sort.ISorter;
import ja.centre.util.sort.MergeSorter;
import ja.centre.util.sort.external.ExternalSorter;
import ja.centre.util.sort.external.FileMultipartWriter;
import ja.centre.util.sort.external.IMultipartWriter;
import ja.centre.util.sort.external.IPersister;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.dictionaryindex.builder.IDictionaryIndexBuilder;
import ja.lingo.engine.dictionaryindex.reader.IDictionaryIndex;
import ja.lingo.engine.util.EngineFiles;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

public class DictionaryIndexSorter {
    private DictionaryIndexSorter() {
    }

    public static void sort( IDictionaryIndex reader, IDictionaryIndexBuilder builder, IInfo info, Comparator<String> titleComparator ) throws IOException {
        TokenPairPersister persister = new TokenPairPersister( info );
        try {
            TempExternalSorter<TokenPair> externalSorter = new TempExternalSorter<TokenPair>(
                    persister, new MergeSorter<TokenPair>(), new TokenPairComparator( titleComparator ), 100000 );
            externalSorter.sort( new DictionaryIndexReaderAdapter( reader ), new DictionaryIndexBuilderWriterAdapter( builder, info ) );
        } finally {
            persister.close();
        }
    }

    private static class TempExternalSorter<T> extends ExternalSorter<T> {
        public TempExternalSorter( IPersister<T> persister, ISorter<T> sorter, Comparator<T> comparator, int valuesInMemory ) {
            super( persister, sorter, comparator, valuesInMemory );
        }

        protected IMultipartWriter<T> createMultipartWriter( IPersister<T> persister, ISorter<T> sorter, Comparator<T> comparator, int valuesInMemory ) {
            return new FileMultipartWriter<T>( persister, sorter, comparator, valuesInMemory ) {
                protected File createFileForPart( int partIndex ) throws IOException {
                    return EngineFiles.createTempFile( "dictionaryIndexSorter.part." + partIndex );
                }
            };
        }
    }

    private static class TokenPairComparator implements Comparator<TokenPair> {
        private Comparator<String> titleComparator;

        public TokenPairComparator( Comparator<String> titleComparator ) {
            Arguments.assertNotNull( "titleComparator", titleComparator );
            this.titleComparator = titleComparator;
        }

        public int compare( TokenPair o1, TokenPair o2 ) {
            return titleComparator.compare( o1.getTitle(), o2.getTitle() );
        }
    }
}
