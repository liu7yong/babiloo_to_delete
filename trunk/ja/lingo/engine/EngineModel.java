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

import ja.lingo.engine.beans.IInfo;

import java.util.ArrayList;
import java.util.List;

// NOTE: must be public for JavaBeans XML Encoder/Decoder
public class EngineModel {
    private String cacheVersion;
    private List<IInfo> infos = new ArrayList<IInfo>();
    private long indexChecksum;

    public EngineModel() {
    }

    public String getCacheVersion() {
        return cacheVersion;
    }
    public void setCacheVersion( String cacheVersion ) {
        this.cacheVersion = cacheVersion;
    }

    public List<IInfo> getInfos() {
        return infos;
    }
    public void setInfos( List<IInfo> infos ) {
        this.infos = infos;
    }

    public long getIndexChecksum() {
        return indexChecksum;
    }
    public void setIndexChecksum( long indexChecksum ) {
        this.indexChecksum = indexChecksum;
    }
}
