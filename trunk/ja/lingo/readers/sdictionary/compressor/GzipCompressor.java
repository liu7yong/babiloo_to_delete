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

package ja.lingo.readers.sdictionary.compressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

class GzipCompressor extends ACompressor {
    public byte[] uncompress( byte[] bytes, int offset, int length ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream gzis = new InflaterInputStream( new ByteArrayInputStream( bytes, offset, length ) );

        byte[] buffer = new byte[32768];
        int read;
        while ( (read = gzis.read( buffer )) != -1 ) {
            baos.write( buffer, 0, read );
        }
        gzis.close();

        return baos.toByteArray();
    }
}
