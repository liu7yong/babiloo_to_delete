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

import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.reader.util.BaseConverter;
import ja.centre.util.regex.Replacers;
import ja.centre.util.regex.IReplacer;

import java.util.LinkedList;
import java.util.regex.Pattern;

class MovaConverter extends BaseConverter {
    public MovaConverter( IInfo info ) {
        super( info );
    }

    public String getBody( byte[] titleBytes, int titleOffset, int titleLength, byte[] bodyBytes, int bodyOffset, int bodyLength ) {
        return new MovaConverterRequest( convertIpa2Unicode( bodyBytes, bodyOffset, bodyLength ) ).asString();
    }

    private static class MovaConverterRequest {
        private static final String[] romanListElements = { "_I", "_II", "_III", "_IV", "_V", "_VI", "_VII", "_VIII", "_IX", "_X" };
        private static final String UPPER_ROMAN = "1";
        private static final String ARABIC_NUMBERS = "2";
        private static final String UPPER_ALPHA = "3";
        private static final String LOWER_ALPHA = "4";

        private static final Pattern PATTERN_SPLIT = Pattern.compile( " " );
        private static final IReplacer REPLACER_TRANS = Replacers.regex( "(\\[.*?\\])", "<span class=\"trans\">$1</span>" );
        private static final IReplacer REPLACER_KEYWORD = Replacers.regex( "_(\\S+?[\\.\\:])", "<span class=\"keywd\">$1</span>" );

        private StringBuilder builder = new StringBuilder();
        private LinkedList<ListEntry> listStack = new LinkedList<ListEntry>();

        private boolean lastWasCharacters;
        private String body;
        
        public MovaConverterRequest( String articleBody ) {
            // TODO optimize split
            for ( String token : PATTERN_SPLIT.split( articleBody ) ) {
                if ( isRomanListElement( token ) ) {
                    processListElement( UPPER_ROMAN );
                } else if ( isArabicPointListElement( token ) ) {
                    processListElement( ARABIC_NUMBERS );
                } else if ( isArabicGTListElement( token ) ) {
                    processListElement( UPPER_ALPHA );
                } else if ( isLetterGTListElement( token ) ) {
                    processListElement( LOWER_ALPHA );
                } else {
                    if ( lastWasCharacters ) {
                        builder.append( " " );
                    }

                    builder.append( token );

                    lastWasCharacters = true;
                }
            }
            flushListsIfRequired();

            String body = builder.toString(); // regex faster are faster on Strings

            // [transcription] - "(\[.*?\])"
            // TODO optimize
            body = REPLACER_TRANS.replace( body );

            // _n. ... _pl. - "_(\S+?[\.\:])"
            // TODO optimize
            body = REPLACER_KEYWORD.replace( body );

            this.body = body;

            builder = null;
        }

        public String asString() {
            return body;
        }

        private void processListElement( String style ) {
            ListEntry entry = findInListStack( style );
            if ( entry == null ) {
                entry = new ListEntry( style );
                addListToStack( entry );
            } else {
                flushListsIfRequired( entry );
            }

            entry.increment();
            flushLastListElement();
            addListElementToStack( style );

            lastWasCharacters = false;
        }

        private void flushLastListElement() {
            while ( !listStack.isEmpty() ) {
                if ( listStack.getLast().isElement() ) {
                    builder.append( "</span></li>" );
                    listStack.removeLast();
                } else {
                    break;
                }
            }
        }
        private void flushListsIfRequired() {
            flushListsIfRequired( null );
        }
        private void flushListsIfRequired( ListEntry entry ) {
            while ( listStack.size() > 0 ) {
                ListEntry currentEntry = listStack.getLast();
                if ( entry == currentEntry ) {
                    break;
                }
                builder.append( currentEntry.isElement() ? "</span></li>" : "</ol>" );
                listStack.removeLast();
            }
        }

        private boolean isRomanListElement( String token ) {
            if ( !token.startsWith( "_" ) ) {
                return false;
            }

            for ( int i = 0; i < romanListElements.length; i++ ) {
                if ( token.equals( romanListElements[i] ) ) {
                    return true;
                }
            }

            return false;
        }
        private boolean isArabicPointListElement( String token ) {
            if ( !token.endsWith( "." ) ) {
                return false;
            }

            for ( int i = 0; i < token.length() - 1; i++ ) {
                char c = token.charAt( i );
                if ( c < '0' || c > '9' ) {
                    return false;
                }
            }
            return true;
        }
        private boolean isArabicGTListElement( String token ) {
            if ( !token.endsWith( ">" ) ) {
                return false;
            }

            for ( int i = 0; i < token.length() - 1; i++ ) {
                char c = token.charAt( i );
                if ( c < '0' || c > '9' ) {
                    return false;
                }
            }
            return true;
        }
        private boolean isLetterGTListElement( String token ) {
            if ( !token.endsWith( ">" ) ) {
                return false;
            }

            char c = token.charAt( 0 );

            if ( c < 0x0430 || c > 0x044f ) {
                return false;
            }

            return true;
        }

        private ListEntry findInListStack( String name ) {
            for ( int i = listStack.size() - 1; i >= 0; i-- ) {
                ListEntry entry = listStack.get( i );
                if ( name.equals( entry.name ) ) {
                    return entry;
                }
            }
            return null;
        }
        private void addListToStack( ListEntry entry ) {
            builder.append( "<ol class=\"" ).append( entry.getName() ).append( "\">" );
            listStack.add( entry );
        }
        private void addListElementToStack( String name ) {
            ListEntry entry = new ListEntry( name, true );
            builder.append( "<li><span class='black'>" );
            listStack.add( entry );
        }

        private static class ListEntry {
            private String name;
            private int value;
            private boolean isElement;

            public ListEntry( String name ) {
                this( name, false );
            }
            public ListEntry( String name, boolean element ) {
                this.name = name;
                isElement = element;
            }

            public String getName() {
                return name;
            }
            public int getValue() {
                return value;
            }

            public void increment() {
                value++;
            }

            public boolean isElement() {
                return isElement;
            }
        }
    }

    public String convertIpa2Unicode( byte[] dataBytes, int bodyOffset, int bodyLength ) {
        StringBuilder builder = new StringBuilder( createString( dataBytes, bodyOffset, bodyLength ) );
        int bracketDepth = 0;
        for ( int i = 0; i < builder.length(); i++ ) {
            char currentChar = builder.charAt( i );
            if ( currentChar == '[' ) {
                bracketDepth++;
            } else if ( currentChar == ']' ) {
                bracketDepth--;
            }

            if ( bracketDepth > 0 ) {
                int b = (dataBytes[i + bodyOffset] & 0xFF);
                char newChar = IPA_2_UNICODE_TABLE[b];
                builder.setCharAt( i, newChar );
            }
        }

        return builder.toString();
    }

    private final char[] IPA_2_UNICODE_TABLE = {
            0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, // 0
            0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, // 16
            0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, // 32
            0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, 0x0020, // 48
            0x0020, 0x030B, 0x0131, 0x0304, 0x0300, 0x030F, 0x030C, 0x02BC, // 64

            0x0306, 0x0303, 0x030A, 0x031F, 0x002C, 0x0324, 0x002E, 0x002F,
            0x0330, 0x0318, 0x0319, 0x031D, 0x031E, 0x032A, 0x033B, 0x031C,
            0x0325, 0x032F, 0x02E1, 0x029F, 0x207F, 0x0320, 0x02D1, 0x0294,
            0x0301, 0x0251, 0x03B2, 0x0063, 0x00F0, 0x025B, 0x0264, 0x0262,
            0x02B0, 0x026A, 0x02B2, 0x029C, 0x026E, 0x0271, 0x014B, 0x00F8,

            0x0275, 0x00E6, 0x027E, 0x0283, 0x03B8, 0x028A, 0x028B, 0x02B7,
            0x03C7, 0x028F, 0x0292, 0x005B, 0x005C, 0x005D, 0x0302, 0x0308,
            0x0329, 0x0061, 0x0062, 0x0063, 0x0064, 0x0065, 0x0066, 0x0261,
            0x0068, 0x0069, 0x006A, 0x006B, 0x006C, 0x006D, 0x006E, 0x006F,
            0x0070, 0x0071, 0x0072, 0x0073, 0x0074, 0x0075, 0x0076, 0x0077,

            0x0078, 0x0079, 0x007A, 0x0280, 0x031A, 0x027D, 0x033D, 0x007F,
            0x02E9, 0x0252, 0x0258, 0x0361, 0x2016, 0x02E5, 0x02E5, 0x0298,
            0x030B, 0x030B, 0x02E5, 0x2191, 0x0250, 0x0254, 0x01C0, 0x0301,
            0x0301, 0x02E6, 0x01C1, 0x0304, 0x0304, 0x02E7, 0x007C, 0x01C3,
            0x0300, 0x0300, 0x02E8, 0x2193, 0x01C2, 0x030F, 0x030F, 0x02E9,

            0x0020, 0x030A, 0x031E, 0x031D, 0x032C, 0x0325, 0x0339, 0x0282,
            0x0279, 0x0260, 0x0319, 0x0259, 0x0289, 0x0320, 0x0268, 0x0276,
            0x033A, 0x031F, 0x0274, 0x02E4, 0x028E, 0x026F, 0x0020, 0x0020,
            0x0278, 0x02A2, 0x0253, 0x032F, 0x0330, 0x0290, 0x006A, 0x0153,
            0x0295, 0x0318, 0x026C, 0x028C, 0x0263, 0x0020, 0x029D, 0x02CC,

            0x02C8, 0xF180, 0x200A, 0xF181, 0x2197, 0x2198, 0x025C, 0x025E,
            0x0324, 0x033C, 0x0281, 0x027B, 0xF182, 0x02DE, 0x002D, 0x0284,
            0x02E7, 0x02E7, 0x030B, 0x0301, 0x0304, 0x0300, 0x030F, 0x0302,
            0x030C, 0x0306, 0x0303, 0x028D, 0x027A, 0x0270, 0x0302, 0x0265,
            0x02E9, 0x0302, 0x0256, 0x0257, 0x02E0, 0x203F, 0x0267, 0x025F,

            0x0127, 0x026D, 0x026B, 0x030C, 0x030C, 0x0299, 0x0268, 0x0273,
            0x0272, 0x003A, 0x0266, 0x02A1, 0x0291, 0x029B, 0x0255, 0x0288
    };
}
