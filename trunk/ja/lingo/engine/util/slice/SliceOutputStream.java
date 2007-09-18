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

package ja.lingo.engine.util.slice;

import ja.lingo.engine.util.EngineFiles;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SliceOutputStream extends OutputStream {

    private String tempFileName;
    private FileOutputStream flushFosOnClose;

    private OutputStream tempOs;

    public SliceOutputStream( String prefix, FileOutputStream flushFosOnClose ) throws IOException {
        tempFileName = EngineFiles.createTemp( prefix + ".slice");

        tempOs = new BufferedOutputStream( new FileOutputStream( tempFileName ), 32768 );

        this.flushFosOnClose = flushFosOnClose;
    }

    public void write( int b ) throws IOException {
        tempOs.write( b );
    }
    public void write( byte b[] ) throws IOException {
        tempOs.write( b );
    }
    public void write( byte b[], int off, int len ) throws IOException {
        tempOs.write( b, off, len );
    }

    public void close() throws IOException {
        tempOs.close();

        EngineFiles.appendFileLengthWithContentAndDelete( tempFileName, flushFosOnClose );
    }
}
