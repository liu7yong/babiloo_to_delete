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

package ja.lingo.engine.reader;

import ja.lingo.engine.beans.IInfo;
import ja.lingo.application.util.progress.IMonitor;

import javax.swing.filechooser.FileFilter;
import java.util.List;

public interface IDictionaryReader {
    IParser createParser( IInfo info, IMonitor monitor );

    IConverter createConverter( IInfo info );

    String getName();
    String getTitle();
    String getDescription();

    List<String>getSupportedEncodings();
    FileFilter getFileFilter();
}
