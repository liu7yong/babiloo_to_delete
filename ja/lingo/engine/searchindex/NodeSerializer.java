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

package ja.lingo.engine.searchindex;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.centre.util.io.Files;
import ja.centre.util.measurer.TimeMeasurer;
import ja.lingo.engine.util.EngineFiles;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class NodeSerializer {
    private static final Log LOG = LogFactory.getLog( NodeSerializer.class );

    private DataOutputStream tempDos;
    private int firstLevelNodesCount;

    private String fileName;
    private TimeMeasurer timeMeasurer;
    private String tempFileName;

    public NodeSerializer( String fileName ) throws IOException {
        Arguments.assertNotNull( "fileName", fileName );
        this.fileName = fileName;

        tempFileName = EngineFiles.createTemp( "search.index" );

        tempDos = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( tempFileName ) ) );

        timeMeasurer = new TimeMeasurer( false );
    }

    public void serialize( INode node ) throws IOException {
        firstLevelNodesCount++;

        serialize( node, tempDos );
    }

    public void close() throws IOException {
        tempDos.close();

        LOG.info( "calculateSize(...) spent " + timeMeasurer );

        long size = Files.length( tempFileName );
        if ( size > Integer.MAX_VALUE ) {
            States.doThrow( "File is \"" + fileName
                    + "\"to large. Maximum allowed size is " + Integer.MAX_VALUE
                    + ". Actual size is " + size );
        }


        FileOutputStream fos = new FileOutputStream( fileName );

        DataOutputStream dos = new DataOutputStream( fos );
        serializeNodeHeader( dos, (int) size, firstLevelNodesCount, '\u0000', 0 );
        dos.flush();

        EngineFiles.appendFileAndDelete( tempFileName, fos );
        fos.close();
    }

    private void serialize( INode node, DataOutputStream dos ) throws IOException {
        Arguments.assertNotNull( "node", node );

        int size = calculateSize( node );
        serializeNodeHeader( dos, size, node.childrenCount(), node.getKey(), node.getValue() );

        for ( int i = 0; i < node.childrenCount(); i++ ) {
            serialize( node.getChild( i ), dos );
        }
    }

    private void serializeNodeHeader( DataOutputStream dos, int size, int childrenCount, char key, int value ) throws IOException {
        dos.writeInt( size );
        dos.writeInt( childrenCount );
        dos.writeInt( key );
        dos.writeInt( value );
    }

    private static final int HEADER_SIZE = 16;
    private int size;

    private int calculateSize( INode node ) {
        timeMeasurer.start();

        size = 0;
        _calculateSize( node );

        timeMeasurer.stop();

        return size;
    }

    private void _calculateSize( INode node ) {
        size += HEADER_SIZE;
        for ( int i = 0; i < node.childrenCount(); i++ ) {
            _calculateSize( node.getChild( i ) );
        }
    }

}
