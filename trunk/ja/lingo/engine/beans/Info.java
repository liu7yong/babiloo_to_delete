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

package ja.lingo.engine.beans;

import ja.centre.util.assertions.Arguments;
import ja.lingo.engine.reader.IDictionaryReader;
import ja.lingo.application.util.misc.Strings;

public class Info implements IInfo {
    private String title;
    private String description;

    private int capacity;

    private String indexFileName;

    private String dataFileName;
    private String dataFileEncoding;
    private long dataFileChecksum;

    private IDictionaryReader reader;

    public Info() {
    }

    public Info( IInfo info ) {
        Arguments.assertNotNull( "info", info );

        setTitle( info.getTitle() );
        setDescription( info.getDescription() );
        setCapacity( info.getCapacity() );
        setIndexFileName( info.getIndexFileName() );
        setDataFileName( info.getDataFileName() );
        setDataFileEncoding( info.getDataFileEncoding() );
        setDataFileChecksum( info.getDataFileChecksum() );
        setReader( info.getReader() );
    }

    public Info( String dataFileName, String dataFileEncoding ) {
        this.dataFileName = dataFileName;
        this.dataFileEncoding = dataFileEncoding;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle( String title ) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription( String description ) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }
    public void setCapacity( int capacity ) {
        this.capacity = capacity;
    }

    public String getIndexFileName() {
        return indexFileName;
    }
    public void setIndexFileName( String indexFileName ) {
        this.indexFileName = indexFileName;
    }

    public String getDataFileName() {
        return dataFileName;
    }
    public void setDataFileName( String dataFileName ) {
        this.dataFileName = dataFileName;
    }

    public String getDataFileEncoding() {
        return dataFileEncoding;
    }
    public void setDataFileEncoding( String dataFileEncoding ) {
        this.dataFileEncoding = dataFileEncoding;
    }

    public long getDataFileChecksum() {
        return dataFileChecksum;
    }
    public void setDataFileChecksum( long dataFileChecksum ) {
        this.dataFileChecksum = dataFileChecksum;
    }

    public IDictionaryReader getReader() {
        return reader;
    }
    public void setReader( IDictionaryReader reader ) {
        this.reader = reader;
    }

    public boolean equals( Object obj ) {
        if ( obj == this ) {
            return true;
        }

        if ( obj == null || getClass() != obj.getClass() ) {
            return false;
        }

        IInfo info = (IInfo) obj;
        return getIndexFileName().equals( info.getIndexFileName() );
    }

    public int hashCode() {
        return getIndexFileName().hashCode();
    }

    public String toString() {
        return "Info{" +
                "title='" + title + '\'' +
                ", description='" + Strings.cutIfNecessary( description ) + '\'' +
                ", capacity=" + capacity +
                ", indexFileName='" + indexFileName + '\'' +
                ", dataFileName='" + dataFileName + '\'' +
                ", dataFileEncoding='" + dataFileEncoding + '\'' +
                ", dataFileChecksum=" + dataFileChecksum +
                ", reader=" + reader +
                '}';
    }
}
