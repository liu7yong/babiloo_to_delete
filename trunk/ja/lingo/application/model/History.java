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

package ja.lingo.application.model;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class History {
    private static final Log LOG = LogFactory.getLog( History.class );

    private List<IHistoryListener> listeners;
    private List<String> titles;

    private int selected = -1;

    public History() {
        listeners = new ArrayList<IHistoryListener>();
        titles = new ArrayList<String>();
    }

    public void clear() {
        if ( titles.isEmpty() ) {
            return;
        }
        titles.clear();

        fireContentsChanged();
        fireSelectionChanged();
    }

    public void bookmark( String title ) {
        Arguments.assertNotNull( "title", title );
        // TODO introduce limit??

        if ( !titles.isEmpty() ) {
            if ( getSelectedTitle().equals( title ) ) {
                return;
            }

            if ( titles.get( 0 ).equals( title ) ) {
                setSelected( 0 );
                return;
            }
        }

        titles.add( 0, title );
        selected = 0;

        fireContentsChanged();
        fireSelectionChanged();
    }

    public int getSelectedIndex() {
        return selected;
    }

    public void setSelected( int index ) {
        if ( selected == index ) {
            return;
        }

        if ( titles.size() == 0 || index < 0 || index >= titles.size() ) {
            Arguments.doThrow( "Index expected to be in [0;"
                    + (titles.size() - 1) + "] range. Actual value is " + index );
        }
        selected = index;
        fireSelectionChanged();
    }

    public void setSelectedAndTranslate( int index ) {
        setSelected( index );

        fireSelectionChangedByUser();
    }

    public void back() {
        LOG.info( "Back..." );

        assertHasPrevious();
        setSelectedAndTranslate( getSelectedIndex() + 1 );
    }

    public void forward() {
        LOG.info( "Forward..." );

        assertHasNext();
        setSelectedAndTranslate( getSelectedIndex() - 1 );
    }

    public boolean hasPrevious() {
        return getSelectedIndex() < titles.size() - 1;
    }

    public boolean hasNext() {
        return getSelectedIndex() > 0 && !isEmpty();
    }

    public boolean isEmpty() {
        return titles.isEmpty();
    }

    public int size() {
        return titles.size();
    }

    public String getSelectedTitle() {
        return getTitle( getSelectedIndex() );
    }

    public String getTitle( int index ) {
        return titles.get( index );
    }

    public void addHistoryListener( IHistoryListener historyListener ) {
        Arguments.assertNotNull( "historyListener", historyListener );

        listeners.add( historyListener );
    }

    private void assertHasPrevious() {
        assertNotEmpty();

        States.assertTrue( hasPrevious(), "There is no previous entry." );
    }

    private void assertHasNext() {
        assertNotEmpty();

        States.assertTrue( hasNext(), "There is no previous entry." );
    }

    private void assertNotEmpty() {
        States.assertNonZero( titles.size(), "There are no entries." );
    }

    private void fireContentsChanged() {
        for ( IHistoryListener listener : copyListeners() ) {
            listener.contentsChanged();
        }
    }

    private void fireSelectionChanged() {
        for ( IHistoryListener listener : copyListeners() ) {
            listener.selectionChanged( getSelectedIndex() );
        }
    }

    private void fireSelectionChangedByUser() {
        for ( IHistoryListener listener : copyListeners() ) {
            listener.selectionChangedByUser( getSelectedIndex() );
        }
    }

    private ArrayList<IHistoryListener> copyListeners() {
        return new ArrayList<IHistoryListener>( listeners );
    }
}
