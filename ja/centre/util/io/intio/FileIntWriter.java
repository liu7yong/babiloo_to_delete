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

package ja.centre.util.io.intio;

import ja.centre.util.assertions.Arguments;

import java.io.*;

public class FileIntWriter implements IIntWriter {
    private DataOutputStream dos;
    private int intsWritten;

    public FileIntWriter( String fileName ) throws FileNotFoundException {
        Arguments.assertNotNull( "fileName", fileName );

        this.dos = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( fileName ) ) );
    }

    public FileIntWriter( OutputStream os ) {
        dos = new DataOutputStream( new BufferedOutputStream( os ) );
    }

    public void writeInt( int value ) throws IOException {
        // TODO optimize? sort(240)
        dos.writeInt( value );
        intsWritten++;
    }

    public void close() throws IOException {
        dos.close();
        dos = null;
    }

    public int getIntsWrittenCount() {
        return intsWritten;
    }
}
