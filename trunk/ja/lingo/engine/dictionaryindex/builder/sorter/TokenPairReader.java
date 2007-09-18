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

import ja.centre.util.assertions.States;
import ja.centre.util.io.ByteArray;
import ja.centre.util.sort.external.IReader;
import ja.lingo.engine.dictionaryindex.Token;
import ja.lingo.engine.dictionaryindex.reader.IDataSource;
import ja.lingo.engine.reader.IConverter;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

class TokenPairReader implements IReader<TokenPair> {
    private DataInputStream dis;
    private IConverter converter;
    private IDataSource dataSource;
    private ByteArray buffer;

    private int available;

    public TokenPairReader( InputStream is, IConverter converter, IDataSource dataSource ) throws IOException {
        this.dataSource = dataSource;
        this.converter = converter;
        this.dis = new DataInputStream( new BufferedInputStream( is ) );
        this.buffer = new ByteArray();

        this.available = dis.available(); // TODO is it ok to do so for FileInputStream???
    }

    public boolean hasNext() throws IOException {
        return available > 0;
    }

    public TokenPair next() throws IOException {
        States.assertTrue( hasNext(), "There is no next TokenPair" );

        // TODO optimize? sort(410)
        Token titleToken = new Token( dis.readInt(), dis.readInt() );
        Token bodyToken = new Token( dis.readInt(), dis.readInt() );
        available -= 4 * 4; // 4 ints // TODO not good, make separate class with 'available' caching

        dataSource.getData( titleToken.getStart(), titleToken.getLength(), buffer );

        return new TokenPair(
                converter.getTitle( buffer.getBytes(), 0, buffer.getLength() ),
                titleToken,
                bodyToken );
    }

    public void close() throws IOException {
        dis.close();
        dis = null;
    }
}