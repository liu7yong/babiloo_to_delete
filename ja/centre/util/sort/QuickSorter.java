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

import ja.centre.util.assertions.Arguments;

import java.util.Comparator;

/**
 * NOTE: NOT THREAD SAFE
 */
public class QuickSorter<T> implements ISorter<T> {
    private Comparator<T> comparator;
    private ISortSubject<T> subject;

    //private int deep;

    public void sort( ISortSubject<T> subject, Comparator<T> comparator ) {
        Arguments.assertNotNull( "subject", subject );
        Arguments.assertNotNull( "comparator", comparator );

        this.comparator = comparator;
        this.subject = subject;

        recQuickSort( 0, subject.size() - 1 );
    }

    private void recQuickSort( int left, int right ) {
        //deep++;

        if ( right - left <= 0 ) {
            return;
        } else {
            T pivot = subject.get( right );

            //if ( deep > 2000 ) {
            //    System.out.println( "left = " + left + ", right = " + right + ", toString() = " +  pivot );
            //}

            int partition = partitionIt( left, right, pivot );
            recQuickSort( left, partition - 1 );
            recQuickSort( partition + 1, right );
        }
        //deep--;
    }

    private int partitionIt( int left, int right, T pivot ) {
        int leftPtr = left - 1;
        int rightPtr = right;
        while ( true ) {
            while ( comparator.compare( subject.get( ++leftPtr ), pivot ) < 0 );
            while ( rightPtr > 0 && comparator.compare( subject.get( --rightPtr ), pivot ) > 0 ) ;

            if ( leftPtr >= rightPtr )
                break;
            else
                subject.swap( leftPtr, rightPtr );
        }
        subject.swap( leftPtr, right );
        return leftPtr;
    }
}
