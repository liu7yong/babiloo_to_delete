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

import ja.centre.util.io.ByteArray;
import ja.centre.util.io.Files;
import ja.centre.util.io.linereader.ILineReader;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.application.util.misc.Strings;
import ja.lingo.engine.beans.Info;
import ja.lingo.engine.dictionaryindex.Token;
import ja.lingo.engine.dictionaryindex.builder.IDictionaryIndexBuilder;
import ja.lingo.engine.reader.IParser;
import ja.lingo.engine.reader.util.TextualHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DslParser implements IParser {
    private static final Pattern NAME_PATTERN = Pattern.compile( "^#NAME\\s*\\\"(.*?)\\\"$" );
    private static final int NAME_GROUP = 1;

    private TextualHelper helper;

    static final String UTF_16_LE = "UTF-16LE";

    public DslParser( String fileName, String encoding, DslReader reader, IMonitor monitor ) {
        helper = new TextualHelper( fileName, encoding, reader, monitor );
    }

    public void buildIndex( IDictionaryIndexBuilder builder ) throws IOException {
        helper.buildIndex( builder, new TextualHelper.IBuilderAdapter() {
            public void buildIndexFromCustomFormat( IDictionaryIndexBuilder builder, ILineReader lineReader, Info info ) throws IOException {
                DslParser.this.buildIndexFromCustomFormat( builder, lineReader, info );
            }
        } );
    }
    private void buildIndexFromCustomFormat( IDictionaryIndexBuilder builder, ILineReader lineReader, Info info ) throws IOException {
        boolean readingMeta = true;

        TitlesBodyCollector collector = new TitlesBodyCollector( builder );

        ByteArray lineBytes;
        while ( true ) {
            int lineStart = lineReader.getNextLineStart();

            if ( (lineBytes = lineReader.readLine()) == null ) {
                break;
            }

            int lineLength = lineReader.getLastLineEnd() - lineStart;

            // still in meta?
            if ( readingMeta ) {
                String line = new String( lineBytes.getBytes(), 0, lineBytes.getLength(), UTF_16_LE );
                if ( !line.startsWith( "#" ) || Strings.isEmpty( line ) ) {
                    readingMeta = false;
                }
            }

            // in meta?
            if ( readingMeta ) {
                String line = new String( lineBytes.getBytes(), 0, lineBytes.getLength(), UTF_16_LE );

                // if meta is ended
                if ( line.startsWith( "#NAME" ) ) {
                    Matcher matcher = NAME_PATTERN.matcher( line );
                    if ( matcher.find() ) {
                        info.setTitle( matcher.group( NAME_GROUP ) );
                    } else {
                        info.setTitle( line );
                    }
                    info.setDataFileEncoding( UTF_16_LE ); // TODO
                }
            } else {
                // not in meta - data
                if ( lineBytes.getLength() == 0 ) {
                    // skip - blank line
                } else if ( lineBytes.startsWith( 9, 0 ) ) { // '\u0009' - is body continued?
                    collector.updateBodyEnd( lineReader.getLastLineEnd() );
                } else { // ok, it is a title
                    // check for comment: {{comment}}
                    if ( lineBytes.getLength() > 4
                            && lineBytes.startsWith( 0x7b, 0, 0x7b, 0 ) // {{
                            && lineBytes.endsWith( 0x7d, 0, 0x7d, 0 ) ) { // }}
                        // skip - that's a comment
                    } else {
                        // ok, it is a title, for sure
                        collector.tryFlush();
                        collector.addTitle( lineStart, lineLength );
                    }
                }
            }
        }
        collector.tryFlush();

        tryUpdateDescritionFromAnnFile( info );
    }

    private static class TitlesBodyCollector {
        private IDictionaryIndexBuilder builder;

        private Token nextTitleToken;
        private List<Token> nextTitleTokens = new ArrayList<Token>();

        private int nextBodyStart;
        private int nextBodyEnd;

        private boolean needsBody = true;

        public TitlesBodyCollector( IDictionaryIndexBuilder builder ) {
            this.builder = builder;
        }

        public void updateBodyEnd( int bodyEnd ) {
            nextBodyEnd = bodyEnd;
            needsBody = false;
        }

        public void addTitle( int lineStart, int lineLength ) {
            // optimization: uses reference for 1 token, uses list for other tokens
            Token titleToken = new Token( lineStart, lineLength );
            if ( nextTitleToken == null ) {
                nextTitleToken = titleToken;
            } else {
                nextTitleTokens.add( titleToken );
            }

            nextBodyStart = lineStart + lineLength;
            needsBody = true;
        }

        public void tryFlush() throws IOException {
            // skip if there is nothing to write or we're waiting for body
            if ( nextTitleToken == null || needsBody ) {
                return;
            }

            // optimization: doesn't use List if there is only 1 title to flush
            Token bodyToken = new Token( nextBodyStart, nextBodyEnd - nextBodyStart );

            // flush first
            builder.addArticle( nextTitleToken, bodyToken );
            nextTitleToken = null;

            // flush 2+ if they exist
            if ( !nextTitleTokens.isEmpty() ) {
                for ( Token titleToken : nextTitleTokens ) {
                    builder.addArticle( titleToken, bodyToken );
                }
                nextTitleTokens.clear();
            }
        }
    }

    private static void tryUpdateDescritionFromAnnFile( Info info ) {
        try {
            byte[] data = Files.readAsBytes( calculateAnnFileName( info.getDataFileName() ) );
            info.setDescription( new String( data, UTF_16_LE ) );
        } catch ( IOException e ) {
            info.setDescription( info.getTitle() );
        }
    }
    private static String calculateAnnFileName( String fileName ) {
        int lastDotIndex = fileName.lastIndexOf( "." );
        if ( lastDotIndex == -1 ) {
            return "";
        }
        return fileName.substring( 0, lastDotIndex ) + ".ann";
    }
}
