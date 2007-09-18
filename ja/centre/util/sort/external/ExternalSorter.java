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

package ja.centre.util.sort.external;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.sort.ISorter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Comparator;

public class ExternalSorter<T> implements IExternalSorter<T> {
    private static final Log LOG = LogFactory.getLog( ExternalSorter.class );
    private IPersister<T> persister;
    private ISorter<T> sorter;
    private Comparator<T> comparator;
    private int valuesInMemory;

    public ExternalSorter( IPersister<T> persister, ISorter<T> sorter, Comparator<T> comparator, int valuesInMemory) {
        Arguments.assertNotNull( "persister", persister );
        Arguments.assertNotNull( "sorter", sorter );
        Arguments.assertNotNull( "comparator", comparator );
        Arguments.assertPositiveNonZero( "valuesInMemory", valuesInMemory );

        this.persister = persister;
        this.sorter = sorter;
        this.comparator = comparator;
        this.valuesInMemory = valuesInMemory;
    }

    public void sort( IReader<T> reader, IWriter<T> writer ) throws IOException {
        Arguments.assertNotNull( "reader", reader );
        Arguments.assertNotNull( "writer", writer );

        try {
            doSort( reader, writer );
        } finally{
            reader.close();
            writer.close();
        }
    }

    private void doSort( IReader<T> reader, IWriter<T> writer ) throws IOException {
        IMultipartWriter<T> multipartWriter = createMultipartWriter( persister, sorter, comparator, valuesInMemory );

        LOG.info( "Splitting to sorted parts..." );
        while ( reader.hasNext() ) {
            multipartWriter.write( reader.next() );
        }
        multipartWriter.close();

        // TODO merge with 10 readers at once (make it parameter - mergeFilesAtOnce)
        LOG.info( "Copying from splitted parts to destination writer" );
        IReader<T> multipartReader = new MergingMultipartReader<T>(
                multipartWriter.getExternalReaders(), comparator );
        while ( multipartReader.hasNext() ) {
            writer.write( multipartReader.next() );
        }

        LOG.info( "Closing..." );
        multipartReader.close();
        multipartWriter.clean();
    }

    protected IMultipartWriter<T> createMultipartWriter( IPersister<T> persister, ISorter<T> sorter, Comparator<T> comparator, int valuesInMemory ) {
        return new FileMultipartWriter<T>( persister, sorter, comparator, valuesInMemory );
    }
}