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

package ja.lingo.engine.mergedindex;

import ja.centre.util.assertions.Arguments;
import ja.lingo.engine.beans.IArticle;
import ja.lingo.engine.dictionaryindex.reader.IDictionaryIndex;
import ja.lingo.engine.util.slice.SliceOutputStream;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergedIndexBuilder implements IMergedIndexBuilder {
    private DataOutputStream dos;
    private Map<IDictionaryIndex, Integer> readerToIdMap;

    private DataOutputStream offsetLengthDos;
    private DataOutputStream groupsDos;

    private int articleOffset;

    private static final int BUFFER_SIZE = 65536;

    public MergedIndexBuilder( String fileName ) throws IOException {
        Arguments.assertNotNull( "fileName", fileName );

        FileOutputStream fos = new FileOutputStream( fileName );
        this.dos = new DataOutputStream( fos );

        this.offsetLengthDos = new DataOutputStream( new BufferedOutputStream(
                new SliceOutputStream( "offsetsLengths", fos ), BUFFER_SIZE ) );

        this.groupsDos = new DataOutputStream( new BufferedOutputStream(
                new SliceOutputStream( "groups", fos ), BUFFER_SIZE ) );
    }

    public void writeReaders( List<IDictionaryIndex> readers ) throws IOException {
        Arguments.assertNotNull( "readers", readers );

        initializeReaderToIdMap( readers );
        serializeReaderToIdMap();
    }

    public void write( IArticle article ) throws IOException {
        // serialize offset/length
        offsetLengthDos.writeInt( articleOffset );
        offsetLengthDos.writeInt( article.size() );

        articleOffset += article.size();

        // serialize groups
        for ( int i = 0; i < article.size(); i++ ) {
            groupsDos.writeInt( readerToIdMap.get( article.getReader( i ) ) );
            groupsDos.writeInt( article.getInReaderIndex( i ) );
        }
    }

    public void close() throws IOException {
        dos.flush(); // NOTE: for case if some buffer is between DOS and nested FOS

        // NOTE: close will also flush collected data. See SliceOutputStream.close()
        offsetLengthDos.close();
        groupsDos.close();

        dos.close();
    }

    private void initializeReaderToIdMap( List<IDictionaryIndex> readers ) {
        readerToIdMap = new HashMap<IDictionaryIndex, Integer>();
        for ( int i = 0; i < readers.size(); i++ ) {
            IDictionaryIndex reader = readers.get( i );
            readerToIdMap.put( reader, i );
        }
    }

    private void serializeReaderToIdMap() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream tempDos = new DataOutputStream( baos );

        tempDos.writeInt( readerToIdMap.size() );
        for ( Map.Entry<IDictionaryIndex, Integer> entry : readerToIdMap.entrySet() ) {
            tempDos.writeInt( entry.getValue() );
            tempDos.writeUTF( entry.getKey().getInfo().getIndexFileName() );
        }

        // serialize
        dos.writeInt( baos.size() );
        baos.writeTo( dos );
    }
}
