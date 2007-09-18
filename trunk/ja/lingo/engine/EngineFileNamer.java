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

import ja.centre.util.assertions.Arguments;

import java.io.File;

class EngineFileNamer {
    private String workingDirectory;

    public EngineFileNamer( String workingDirectory ) {
        Arguments.assertNotNull( "workingDirectory", workingDirectory );

        this.workingDirectory = workingDirectory;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }
    public String getCacheDirectory() {
        return calculateCacheFileName( "" );
    }

    public String calculateCacheFileName( String fileName ) {
        return calculateWorkingFileName( "cache" + File.separator + fileName );
    }
    public String calculateNextIndexFileName() {
        return calculateCacheFileName( System.currentTimeMillis() + ".index" );
    }

    public String getSearchIndexFileName() {
        return calculateCacheFileName( "search.index" );
    }
    public String getMergedIndexFileName() {
        return calculateCacheFileName( "merged.index" );
    }

    public String getModelFileName() {
        return calculateWorkingFileName( "dictionaries.xml" );
    }
    public String getLockFileName() {
        return calculateWorkingFileName( "lock" );
    }

    private String calculateWorkingFileName( String fileName ) {
        return workingDirectory + File.separator + fileName;
    }
}
