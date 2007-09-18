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

import ja.centre.util.assertions.States;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.reader.IConverter;
import ja.lingo.readers.sdictionary.compressor.ICompressor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BabilooConverter implements IConverter {
    private static final String UTF_8 = "UTF-8";

    private IDataAccessor compressor;

    public BabilooConverter( IInfo info ) throws IOException {
        BabilooDriver driver = null;
        try {
            driver = new BabilooDriver( info.getDataFileName() );

            compressor = driver.getCompressor();
        } finally {
            if ( driver != null ) {
                driver.close();
            }
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
            byte[] b = compressor.readData(bodyOffset, bodyLength);
            String body = transformToHTML(new String(b, "UTF-8"));
            if ( body.contains( "<t>" ) ) {
                // copied from mova converter request
                Matcher matcher = Pattern.compile( "(\\<t>.*?\\</t>)" ).matcher( body );
                StringBuffer buffer = new StringBuffer();
                while ( matcher.find() ) {
                    matcher.appendReplacement( buffer, "<span class=\"trans\">[$1]</span>" );
                }
                matcher.appendTail( buffer );
                body = buffer.toString();
            }
            return body;
        } catch ( IOException e ) {
            throw States.shouldNeverReachHere( e );
        }
    }
    
    public String transformToHTML(String input){
       
        String middle = input.replaceAll("<ROSA>"," <FONT COLOR=\"#cd4971\">");
        middle = middle.replaceAll("</ROSA>","</FONT>");
        
        middle = middle.replaceAll("<NEGRO>","<FONT COLOR=\"#000000\">");
        middle = middle.replaceAll("</NEGRO>","</FONT>");
        
        middle = middle.replaceAll("<VERDE>","<FONT COLOR=\"#008000\">");
        middle = middle.replaceAll("</VERDE>","</FONT>");
        
        middle = middle.replaceAll("<AZUL>","<FONT COLOR=\"#0000ff\">");
        middle = middle.replaceAll("</AZUL>","</FONT>");
                
        middle = middle.replaceAll("<GRIS>","<FONT COLOR=\"#808080\">");
        middle = middle.replaceAll("</GRIS>","</FONT>");
        
        middle = middle.replaceAll("<PRONUN>","<FONT COLOR=\"#cd4971\">");
        middle = middle.replaceAll("</PRONUN>","</FONT>");
        
        middle = middle.replaceAll("<P>","<BR");
        middle = middle.replaceAll("</P>","");
        
        middle = middle.replaceAll("<9PT>","<FONT STYLE=\"font-size: 14pt\">");
        middle = middle.replaceAll("</9PT>","</FONT>");
        
        middle = middle.replaceAll("<10PT>","<FONT STYLE=\"font-size: 16pt\">");
        middle = middle.replaceAll("</10PT>","</FONT>");
        
        middle = middle.replaceAll("<HEADPT>","<FONT STYLE=\"font-size: 18pt\">");
        middle = middle.replaceAll("</HEADPT>","</FONT>");
        
        middle = middle.replaceAll("<8PT>","<FONT STYLE=\"font-size: 12pt\">");
        middle = middle.replaceAll("</8PT>","</FONT>");
        
        return middle;
    }
}
