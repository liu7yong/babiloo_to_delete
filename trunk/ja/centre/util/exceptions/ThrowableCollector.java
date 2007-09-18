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

package ja.centre.util.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ThrowableCollector {
    private List<String> whenMessages = new ArrayList<String>();
    private List<Throwable> throwables = new ArrayList<Throwable>();

    public void add( String whenMessage, Throwable throwable ) {
        whenMessages.add( whenMessage );
        throwables.add( throwable );
    }

    public boolean isNotEmpty() {
        return !throwables.isEmpty();
    }

    public String getMessage() {
        StringBuilder buffer = new StringBuilder();
        for ( int i = 0; i < throwables.size(); i++ ) {
            Throwable throwable = throwables.get( i );

            if ( i == 0 ) {
                buffer.append( throwables.size() ).append( " throwables collected:\n" );
            }

            buffer.append( "when ")
                    .append( whenMessages.get( i ) )
                    .append( ": " )
                    .append( throwable.getClass().getName() )
                    .append( ": " )
                    .append( throwable.getMessage() );

            if ( i != throwables.size() - 1 ) {
                buffer.append( "\n" );
            }
        }
        return buffer.toString();
    }
}
