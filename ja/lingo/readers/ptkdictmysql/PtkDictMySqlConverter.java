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

package ja.lingo.readers.ptkdictmysql;

import ja.lingo.application.util.misc.Strings;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.reader.util.BaseConverter;

class PtkDictMySqlConverter extends BaseConverter {

    private static final String[] BODY_PREFIXES = { ": ", ":<br>", " " };

    public PtkDictMySqlConverter( IInfo info ) {
        super( info );
    }

    protected String convertTitle( String title ) {
        return Strings.unescapeSql( title );
    }

    protected String convertBody( String title, String body ) {
        title = Strings.unescapeSql( title );
        body = Strings.unescapeSql( body );

        if ( body.contains( "\n" ) ) {
            body = body.replace( "\n", "<br>" );
        }

        // some dictionaries contain article title in the begining of article body
        // it is not neccesary and must be cut 
        for ( String prefix : BODY_PREFIXES ) {
            if ( body.startsWith( title + prefix ) ) {
                body = body.substring( title.length() + prefix.length() );
                break;
            }
        }

        return body;
    }
}
