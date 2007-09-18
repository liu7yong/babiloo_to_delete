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
import ja.lingo.engine.dictionaryindex.builder.IDictionaryIndexBuilder;

import java.io.IOException;

public class ScoringDictionaryIndexBuilder implements IDictionaryIndexBuilder {
    private IDictionaryIndexBuilder builder;
    private ProgressScorer scorer;

    public ScoringDictionaryIndexBuilder( IDictionaryIndexBuilder builder, ProgressScorer scorer ) {
        Arguments.assertNotNull( "builder", builder );
        Arguments.assertNotNull( "scorer", scorer );

        this.builder = builder;
        this.scorer = scorer;
    }

    public void addArticle( Token title, Token body ) throws IOException {
        scorer.increase();
        builder.addArticle( title, body );
    }

    public void setInfo( IInfo info ) throws IOException {
        builder.setInfo( info );
    }

    public void close() throws IOException {
        builder.close();
    }

    public IInfo getInfo() {
        return builder.getInfo();
    }
}
