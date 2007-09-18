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

package ja.lingo.readers.babiloo;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.io.checksum.ChecksumUtil;
import ja.lingo.engine.beans.Info;
import ja.lingo.engine.dictionaryindex.Token;
import ja.lingo.engine.dictionaryindex.builder.IDictionaryIndexBuilder;
import ja.lingo.engine.observer.IEngineObserver;
import ja.lingo.engine.reader.IDictionaryReader;
import ja.lingo.engine.reader.IParser;

import java.io.IOException;

class BabilooParser implements IParser {
    private String fileName;
    private IDictionaryReader reader;
    private IEngineObserver observer;

    private BabilooDriver driver;

    public BabilooParser( String fileName, IDictionaryReader reader, IEngineObserver observer ) {
        Arguments.assertNotNull( fileName, "fileName" );
        Arguments.assertNotNull( reader, "reader" );
        Arguments.assertNotNull( observer, "observer" );

        this.fileName = fileName;
        this.reader = reader;
        this.observer = observer;
    }

    public void buildIndex( IDictionaryIndexBuilder builder ) throws IOException {
        try {
            driver = new BabilooDriver( fileName );

            // add info
            Info info = new Info();
            info.setTitle( driver.getTitle() );
            info.setDescription( driver.getCopyright() + "\nVersion: " + driver.getVersion() );
            info.setReader( reader );
            info.setDataFileName( fileName );
            info.setArticlesDataFileName(driver.getDataFileName());
            info.setTitlesDataFileName( driver.getTitlesDataFileName() );
            info.setDataFileEncoding( "UTF-8" ); // TODO remove
            info.setDataFileChecksum( ChecksumUtil.calculateForLengthModification( fileName ) );

            // traverse through full index and add the articles
            observer.start( 0, driver.getCapacity() );
            Key k;
            int nextWord = driver.getFullIndexOffset();
            for ( int i = 0; i < driver.getCapacity(); i++ ) {
                k = (Key) driver.getIndex().getOffsets(i);
                long off = k.getOffset();
                long len = k.getLength();
                if ((int) k.getWordLength() == 0){
                    continue;
                }
                builder.addArticle(
                new Token( (int) k.getWordOffset(), (int) k.getWordLength() ),
                new Token( (int) k.getOffset(), (int) k.getLength() ) );

                if ( observer.needsUpdate() ) {
                    observer.update( i );
                }
            }
            observer.finish();

            builder.setInfo( info );
            builder.close();
        } finally {
            if ( driver != null ) {
                driver.close();
                driver = null;
            }
        }
    }


}
