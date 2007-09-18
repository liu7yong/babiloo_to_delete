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

package ja.centre.gui.model;

import ja.centre.util.assertions.Arguments;

import java.util.ArrayList;
import java.util.List;

public class StaticListModel<T> extends AListModel<T> {
    private List<T> entities;

    public StaticListModel( ILabelBuilder<T> labelBuilder ) {
        this( labelBuilder, new ArrayList<T>() );
    }
    public StaticListModel( ILabelBuilder<T> labelBuilder, List<T> entities ) {
        super( labelBuilder );

        Arguments.assertNotNull( "entities", entities );

        this.entities = entities;
    }

    public void setEntities( List<T> entities ) {
        Arguments.assertNotNull( "entities", entities );

        if ( this.entities.size() > 0 ) {
            int oldSize = this.entities.size() - 1;
            this.entities.clear();
            fireIntervalRemoved( this, 0, oldSize );
        }

        this.entities.addAll( entities );

        if ( this.entities.size() > 0 ) {
            fireIntervalAdded( this, 0, this.entities.size() - 1 );
        }
    }

    public final T getEntity( int index ) {
        return entities.get( index );
    }

    public final int indexOf( T t ) {
        return entities.indexOf( t );
    }

    public final int getSize() {
        return entities.size();
    }

    public List<T> getEntities() {
        return entities;
    }
}
