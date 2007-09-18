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

package ja.centre.util.io.lock;

import ja.centre.util.io.Files;
import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;

import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;

class DeletingFileLockImpl implements ILock {
    private String fileName;
    private FileOutputStream fos;
    private boolean wasOverwritten;

    public DeletingFileLockImpl( String fileName ) throws LockedException, IOException {
        Arguments.assertNotNull( "fileName", fileName );
        this.fileName = fileName;

        wasOverwritten = Files.exists( fileName );

        fos = new FileOutputStream( fileName );

        FileLock lock = fos.getChannel().tryLock();
        if ( lock == null ) {
            throw new LockedException( "File \"" + fileName + "\" is already locked" );
        }
    }

    public boolean isLocked() {
        return fos != null;
    }

    public void release() throws IOException {
        States.assertTrue( isLocked(), "Expected to be not locked" );

        fos.close();
        fos = null;

        Files.delete( fileName );
    }

    public boolean wasOverwritten() {
        return wasOverwritten;
    }
}
