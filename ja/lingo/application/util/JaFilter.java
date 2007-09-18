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

package ja.lingo.application.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class JaFilter extends FileFilter {
    private String[] extensions;
    private String description;

    public JaFilter( String description, String... extensions ) {
        this.extensions = extensions;
        this.description = description;
    }

    public boolean accept( File f ) {
        return f.isDirectory() || hasValidExtension( f.getName() );
    }

    public String getDescription() {
        return description;
    }

    public boolean hasValidExtension( String fileName ) {
        for ( String extension : extensions ) {
            if ( fileName.endsWith( extension ) ) {
                return true;
            }
        }
        return false;
    }

    public String appendExtensionIfNeeded( String fileName ) {
        if ( !hasValidExtension( fileName ) ) {
            fileName = fileName += extensions[0];
        }
        return fileName;
    }
}
