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
import ja.centre.util.assertions.States;
import ja.centre.util.io.ByteArray;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.dictionaryindex.Token;
import ja.lingo.engine.reader.IConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class DictionaryIndex implements IDictionaryIndex {
    private static final Log LOG = LogFactory.getLog( DictionaryIndex.class );

    private IInfo info;

    private IConverter converter;

    private ReaderFormat readerFormat;
    private ByteArray titleByteArray;
    private ByteArray bodyByteArray;

    // caching stuff
    private int lastTitleIndex = -1;
    private String lastTitle;

    public DictionaryIndex( IInfo info, String indexFileName ) throws IOException {
        this( info, new ReaderFormat( indexFileName, info.getDataFileName() ) );
        //Babiloo must load the TitlesIndex 
        //this( info, new ReaderFormat( indexFileName, info.getTitlesDataFileName() );
    }
    public DictionaryIndex( IInfo info, ReaderFormat readerFormat ) {
        Arguments.assertNotNull( "info", info );
        Arguments.assertNotNull( "readerFormat", readerFormat );

        this.info = info;
        this.readerFormat = readerFormat;

        this.converter = info.getReader().createConverter( info );

        this.titleByteArray = new ByteArray();
        this.bodyByteArray = new ByteArray();
    }

    public int size() {
        assertNotClosed();
        return readerFormat.size();
    }

    public IInfo getInfo() {
        assertNotClosed();
        return this.info;
    }

    public void close() throws IOException {
        assertNotClosed();
        LOG.info( "Closing \"" + readerFormat + "\"..." );
        readerFormat.close();
    }

    public String getTitle( int index ) {
        assertNotClosed();

        if ( lastTitleIndex != index ) {
            lastTitleIndex = index;

            readerFormat.readTitle( index, titleByteArray );
            lastTitle = converter.getTitle( titleByteArray.getBytes(),
                    0, titleByteArray.getLength() );
        }
        return lastTitle;
    }

    public String getBody( int index ) {
        assertNotClosed();

        readerFormat.readTitle( index, titleByteArray );
        readerFormat.readBody( index, bodyByteArray );

        return converter.getBody(
                titleByteArray.getBytes(), 0, titleByteArray.getLength(),
                bodyByteArray.getBytes(), 0, bodyByteArray.getLength() );
    }

    public Token getTitleToken( int index ) {
        assertNotClosed();
        return readerFormat.getTitleToken( index );
    }

    public Token getBodyToken( int index ) {
        assertNotClosed();
        return readerFormat.getBodyToken( index );
    }

    private void assertNotClosed() {
        if ( readerFormat.isClosed() ) {
            States.doThrow( "Index reader expected to be not closed (\"" + readerFormat + "\")" );
        }
    }
}
