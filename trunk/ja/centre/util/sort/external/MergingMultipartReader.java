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

import java.io.IOException;
import java.util.*;

public class MergingMultipartReader<T> implements IReader<T> {
    private List<StepReader<T>> stepReaders;
    private Comparator<? super T> comparator;

    public MergingMultipartReader( Collection<IReader<T>> readers, Comparator<? super T> comparator ) throws IOException {
        Arguments.assertNotNull( "readers", readers );
        Arguments.assertNotNull( "comparator", comparator );

        // convert readers to step readers, delete empty readers (by calling their "close" method)
        stepReaders = new ArrayList<StepReader<T>>();
        for ( IReader<T> reader : readers ) {
            StepReader<T> stepReader = new StepReader<T>( reader );
            if ( stepReader.hasNext() ) {
                stepReader.readNext();

                stepReaders.add( stepReader );
            } else {
                stepReader.close();
            }
        }

        this.comparator = comparator;
    }

    public boolean hasNext() {
        return !stepReaders.isEmpty();
    }

    public T next() throws IOException {
        // find reader with the least value
        StepReader<T> readerWithLeast = null;
        for ( StepReader<T> stepReader : stepReaders ) {
            if ( readerWithLeast == null || comparator.compare( stepReader.getLast(), readerWithLeast.getLast() ) < 0 ) {
                readerWithLeast = stepReader;
            }
        }

        States.assertNotNull( readerWithLeast, "There is no next least value" );

        T returnValue = readerWithLeast.getLast();

        // move reader's point and remove+close/clean reader if necessary
        if ( readerWithLeast.hasNext() ) {
            readerWithLeast.readNext();
        } else {
            stepReaders.remove( readerWithLeast );
            readerWithLeast.close();
        }

        return returnValue;
    }

    public void close() throws IOException {
        for ( Iterator<StepReader<T>> i = stepReaders.iterator(); i.hasNext(); ) {
            StepReader<T> stepReader = i.next();

            stepReader.close();
            i.remove();
        }
    }
}
