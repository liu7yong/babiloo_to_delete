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

package ja.lingo.readers.dsl;

import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.reader.BaseDictionaryReader;
import ja.lingo.engine.reader.IConverter;
import ja.lingo.engine.reader.IParser;

public class DslReader extends BaseDictionaryReader {
    public DslReader() {
        super( false );
        supportedEncodings.add( DslParser.UTF_16_LE );
        name = "dsl";
        title = "DSL";

        extensions = new String[] { ".dsl" };
    }

    public IParser createParser( IInfo info, IMonitor monitor ) {
        return new DslParser( info.getDataFileName(), info.getDataFileEncoding(), this, monitor );
    }

    public IConverter createConverter( IInfo info ) {
        return new DslConverter( info );
    }

}
