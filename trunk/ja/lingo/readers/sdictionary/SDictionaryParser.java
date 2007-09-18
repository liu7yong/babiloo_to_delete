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

package ja.lingo.readers.sdictionary;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.io.checksum.ChecksumUtil;
import ja.centre.util.io.Files;
import ja.lingo.engine.beans.Info;
import ja.lingo.engine.dictionaryindex.Token;
import ja.lingo.engine.dictionaryindex.builder.IDictionaryIndexBuilder;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.reader.BaseParser;

import java.io.IOException;

class SDictionaryParser extends BaseParser {
    private String fileName;
    private SDictionaryReader reader;
    private IMonitor monitor;

    private SDictionaryDriver driver;

    public SDictionaryParser( String fileName, SDictionaryReader reader, IMonitor monitor ) {
        Arguments.assertNotNull( "fileName", fileName );
        Arguments.assertNotNull( "reader", reader );
        Arguments.assertNotNull( "monitor", monitor );

        this.fileName = fileName;
        this.reader = reader;
        this.monitor = monitor;
    }

    public void buildIndex( IDictionaryIndexBuilder builder ) throws IOException {
        try {
            driver = new SDictionaryDriver( fileName );

            // add info
            Info info = new Info();
            info.setTitle( driver.getTitle() );
            info.setDescription( driver.getCopyright() + "\nVersion: " + driver.getVersion() );
            info.setReader( reader );
            info.setDataFileName( fileName );
            info.setDataFileEncoding( "UTF-8" ); // TODO remove
            info.setDataFileChecksum( ChecksumUtil.calculateForLenMod( fileName ) );

            // traverse through full index and add the articles
            monitor.start( 0, driver.getCapacity() );

            int nextWord = driver.getFullIndexOffset();
            for ( int i = 0; i < driver.getCapacity(); i++ ) {
                nextWord = addNextArticle( nextWord, builder );

                if ( needsUpdate() ) {
                    monitor.update( i );
                }
            }
            monitor.finish();

            builder.setInfo( info );
        } finally {
            builder.close();
            Files.closeQuietly( driver );
        }
    }

    private int addNextArticle( int offset, IDictionaryIndexBuilder builder ) throws IOException {
        int nextWord = driver.readShort( offset );
        //System.out.println( "(" + offset + ") nextWord = " + nextWord );

        //int previousWord = readShort( offset + 2 );
        //System.out.println( "(" + offset + ") previousWord = " + previousWord );

        int articlePointer = driver.readInt( offset + 2 + 2 );
        //System.out.println( "(" + offset + ") articlePointer = " + articlePointer );

        int titleOffset = offset + 2 + 2 + 4;
        int titleLength = nextWord - 2 - 2 - 4;
        //String title = readUtf8( titleOffset, titleLength );


        int bodyOffset = driver.getArticlesOffset() + articlePointer + 4;
        int bodyLength = driver.readInt( bodyOffset - 4 );
        //Unit bodyUnit = readCompressedUnit( bodyOffset );

        //System.out.println( "(" + offset + ") title = \"" + title + "\", \"" + bodyUnit.getBody() + "\"");

        builder.addArticle(
                new Token( titleOffset, titleLength ),
                new Token( bodyOffset, bodyLength ) );

        return nextWord + offset;
    }
}
