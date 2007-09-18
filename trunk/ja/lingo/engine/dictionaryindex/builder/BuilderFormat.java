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
import ja.centre.util.io.intio.FileIntWriter;
import ja.lingo.engine.util.slice.SliceOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileOutputStream;
import java.io.IOException;

class BuilderFormat  {
    private static final Log LOG = LogFactory.getLog( BuilderFormat.class );

    private TokenBuilder articleTitleTokenBuilder;
    private TokenBuilder articleBodyTokenBuilder;

    private FileOutputStream indexFos;

    private boolean closed;

    public BuilderFormat( String indexFileName ) throws IOException {
        Arguments.assertNotNull( "indexFileName", indexFileName );

        this.indexFos = new FileOutputStream( indexFileName );

        // TODO close first if second failed
        articleTitleTokenBuilder = new TokenBuilder( new FileIntWriter( new SliceOutputStream( "titles", indexFos ) ) );
        articleBodyTokenBuilder = new TokenBuilder( new FileIntWriter( new SliceOutputStream( "bodies", indexFos ) ) );
    }

    public TokenBuilder getArticleTitleTokenBuilder() {
        return articleTitleTokenBuilder;
    }

    public TokenBuilder getArticleBodyTokenBuilder() {
        return articleBodyTokenBuilder;
    }

    public void close() throws IOException {
        States.assertFalse( closed, "Expected to be not closed" );

        /*
        // magic
        fos.write( "JFDF".getBytes( "ISO8859-1" ) );

        // version
        fos.write( 0 );
        fos.write( 0 );
        fos.write( 0 );
        fos.write( 0 );
        */

        // NOTE: the order is significant. See SliceOutputStream.close()
        articleTitleTokenBuilder.close();
        articleBodyTokenBuilder.close();

        indexFos.close();

        closed = true;

        LOG.info( "Builder format closed" );
    }

    public boolean isClosed() {
        return closed;
    }
}
