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
import ja.centre.util.assertions.States;
import ja.centre.util.sort.ISortSubject;
import ja.centre.util.sort.ISorter;
import ja.centre.util.sort.ListSortSubject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

abstract class AMultipartWriter<T> implements IMultipartWriter<T> {
    private static final Log LOG = LogFactory.getLog( AMultipartWriter.class );

    private IPersister<T> persister;
    private final int valuesInMemory;
    private ISorter<T> sorter;
    private Comparator<T> comparator;

    private List<IReader<T>> readers;
    private boolean isClosed;

    private ListSortSubject<T> currentSubject;

    protected AMultipartWriter( IPersister<T> persister, ISorter<T> sorter, Comparator<T> comparator, int valuesInMemory ) {
        Arguments.assertNotNull( "persister", persister );
        Arguments.assertNotNull( "sorter", sorter );
        Arguments.assertNotNull( "comparator", comparator );
        Arguments.assertPositiveNonZero( "valuesInMemory", valuesInMemory );

        this.persister = persister;
        this.valuesInMemory = valuesInMemory;
        this.sorter = sorter;
        this.comparator = comparator;

        this.readers = new ArrayList<IReader<T>>();
        this.currentSubject = new ListSortSubject<T>();
    }

    public void write( T value ) throws IOException {
        assertNotClosed();

        if ( currentSubject.size() == valuesInMemory ) {
            sortAndFlushCurrentSubject( false );
        }

        currentSubject.add( value );
    }

    public void close() throws IOException {
        assertNotClosed();

        sortAndFlushCurrentSubject( getCurrentPartIndex() == 0 );

        isClosed = true;
    }

    public void clean() throws IOException {
        for ( int i = 0; i < readers.size(); i++ ) {
            cleanDataForPart( i );
        }
    }

    private void sortAndFlushCurrentSubject( boolean storeInMemory ) throws IOException {
        LOG.info( "Sorting and flushing part " + getCurrentPartIndex() + "..." );

        // skip if empty
        if ( currentSubject.size() == 0 ) {
            LOG.info( "Skipping due to empty data" );
            return;
        }

        // sort
        sorter.sort( currentSubject, comparator );

        IReader<T> reader = storeInMemory
                ? createInMemoryReader()
                : createExternalReader();
        readers.add( reader );

        // reset state
        currentSubject = new ListSortSubject<T>();
    }

    private SortSubjectReader<T> createInMemoryReader() {
        LOG.info( "Creating in-memory reader.." );
        return new SortSubjectReader<T>( currentSubject );
    }

    private IReader<T> createExternalReader() throws IOException {
        LOG.info( "Creating external reader..." );

        // flush
        IWriter<T> writer = persister.createWriter( createOutputStreamForPart( getCurrentPartIndex() ) );
        for ( T t : currentSubject.asList() ) {
            writer.write( t );
        }
        writer.close();

        // create and add reader
        return persister.createReader( createInputStreamForPart( getCurrentPartIndex() ) );
    }

    private int getCurrentPartIndex() {
        return readers.size();
    }

    protected abstract OutputStream createOutputStreamForPart( int partIndex ) throws IOException;

    protected abstract InputStream createInputStreamForPart( int partIndex ) throws IOException;

    protected abstract void cleanDataForPart( int partIndex ) throws IOException;

    public List<IReader<T>> getExternalReaders() {
        assertClosed();

        return readers;
    }

    private void assertClosed() {
        if ( !isClosed ) {
            States.doThrow( "Expected to be closed" );
        }
    }

    private void assertNotClosed() {
        if ( isClosed ) {
            States.doThrow( "Expected to be not closed" );
        }
    }

    private static class SortSubjectReader<T> implements IReader<T> {
        private ISortSubject<T> subject;
        private int index;

        public SortSubjectReader( ISortSubject<T> sortSubject ) {
            Arguments.assertNotNull( "subject", sortSubject );

            this.subject = sortSubject;
        }

        public boolean hasNext() throws IOException {
            return index < subject.size();
        }

        public T next() throws IOException {
            if ( !hasNext() ) {
                States.doThrow( "There is no next value" );
            }

            return subject.get( index++ );
        }

        public void close() throws IOException {
            // do nothing
        }
    }
}
