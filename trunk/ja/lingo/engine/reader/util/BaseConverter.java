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

package ja.lingo.engine.reader.util;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.reader.IConverter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class BaseConverter implements IConverter {
    private String encoding;

    public BaseConverter( IInfo info ) {
        Arguments.assertNotNull( "info", info );

        encoding = info.getDataFileEncoding();

        if ( !Charset.isSupported( encoding ) ) {
            Arguments.doThrow( "Character encoding \"" + encoding + "\" is not supported" );
        }
    }

    // TODO optimize? sort(690), merge(921) - for Mova
    public String getTitle( byte[] titleBytes, int titleOffset, int titleLength ) {
        return convertTitle( createString( titleBytes, titleOffset, titleLength ) );
    }

    public String getBody( byte[] titleBytes, int titleOffset, int titleLength, byte[] bodyBytes, int bodyOffset, int bodyLength ) {
        return convertBody( createString( titleBytes, titleOffset, titleLength ),
                createString( bodyBytes, bodyOffset, bodyLength ) );
    }

    protected String convertTitle( String title ) {
        return title;
    }
    protected String convertBody( String title, String body ) {
        return body;
    }

    protected String createString( byte[] bytes, int offset, int length ) {
        try {
            return new String( bytes, offset, length, encoding );
        } catch ( UnsupportedEncodingException e ) {
            throw States.shouldNeverReachHere( e );
        }
    }
}
