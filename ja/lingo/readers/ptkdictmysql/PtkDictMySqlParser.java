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

package ja.lingo.readers.ptkdictmysql;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.centre.util.io.ByteArray;
import ja.centre.util.io.linereader.ILineReader;
import ja.lingo.application.util.misc.Strings;
import ja.lingo.engine.beans.Info;
import ja.lingo.engine.dictionaryindex.Token;
import ja.lingo.engine.dictionaryindex.builder.IDictionaryIndexBuilder;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.reader.IParser;
import ja.lingo.engine.reader.util.TextualHelper;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PtkDictMySqlParser implements IParser {
    private static final String INSERT_INTO_PREFIX = "INSERT INTO ";

    private static final Pattern INSERT_INTO_PATTERN = Pattern.compile( "^INSERT INTO [a-zA-Z0-9_]+ VALUES \\(\\s*\\d+\\s*,\\s*'(.+?)'\\s*,\\s*'(.+?)'\\s*\\);$" );
    private static final int INSERT_INTO_GROUP_TITLE = 1;

    private static final String INFO_ID = "3";

    // example1: eng_rus_dictionary1\nEnglish\nl\nRussian\nr
    // example2: eng_rus_dictionary1\nEnglish\nr\nRussian\nr
    private static final Pattern INFO1_PATTERN = Pattern.compile( "^(\\S+)\\\\n(\\S+)\\\\nl\\\\n(\\S+)\\\\nr$" );
    private static final Pattern INFO2_PATTERN = Pattern.compile( "^(\\S+)\\\\n(\\S+)\\\\nr\\\\n(\\S+)\\\\nr$" );

    private String infoTitle;
    private String infoDescription;
    private TextualHelper helper;


    // for test purposes only
    PtkDictMySqlParser() {
    }

    public PtkDictMySqlParser( String fileName, String encoding, PtkDictMySqlReader reader, IMonitor monitor ) {
        helper = new TextualHelper( fileName, encoding, reader, monitor );
    }

    public void buildIndex( IDictionaryIndexBuilder builder ) throws IOException {
        helper.buildIndex( builder, new TextualHelper.IBuilderAdapter() {
            public void buildIndexFromCustomFormat( IDictionaryIndexBuilder builder, ILineReader lineReader, Info info ) throws IOException {
                PtkDictMySqlParser.this.buildIndexFromCustomFormat( builder, lineReader, info );
            }
        } );
    }

    private void buildIndexFromCustomFormat( IDictionaryIndexBuilder builder, ILineReader lineReader, Info info ) throws IOException {
        Token[] insertIntoValues = new Token[2];
        ByteArray lineBytes;
        while ( true ) {
            int lineStart = lineReader.getNextLineStart();

            if ( (lineBytes = lineReader.readLine()) == null ) {
                break;
            }

            // TODO optimize? parse(2143) - english3
            String line = new String( lineBytes.getBytes(), 0, lineBytes.getLength(), helper.getEncoding() );

            if ( isEmptyOrComment( line ) ) {
                // do nothing
            } else if ( isInsert( line ) ) {
                if ( infoTitle == null && isInsertInfo( line ) ) {
                    String[] values = extractInsertAsStrings( line );

                    infoTitle = Strings.unescapeSql( values[0] );
                    infoDescription = convertInfoDecription( values[1] );
                } else {
                    extractInsert( line, lineStart, insertIntoValues );

                    builder.addArticle( insertIntoValues[0], insertIntoValues[1] );
                }
            }
        }

        info.setTitle( infoTitle );
        info.setDescription( infoDescription );
    }

    boolean isEmptyOrComment( String line ) {
        if ( line.length() == 0 || line.startsWith( "#" ) ) {
            return true;
        }

        return false;
    }

    boolean isInsert( String line ) {
        return line.startsWith( INSERT_INTO_PREFIX );
    }

    boolean isInsertInfo( String insertIntoLine ) {
        int parenthesesIndex = insertIntoLine.indexOf( '(' );
        if ( parenthesesIndex == -1 ) {
            Arguments.doThrow( "parenthesesIndex == -1" );
        }

        int commaIndex = insertIntoLine.indexOf( ",", parenthesesIndex );
        if ( commaIndex == -1 ) {
            Arguments.doThrow( "commaIndex == -1" );
        }

        String id = insertIntoLine.substring( parenthesesIndex + 1, commaIndex );

        return id.trim().equals( INFO_ID );
    }

    String extractInsertInfoTitle( String insertIntoInfoLine ) {
        Matcher matcher = INSERT_INTO_PATTERN.matcher( insertIntoInfoLine );
        if ( matcher.find() ) {
            return matcher.group( INSERT_INTO_GROUP_TITLE );
        }

        throw Arguments.doThrow( "Can't extract info title from insert values from \"" + insertIntoInfoLine + "\"" );
    }

    String convertInfoDecription( String description ) {
        Matcher matcher = INFO1_PATTERN.matcher( description );

        if ( matcher.find() ) {
            description = matcher.group( 1 ) + ", " + matcher.group( 2 ) + "-" + matcher.group( 3 );
        } else {
            matcher = INFO2_PATTERN.matcher( description );
            if ( matcher.find() ) {
                description = matcher.group( 1 ) + ", " + matcher.group( 2 ) + "-" + matcher.group( 3 );
            }
        }

        description = Strings.unescapeSql( description );

        return description;
    }

    private int[] quotesIndexes = new int[4];

    // TODO optimize? parse(981) - english3 (see to the end of the method)
    private void extractInsert( String insertIntoLine, int offset, Token[] values ) {
        // insert into sdf values ( 1, 'sdfsdf', 'sdfsdf' );
        int quotesAdded = 0;
        for ( int i = 0; i < insertIntoLine.length(); i++ ) {
            char c = insertIntoLine.charAt( i );
            if ( c == '\\' ) {
                i++;
            } else if ( c == '\'' ) {
                if ( quotesAdded < quotesIndexes.length ) {
                    quotesIndexes[quotesAdded] = i;
                    quotesAdded++;
                } else {
                    States.doThrow( "Found wrong, " + (quotesAdded + 1)
                            + "th quote. Expected exactly 4 quotes (escaped quotes are not counted)" );
                }
            }
        }

        // 0 - title token, 1 - body token
        for ( int i = 0; i <= 1; i++ ) {
            int start = quotesIndexes[i * 2] + 1;
            int end = quotesIndexes[i * 2 + 1];
            values[i] = new Token( start + offset, end - start ); // TODO optimize? (extract method and see)
        }
    }

    String[] extractInsertAsStrings( String insertIntoLine ) {
        Matcher matcher = INSERT_INTO_PATTERN.matcher( insertIntoLine );
        if ( matcher.find() ) {
            return new String[] {
                    matcher.group( 1 ),
                    matcher.group( 2 )
            };
        }
        throw Arguments.doThrow( "Can't extract insert values from \"" + insertIntoLine + "\"" );
    }
}
