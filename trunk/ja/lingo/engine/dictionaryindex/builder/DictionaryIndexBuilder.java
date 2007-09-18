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
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.beans.Info;
import ja.lingo.engine.dictionaryindex.Token;

import java.io.IOException;

public class DictionaryIndexBuilder implements IDictionaryIndexBuilder {
    private BuilderFormat builderFormat;

    private Info info;
    private String indexFileName;

    public DictionaryIndexBuilder( String indexFileName ) throws IOException {
        Arguments.assertNotNull( "indexFileName", indexFileName );

        // TODO check files existence
        this.indexFileName  = indexFileName;

        builderFormat = new BuilderFormat( indexFileName );
    }

    public void addArticle( Token title, Token body ) throws IOException {
        if ( title.getLength() == 0 ) {
            Arguments.doThrow( "The passed article has zero lenth title" );
        }
        builderFormat.getArticleTitleTokenBuilder().put( title );
        builderFormat.getArticleBodyTokenBuilder().put( body );
    }

    public void setInfo( IInfo info ) throws IOException {
        // validate
        assertNotNullAndNotEmpty( "title",              info.getTitle() );
        assertNotNullAndNotEmpty( "description",        info.getDescription() );
        assertNotNullAndNotEmpty( "readerName",         info.getReader().getName() );         // TODO check existance???
        assertNotNullAndNotEmpty( "dataFileName",       info.getDataFileName() );       // TODO check existance???
        assertNotNullAndNotEmpty( "dataFileEncoding",   info.getDataFileEncoding() );
        assertNotZero(            "dataFileChecksum",   info.getDataFileChecksum() );

        this.info = new Info( info );
    }

    private void assertNotZero( String field, long value ) throws IOException {
        if ( value == 0 ) {
            throw new IOException( "Reader returned dictionary info with zero in field \"" + field + "\"" );
        }
    }

    private void assertNotNullAndNotEmpty( String field, String text ) throws IOException {
        if ( text == null ) {
            throw new IOException( "Reader returned dictionary info with null in field \"" + field + "\"" );
        }

        if ( text.length() == 0 ) {
            throw new IOException( "Reader returned dictionary info with empty in field \"" + field + "\"" );
        }
    }

    public IInfo getInfo() {
        assertClosed();

        return info;
    }

    private void assertClosed() {
        States.assertTrue( builderFormat.isClosed(), "Index builder expected to be closed" );
    }

    public void close() throws IOException {
        if ( info != null ) {
            info.setIndexFileName( indexFileName );
            info.setCapacity( builderFormat.getArticleTitleTokenBuilder().getTokensWrittenCount() );
        }

        builderFormat.close();
    }
}
