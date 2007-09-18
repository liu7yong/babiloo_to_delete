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

package ja.lingo.engine.util.scorer;

import ja.centre.util.assertions.Arguments;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.dictionaryindex.Token;
import ja.lingo.engine.dictionaryindex.reader.IDictionaryIndex;

import java.io.IOException;

public class ScoringDictionaryIndex implements IDictionaryIndex {
    private IDictionaryIndex index;
    private ProgressScorer scorer;

    public ScoringDictionaryIndex( IDictionaryIndex index, ProgressScorer scorer ) {
        Arguments.assertNotNull( "index", index );
        Arguments.assertNotNull( "scorer", scorer );

        this.index = index;
        this.scorer = scorer;
    }

    public int size() {
        return index.size();
    }

    public void close() throws IOException {
        index.close();
    }

    public IInfo getInfo() {
        return index.getInfo();
    }

    public String getTitle( int index ) {
        return this.index.getTitle( index );
    }
    public String getBody( int index ) {
        return this.index.getBody( index );
    }

    public Token getTitleToken( int index ) {
        scorer.increase();
        return this.index.getTitleToken( index );
    }
    public Token getBodyToken( int index ) {
        return this.index.getBodyToken( index );
    }
}
