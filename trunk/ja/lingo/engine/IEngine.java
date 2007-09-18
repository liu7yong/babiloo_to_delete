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

package ja.lingo.engine;

import ja.lingo.engine.beans.IArticle;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.monitor.IAddMonitor;
import ja.lingo.engine.monitor.ICompileMonitor;
import ja.lingo.engine.reader.IDictionaryReader;

import java.io.IOException;
import java.io.Closeable;
import java.util.Comparator;
import java.util.List;

public interface IEngine extends Closeable {
    List<IInfo> getInfos();

    void addDictionary( String dataFileName, String dataFileEncoding, IDictionaryReader reader ) throws IOException;
    void addDictionary( String dataFileName, String dataFileEncoding, IDictionaryReader reader, IAddMonitor monitor ) throws IOException;
    void remove( IInfo info );
    void swapDictionaries( int index0, int index1 );

    IFinder getFinder();
    IExporter getExporter();

    boolean isCompiled();
    void compile( ICompileMonitor monitor ) throws IOException;

    List<IDictionaryReader> getReaders();

    void addEngineListener( IEngineListener listener );

    Comparator<String> getTitleComparator();
    Comparator<IArticle> getArticleComparator();

    boolean contains( String dataFileName );
}
