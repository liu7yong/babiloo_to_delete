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

package ja.lingo.readers.babiloo;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.centre.util.io.nio.MappedByteBufferWrapper;
import ja.lingo.readers.sdictionary.compressor.Compressors;
import ja.lingo.readers.sdictionary.compressor.ICompressor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.io.File;

class BabilooDriver {
    private static final Log LOG = LogFactory.getLog( BabilooDriver.class );

    private static final String UTF_8 = "UTF-8";

    private MappedByteBufferWrapper wrapper;
    private MappedByteBuffer buffer;
    private String fileName;

    private String fData, fIndex;
    private String signature;
    private String inputLanguage;
    private String outputLanguage;
    private byte compression;
    private int capacity;
    private int shortIndexLength;
    private int titleUnitOffset;
    private int copyrightUnitOffset;
    private int versionUnitOffset;
    private int shortIndexOffset;
    private int fullIndexOffset;
    private int articlesOffset;

    private IDataAccessor fDataAccessor;
    private String fEncoding = "utf-8";

    private String fID;
    private KeyList fIndexKey;

    private String fName;

    private File indexFile, dataFile;
        
    private ICompressor compressor;

    public BabilooDriver( String fileName ) throws IOException {
        Arguments.assertNotNull( fileName, "fileName" );
        this.fileName = fileName;

        File f = new File(fileName);
	if (!f.exists()) {
		return;
	}
	LOG.info(new java.util.Date().toString());
	LOG.info("\nCreate dictionary engine using configuration "+fileName);
	DatabaseConfiguration[] configs = DatabaseConfiguration.readConfiguration(fileName);
        
        StringBuffer sb = new StringBuffer();
        DatabaseConfiguration dc = configs[0];
        String id = dc.getId();
        setID(id);
        dataFile = dc.getData();
        indexFile = dc.getIndex();
        fData = dc.getDataFileName();
        fIndex = dc.getTitlesDataFileName();
        if (!dataFile.exists()) {
                throw new IOException("File does not exist: "+dataFile);
        }
        if (!indexFile.exists()) {
                throw new IOException("File does not exist: "+indexFile);
        }
        sb.append("\nCreating database with data file " + dataFile);
        IDataAccessor acc = null;
        if (dataFile.getName().toLowerCase().endsWith(".dz")) {
                acc = new DictZipDataAccessor(dataFile.getAbsolutePath());
        } else {
                acc = new FlatDataAccessor(dataFile.getAbsolutePath());
        }
        setDataAccessor(acc);
        String enc = dc.getEncoding();
        setEncoding(enc);
        // Init key list
        KeyList kl = null;
        boolean inMemory = dc.isMemoryIndex();
        if (inMemory) {
                kl = new MemoryKeyList(indexFile.getAbsolutePath());
        } else {
                kl = new FileKeyList(indexFile.getAbsolutePath());
        }
        kl.setEncoding(enc);
        setIndex(kl);

        
                
//	//for (int i = 0; i<configs.length; i++){
//        for (int i = 0; i<1; i++){ //solo añade la primera
//		try {
//			Database datab = Database.createDatabase(configs[i]);
//		} catch (Throwable e) {
//			LOG.error( "Cannot create database "+configs[i], e);
//		}
//	}
//	LOG.info("\nDatabases created!");
        
        
    }

    public IDataAccessor getDataAccessor() {
		return fDataAccessor;
	}
    /**
     * Insert the method's description here.
     * Creation date: (28.07.01 22:01:41)
     * @return java.lang.String
     */

    /**
     * Insert the method's description here.
     * Creation date: (03.09.01 21:55:54)
     * @return java.lang.String
     */
    public java.lang.String getEncoding() {
            return fEncoding;
    }

    public java.lang.String getID() {
            return fID;
    }
    /**
     * Insert the method's description here.
     * Creation date: (28.07.01 22:01:41)
     * @return org.dict.IKeyList
     */
    public KeyList getIndex() {
            return fIndexKey;
    }
    /**
     * Insert the method's description here.
     * Creation date: (02.09.01 12:46:21)
     * @return org.dict.kernel.IMorphAnalyzer
     */

    /**
     * Insert the method's description here.
     * Creation date: (29.07.01 22:00:15)
     * @return java.lang.String
     */
    public java.lang.String getName() {
            return fName;
    }

        
    public void close() {
        if ( wrapper != null ) {
            try {
                wrapper.close();
            } catch ( IOException e ) {
                LOG.error( "Exception caught when tried to close wrapper", e );
            }
        }
    }

    
    public void setDataAccessor(IDataAccessor acc) {
            fDataAccessor = acc;
    }
    /**
     * Insert the method's description here.
     * Creation date: (03.09.01 21:55:54)
     * @param newEncoding java.lang.String
     */
    public void setEncoding(java.lang.String newEncoding) {
            fEncoding = newEncoding;
    }


    public void setID(java.lang.String newID) {
            fID = newID;
    }

    public void setIndex(KeyList newIndex) {
            fIndexKey = newIndex;
    }

    public void setName(java.lang.String newName) {
            fName = newName;
    }

    public short readShort( int offset ) {
        return buffer.getShort( offset );
    }

    public byte[] readUnit( int offset ) {
        int length = readInt( offset );
        return read( offset + 4, length );
    }

    public String readUnitAsString( int offset ) throws IOException {
        return new String( compressor.uncompress( readUnit( offset ) ), UTF_8 );
    }

    public int readInt( int offset ) {
        return buffer.getInt( offset );
    }

    public byte readByte( int offset ) {
        return buffer.get( offset );
    }

    public String readUtf8( int offset, int length ) {
        return asUtf8( read( offset, length ) );
    }
    
    
    public void setTitlesDataFileName(String _fIndex){
            fIndex = _fIndex;
    }
        
    public String getTitlesDataFileName(){
        return fIndex;
    }

    public void setDataFileName(String _fData){
        fData = _fData;
    }
    public String getDataFileName() {
            return fData;
    }
    
    
    public String asUtf8( byte[] bytes ) {
        try {
            return new String( bytes, UTF_8 );
        } catch ( UnsupportedEncodingException e ) {
            throw States.shouldNeverReachHere( e );
        }
    }

    public byte[] read( int offset, int length ) {
        byte[] bytes = new byte[length];
        buffer.position( offset );
        buffer.get( bytes );
        return bytes;
    }

    // helper methods
    public String getTitle() throws IOException {
        return "test";
    }

    public String getVersion() throws IOException {
        return "test";
    }

    public String getCopyright() throws IOException {
        return "test";
    }

    public int getCapacity() {
        return getIndex().size();
    }

    public int getFullIndexOffset() {
        return fullIndexOffset;
    }

    public int getArticlesOffset() {
        return articlesOffset;
    }

    public IDataAccessor getCompressor() {
        return getDataAccessor();
    }
}
