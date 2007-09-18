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

package ja.lingo.engine.reader.util;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.io.checksum.ChecksumUtil;
import ja.centre.util.io.Files;
import ja.centre.util.io.linereader.ILineReader;
import ja.lingo.engine.beans.Info;
import ja.lingo.engine.dictionaryindex.builder.IDictionaryIndexBuilder;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.reader.IDictionaryReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class TextualHelper {
    private static final Log LOG = LogFactory.getLog( TextualHelper.class );

    private IDictionaryReader reader;

    private String fileName;
    private String encoding;

    private IMonitor monitor;

    public TextualHelper( String fileName, String encoding, IDictionaryReader reader, IMonitor monitor ) {
        Arguments.assertNotNull( "fileName", fileName );
        Arguments.assertNotNull( "encoding", encoding );
        Arguments.assertNotNull( "reader", reader );
        Arguments.assertNotNull( "monitor", monitor );

        this.fileName = fileName;
        this.encoding = encoding;
        this.reader = reader;
        this.monitor = monitor;
    }

    public void buildIndex( IDictionaryIndexBuilder builder, IBuilderAdapter adapter ) throws IOException {
        ILineReader lineReader= null;
        try {
            lineReader = createLineReader();
            Info info = createInfo();

            adapter.buildIndexFromCustomFormat( builder, lineReader, info );

            builder.setInfo( info );
        } finally {
            Files.closeQuietly( builder ); // TODO introduce revert/rollback method
            Files.closeQuietly( lineReader );
         }
    }

    public String getFileName() {
        return fileName;
    }
    public String getEncoding() {
        return encoding;
    }

    private ILineReader createLineReader() throws IOException {
        return new ObservableLineReader( fileName, monitor );
    }
    private Info createInfo() throws IOException {
        Info info = new Info();
        info.setReader( reader );
        info.setDataFileName( fileName );
        info.setDataFileEncoding( encoding );
        info.setDataFileChecksum( ChecksumUtil.calculateForLenMod( fileName ) );
        return info;
    }

    public static interface IBuilderAdapter {
        void buildIndexFromCustomFormat( IDictionaryIndexBuilder builder, ILineReader lineReader, Info info ) throws IOException;
    }
}
