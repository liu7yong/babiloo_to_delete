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

import ja.centre.util.io.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ChecksumUtil {
    public static long calculateFull( String fileName ) throws FileNotFoundException, IOException {
        FileInputStream is = null;
        try {
            is = new FileInputStream( fileName );

            EnhancedChecksum checksum = new EnhancedChecksum();

            byte[] buffer = new byte[32768];
            int read;
            while ( (read = is.read( buffer )) != -1 ) {
                checksum.update( buffer, 0, read );
            }
            return checksum.getValue();
        } finally {
            if ( is != null ) {
                is.close();
            }
        }
    }

    public static long calculateForLenMod( String fileName ) throws FileNotFoundException, IOException {
        File file = Files.create( fileName );

        return new EnhancedChecksum()
                .update( file.lastModified() )
                .update( file.length() )
                .getValue();
    }
}
