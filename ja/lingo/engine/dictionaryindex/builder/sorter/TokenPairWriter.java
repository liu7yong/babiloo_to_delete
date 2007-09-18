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

package ja.lingo.engine.dictionaryindex.builder.sorter;

import ja.centre.util.sort.external.IWriter;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class TokenPairWriter implements IWriter<TokenPair> {
    private DataOutputStream dos;

    public TokenPairWriter( OutputStream os ) {
        this.dos = new DataOutputStream( new BufferedOutputStream( os ) );
    }

    public void write( TokenPair value ) throws IOException {
        // TODO optimize? sort(260)
        dos.writeInt( value.getTitleToken().getStart() );
        dos.writeInt( value.getTitleToken().getLength() );
        dos.writeInt( value.getBodyToken().getStart() );
        dos.writeInt( value.getBodyToken().getLength() );
    }

    public void close() throws IOException {
        dos.close();
        dos = null;
    }
}
