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

package ja.centre.util.io.checksum;

import java.util.zip.CRC32;

public class EnhancedChecksum {
    private CRC32 checksum = new CRC32();

    public EnhancedChecksum update( long longValue ) {
        checksum.update( (byte) ((longValue) & 0xff) );
        checksum.update( (byte) ((longValue >> 8) & 0xff) );
        checksum.update( (byte) ((longValue >> 16) & 0xff) );
        checksum.update( (byte) ((longValue >> 32) & 0xff) );
        return this;
    }

    public EnhancedChecksum update( int b ) {
        checksum.update( b );
        return this;
    }

    public EnhancedChecksum update( byte[] b, int off, int len ) {
        checksum.update( b, off, len );
        return this;
    }

    public EnhancedChecksum update( byte[] b ) {
        checksum.update( b );
        return this;
    }

    public EnhancedChecksum reset() {
        checksum.reset();
        return this;
    }

    public long getValue() {
        return checksum.getValue();
    }
}
