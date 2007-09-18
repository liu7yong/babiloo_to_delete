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

import ja.centre.util.sort.external.IPersister;
import ja.centre.util.sort.external.IReader;
import ja.centre.util.sort.external.IWriter;
import ja.centre.util.io.Files;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.dictionaryindex.reader.DataSource;
import ja.lingo.engine.dictionaryindex.reader.IDataSource;
import ja.lingo.engine.reader.IConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class TokenPairPersister implements IPersister<TokenPair> {
    private IConverter converter;
    private IDataSource dataSource;

    public TokenPairPersister( IInfo info ) throws IOException {
        this.converter = info.getReader().createConverter( info );
        this.dataSource = new DataSource( info.getDataFileName() );
    }

    public IWriter<TokenPair> createWriter( OutputStream os ) throws IOException {
        return new TokenPairWriter( os );
    }

    public IReader<TokenPair> createReader( InputStream is ) throws IOException {
        return new TokenPairReader( is, converter, dataSource );
    }


    public void close() throws IOException {
        Files.close( dataSource );
    }
}
