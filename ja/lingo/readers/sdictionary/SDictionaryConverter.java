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

import ja.centre.util.assertions.States;
import ja.centre.util.regex.IReplacer;
import ja.centre.util.regex.Replacers;
import ja.centre.util.io.Files;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.reader.IConverter;
import ja.lingo.readers.sdictionary.compressor.ICompressor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;

class SDictionaryConverter implements IConverter {
    private static final String UTF_8 = "UTF-8";

    private ICompressor compressor;

    private static final IReplacer REPLACER_TRANS = Replacers.regex(
            "(\\<t>.*?\\</t>)",
            "<span class=\"trans\">[$1]</span>",
            "<t>" );

    private Map<Character, Character> fixMap = new HashMap<Character, Character>();
    {
        fixMap.put( '\u255a', '\u00ab' );   // <<
        fixMap.put( '\u2569', '\u00bb' );   // >>
        fixMap.put( '\u255f', '\u00b0' );   // o (degrees)
        fixMap.put( '\u2565', '/' );        // kVt/4
        fixMap.put( '\u2553', '\u00b2' );   // 2 (<sup>2</sup>)
        fixMap.put( '\u2556', '\u00b6' );   // Paragraph
        //fixMap.put( '\u2567', '\u0000' );   // ??
        //fixMap.put( '\u2568', '\u0000' );   // ??
    }

    public SDictionaryConverter( IInfo info ) throws IOException {
        SDictionaryDriver driver = null;
        try {
            driver = new SDictionaryDriver( info.getDataFileName() );

            compressor = driver.getCompressor();
        } finally {
            Files.closeQuietly( driver );
        }
    }

    public String getTitle( byte[] titleBytes, int titleOffset, int titleLength ) {
        try {
            return new String( titleBytes, titleOffset, titleLength, UTF_8 );
        } catch ( UnsupportedEncodingException e ) {
            throw States.shouldNeverReachHere( e );
        }
    }

    public String getBody( byte[] titleBytes, int titleOffset, int titleLength, byte[] bodyBytes, int bodyOffset, int bodyLength ) {
        try {
            String body = new String( compressor.uncompress( bodyBytes, bodyOffset, bodyLength ), UTF_8 );
            body = fixBody( body );
            return REPLACER_TRANS.replace( body );
        } catch ( IOException e ) {
            throw States.shouldNeverReachHere( e );
        }
    }

    private String fixBody( String body ) {
        StringBuilder buf = new StringBuilder( body );

        for ( int i = 0; i < buf.length(); i++ ) {
            Character replace = fixMap.get( buf.charAt( i ) );
            if ( replace != null ) {
                buf.setCharAt( i, replace );
            }
        }

        return buf.toString();
    }
}
