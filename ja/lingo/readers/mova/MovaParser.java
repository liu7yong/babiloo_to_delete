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

package ja.lingo.readers.mova;

import ja.centre.util.arrays.ArrayUtil;
import ja.centre.util.io.ByteArray;
import ja.centre.util.io.linereader.ILineReader;
import ja.lingo.engine.beans.Info;
import ja.lingo.engine.dictionaryindex.Token;
import ja.lingo.engine.dictionaryindex.builder.IDictionaryIndexBuilder;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.reader.IParser;
import ja.lingo.engine.reader.util.TextualHelper;

import java.io.IOException;

class MovaParser implements IParser {
    private boolean infoIsSent;
    private static final byte[] SEPARATOR_BYTES = { 32, 32 }; // two spaces

    private TextualHelper helper;

    /**
     * NOTE: for test pusrposes only
     */
    MovaParser() {
    }

    public MovaParser( String fileName, String encoding, MovaReader reader, IMonitor monitor ) {
        helper = new TextualHelper( fileName, encoding, reader, monitor );
    }

    public void buildIndex( IDictionaryIndexBuilder builder ) throws IOException {
        helper.buildIndex( builder, new TextualHelper.IBuilderAdapter() {
            public void buildIndexFromCustomFormat( IDictionaryIndexBuilder builder, ILineReader lineReader, Info info ) throws IOException {
                MovaParser.this.buildIndexFromCustomFormat( builder, lineReader, info );
            }
        });
    }

    private void buildIndexFromCustomFormat( IDictionaryIndexBuilder builder, ILineReader lineReader, Info info ) throws IOException {
        ByteArray lineBytes;
        while ( true ) {
            int lineStart = lineReader.getNextLineStart();

            // TODO optimize? parse#1
            if ( (lineBytes = lineReader.readLine()) == null ) {
                break;
            }

            // TODO optimize? parse#2
            String line = new String( lineBytes.getBytes(), 0, lineBytes.getLength(), helper.getEncoding() );

            // info? (only first line)
            if ( !infoIsSent ) {
                infoIsSent = true;

                String infoTitle;
                String infoDescription;

                int infoSeparatorIndex = line.indexOf( ";" );
                if ( infoSeparatorIndex >= 0 ) {
                    infoTitle = line.substring( 0, infoSeparatorIndex ).trim();
                    infoDescription = line.substring( infoSeparatorIndex + 1 ).trim();
                } else {
                    infoTitle = helper.getFileName();
                    infoDescription = line;
                }

                info.setTitle( infoTitle );
                info.setDescription( infoDescription );
                continue;
            } else if ( line.startsWith( " " ) ) {
                // skip other description lines
                continue;
            }

            if ( line.startsWith( "_" ) ) {
                // skip label articles
                continue;
            }

            appendArticle( line, lineBytes, lineStart, builder );
        }
    }

    void appendArticle( String line, ByteArray lineBytes, int lineStart, IDictionaryIndexBuilder builder ) throws IOException {
        int separatorIndex = ArrayUtil.indexOf( lineBytes.getBytes(), lineBytes.getLength(), SEPARATOR_BYTES, SEPARATOR_BYTES.length );
        if ( separatorIndex != -1 ) {
            addArticle( builder,
                    lineStart, separatorIndex,
                    lineStart + (separatorIndex + SEPARATOR_BYTES.length),
                    lineBytes.getLength() - (separatorIndex + SEPARATOR_BYTES.length) );
        } else {
            addArticle( builder,
                    lineStart,
                    lineBytes.getLength(),
                    lineBytes.getLength(),
                    0 );
        }
    }

    private void addArticle( IDictionaryIndexBuilder builder, int titleStart, int titleLength, int bodyStart, int bodyLength ) throws IOException {
        builder.addArticle(
                new Token( titleStart, titleLength ),
                new Token( bodyStart, bodyLength )
        );
    }
}
