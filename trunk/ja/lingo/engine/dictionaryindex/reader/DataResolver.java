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
import ja.centre.util.io.ByteArray;

class DataResolver {
    private ITokenReader tokenReader;
    private IDataSource dataSource;

    public DataResolver( ITokenReader reader, IDataSource dataSource ) {
        Arguments.assertNotNull( "reader", reader );
        Arguments.assertNotNull( "dataSource", dataSource );

        this.tokenReader = reader;
        this.dataSource = dataSource;
    }

    public void getData( int index, ByteArray byteArray ) {
        dataSource.getData(
                tokenReader.getTokenStart( index ),
                tokenReader.getTokenLength( index ), byteArray );
    }

    public int size() {
        return tokenReader.size();
    }
}
