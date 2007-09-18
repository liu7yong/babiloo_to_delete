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

package ja.centre.util.sort;

import java.util.Comparator;

public class MergeSorter<T> implements ISorter<T> {
    private static final int INSERTIONSORT_THRESHOLD = 7;
    private Comparator<T> comparator;

    public void sort( ISortSubject<T> subject, Comparator<T> comparator ) {
        this.comparator = comparator;

        ListSortSubject<T> subjectCopy = new ListSortSubject<T>();
        for ( int i = 0; i < subject.size(); i++ ) {
            subjectCopy.add( subject.get( i ) );
        }

        mergeSort( subjectCopy, subject, 0, subject.size(), 0 );
    }

    private void mergeSort( ISortSubject<T> src, ISortSubject<T> dest, int low, int high, int off ) {
        int length = high - low;

        // Insertion sort on smallest arrays
        if ( length < INSERTIONSORT_THRESHOLD ) {
            for ( int i = low; i < high; i++ ) {
                for ( int j = i; j > low &&
                        comparator.compare( dest.get( j - 1 ), dest.get( j ) ) > 0; j-- ) {
                    dest.swap( j, j - 1 );
                }
            }
            return;
        }

        // Recursively sort halves of dest into src
        int destLow = low;
        int destHigh = high;
        low += off;
        high += off;
        int mid = (low + high) >> 1;
        mergeSort( dest, src, low, mid, -off );
        mergeSort( dest, src, mid, high, -off );

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if ( comparator.compare( src.get( mid - 1 ), src.get( mid ) ) <= 0 ) {
            for ( int i = 0; i < length; i++ ) {
                dest.set( destLow + i, src.get( low + i) );
            }
            //System.arraycopy( src, low, dest, destLow, length );
            return;
        }

        // Merge sorted halves (now in src) into dest
        for ( int i = destLow, p = low, q = mid; i < destHigh; i++ ) {
            if ( q >= high || p < mid && comparator.compare( src.get( p ), src.get( q ) ) <= 0 ) {
                dest.set( i, src.get( p++ ) );
            } else {
                dest.set( i, src.get( q++ ) );
            }
        }
    }
}
