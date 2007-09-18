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

package ja.centre.util.sort.external;

import ja.centre.util.io.Files;
import ja.centre.util.sort.ISorter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FileMultipartWriter <T> extends AMultipartWriter<T> {
    private static final Log LOG = LogFactory.getLog( FileMultipartWriter.class );

    private Map<Integer,File> partIndexToFileMap = new HashMap<Integer, File>();

    public FileMultipartWriter( IPersister<T> persister, ISorter<T> sorter, Comparator<T> comparator, int valuesInMemory ) {
        super( persister, sorter, comparator, valuesInMemory );
    }

    protected OutputStream createOutputStreamForPart( int partIndex ) throws IOException {
        File file = createFileForPart( partIndex );

        LOG.info( "Created output file \"" + file.getAbsolutePath() + "\" for part " + partIndex );

        partIndexToFileMap.put( partIndex, file );

        return new FileOutputStream( file );
    }

    protected File createFileForPart( int partIndex ) throws IOException {
        return Files.createTempFile( "ja.centre.util.sort.external." );
    }

    protected InputStream createInputStreamForPart( int partIndex ) throws IOException {
        return new FileInputStream( partIndexToFileMap.get( partIndex ) );
    }

    protected void cleanDataForPart( int partIndex ) throws IOException {
        // skip if that part was in-memory
        if ( !partIndexToFileMap.containsKey( partIndex ) )
            return;

        File file = partIndexToFileMap.get( partIndex );

        if ( !file.delete() )
            LOG.warn( "Could not delete temporary file \"" + file.getAbsolutePath() + "\". Anyway, \"File.deleteOnExit()\" will fix it on JVM exit" );
    }
}
