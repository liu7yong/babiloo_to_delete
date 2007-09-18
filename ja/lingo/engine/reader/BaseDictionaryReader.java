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

import ja.centre.gui.resources.Resources;
import ja.lingo.application.util.JaFilter;

import javax.swing.filechooser.FileFilter;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDictionaryReader implements IDictionaryReader {
    protected List<String> supportedEncodings;

    protected String name;
    protected String title;
    protected String description;

    protected String[] extensions;

    protected FileFilter fileFilter;

    protected BaseDictionaryReader() {
        this( true );
    }

    protected BaseDictionaryReader( boolean useDefaultEncodings ) {
        supportedEncodings = new ArrayList<String>();

        if ( useDefaultEncodings ) {
            supportedEncodings.add( "KOI8-R" );
            supportedEncodings.add( "CP1251" );
            supportedEncodings.add( "ISO-8859-1" );
        }

        description = Resources.asString( getClass(), ".html" );
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSupportedEncodings() {
        return supportedEncodings;
    }

    public FileFilter getFileFilter() {
        if ( fileFilter == null
                && extensions != null
                && extensions.length != 0 ) {
            fileFilter = new JaFilter( title + " files", extensions );
        }

        return fileFilter;
    }
}
