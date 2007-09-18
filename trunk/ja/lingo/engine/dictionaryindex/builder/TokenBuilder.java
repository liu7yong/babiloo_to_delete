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

package ja.lingo.engine.dictionaryindex.builder;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.centre.util.io.intio.IIntWriter;
import ja.lingo.engine.dictionaryindex.Token;

import java.io.IOException;

class TokenBuilder {
    private IIntWriter intWriter;

    private int tokensWritten;

    public TokenBuilder( IIntWriter intWriter ) {
        Arguments.assertNotNull( "intWriter", intWriter );

        this.intWriter = intWriter;
    }

    public void put( Token token ) throws IOException {
        intWriter.writeInt( token.getStart() );
        intWriter.writeInt( token.getLength() );

        tokensWritten++;
    }

    public int getTokensWrittenCount() {
        return tokensWritten;
    }

    public void close() throws IOException {
        States.assertNotNull( intWriter, "Already closed." );

        intWriter.close();
        intWriter = null;
    }
}
