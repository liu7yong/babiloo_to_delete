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

package ja.lingo.engine.dictionaryindex.reader;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.io.intio.IIntReader;
import ja.lingo.engine.dictionaryindex.Token;

class TokenReader implements ITokenReader {
    private IIntReader intReader;

    public TokenReader( IIntReader intReader ) {
        Arguments.assertNotNull( "intReader", intReader );

        this.intReader = intReader;
    }

    public Token getToken( int index ) {
        return new Token( getTokenStart( index ), getTokenLength( index ) );
    }

    public int getTokenStart( int index ) {
        return intReader.get( calculateStart( index ) );
    }
    // TODO optimize? merge(881) - english3 VS getTokenStart(...) - with (20) why???
    public int getTokenLength( int index ) {
        return intReader.get( calculateEnd( index ) );
    }

    private int calculateStart( int index ) {
        return index * 2;
    }
    private int calculateEnd( int index ) {
        return index * 2 + 1;
    }

    public int size() {
        return intReader.size() / 2;
    }
}
