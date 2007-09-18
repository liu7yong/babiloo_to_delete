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
import ja.centre.util.beans.BeanPersister;
import ja.centre.util.io.checksum.ChecksumUtil;
import ja.centre.util.io.checksum.EnhancedChecksum;
import ja.centre.util.io.Files;
import ja.centre.util.measurer.TimeMeasurer;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.beans.Info;
import ja.lingo.engine.dictionaryindex.reader.DictionaryIndex;
import ja.lingo.engine.dictionaryindex.reader.IDictionaryIndex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.*;

class EngineModelManager {
    private static final Log LOG = LogFactory.getLog( EngineModelManager.class );

    private List<InfoReaderPair> infoReaderPairs = new ArrayList<InfoReaderPair>();
    private Map<IInfo, IDictionaryIndex> infoToReaderMapForRemoval = new HashMap<IInfo, IDictionaryIndex>();

    private List<IInfo> removedInfos;
    private List<IInfo> dirtyInfos;

    private EngineFileNamer fileNamer;
    private long loadedIndexChecksum;
    private long currentIndexChecksum;

    private static final String VERSION_051 = "0.5.1"; // version that requires rebuild of search index
    static final String VERSION_CURRENT = "0.5.2";

    private IVersioner versioner;

    // for test purposes
    EngineModelManager( IVersioner versioner ) {
        this.versioner = versioner;
    }
    public EngineModelManager( EngineFileNamer fileNamer ) throws UnknownCacheVersionException, IOException {
        Arguments.assertNotNull( "fileNamer", fileNamer );
        this.fileNamer = fileNamer;

        versioner = new Versioner();

        EngineModel model = BeanPersister.load( EngineModel.class, this.fileNamer.getModelFileName() );

        calculateDirtyAndRemovedInfos( model );

        checkCacheVersion( model.getCacheVersion() );

        loadedIndexChecksum = model.getIndexChecksum();

        List<IInfo> infos = new ArrayList<IInfo>();
        infos.addAll( model.getInfos() );
        infos.removeAll( dirtyInfos );
        infos.removeAll( removedInfos );

        for ( IInfo info : infos ) {
            LOG.info( "Loading index: " + info );
            enqueueForAdd( info );
        }
    }

    void checkCacheVersion( String existingVersion ) throws UnknownCacheVersionException, IOException {
        if ( existingVersion != null ) {
            if ( VERSION_CURRENT.compareTo( existingVersion ) < 0 ) {
                versioner.throwUnknownVersion( existingVersion, VERSION_CURRENT );
            } else {
                if ( VERSION_051.compareTo( existingVersion ) >= 0 ) {
                    versioner.updateVersion_0_5( existingVersion, VERSION_CURRENT );
                }
            }
        }
    }

    public void enqueueForAdd( IInfo info ) throws IOException {
        info = new Info( info );
        infoReaderPairs.add( new InfoReaderPair( info, new DictionaryIndex( info, info.getIndexFileName() ) ) );
    }

    public void enqueForRemoval( IInfo info ) {
        info = new Info( info );
        int index = indexOfInfoReaderPair( info );

        if ( index == -1 ) {
            Arguments.doThrow( "Given dictionary info with index file name \""
                    + info.getIndexFileName() + "\" is not registered" );
        }

        infoToReaderMapForRemoval.put( info, infoReaderPairs.get( index ).getReader() );

        infoReaderPairs.remove( index );
    }

    public void swapDictionaries( int info0, int info1 ) {
        Arguments.assertInBounds( "info0", info0, 0, infoReaderPairs.size() - 1 );
        Arguments.assertInBounds( "info1", info1, 0, infoReaderPairs.size() - 1 );

        InfoReaderPair infoReaderPair0 = infoReaderPairs.get( info0 );
        InfoReaderPair infoReaderPair1 = infoReaderPairs.get( info1 );

        infoReaderPairs.set( info0, infoReaderPair1 );
        infoReaderPairs.set( info1, infoReaderPair0 );
    }

    public Map<IInfo, IDictionaryIndex> ejectForRemovalMap() {
        Map<IInfo, IDictionaryIndex> infoToReaderMapForRemovalCopy = infoToReaderMapForRemoval;

        infoToReaderMapForRemoval = new HashMap<IInfo, IDictionaryIndex>();

        return infoToReaderMapForRemovalCopy;
    }

    public List<IDictionaryIndex> getIndexes() {
        List<IDictionaryIndex> readers = new ArrayList<IDictionaryIndex>();
        for ( InfoReaderPair infoReaderPair : infoReaderPairs ) {
            readers.add( infoReaderPair.getReader() );
        }
        return Collections.unmodifiableList( readers );
    }

    public List<IInfo> getInfos() {
        List<IInfo> infos = new ArrayList<IInfo>();
        for ( InfoReaderPair infoReaderPair : infoReaderPairs ) {
            infos.add( infoReaderPair.getInfo() );
        }
        return infos;
    }

    public List<IInfo> getDirtyInfos() {
        return Collections.unmodifiableList( dirtyInfos );
    }

    public List<IInfo> getRemovedInfos() {
        return Collections.unmodifiableList( removedInfos );
    }

    public Map<String, IDictionaryIndex> getIndexFileNameToReaderMap() {
        Map<String, IDictionaryIndex> fileNameToReaderMap = new HashMap<String, IDictionaryIndex>();
        for ( InfoReaderPair infoReaderPair : infoReaderPairs ) {
            fileNameToReaderMap.put(
                    infoReaderPair.getInfo().getIndexFileName(),
                    infoReaderPair.getReader()
            );
        }
        return Collections.unmodifiableMap( fileNameToReaderMap );
    }

    private int indexOfInfoReaderPair( IInfo info ) {
        for ( int i = 0; i < infoReaderPairs.size(); i++ ) {
            InfoReaderPair infoReaderPair = infoReaderPairs.get( i );
            if ( infoReaderPair.getInfo().equals( info ) ) {
                return i;
            }
        }
        return -1;
    }

    public void save() throws IOException {
        EngineModel model = new EngineModel();
        model.setCacheVersion( VERSION_CURRENT );
        model.setInfos( getInfos() );
        model.setIndexChecksum( calculateIndexChecksum() );

        BeanPersister.save( model, fileNamer.getModelFileName() );
    }

    private long calculateIndexChecksum() throws IOException {
        EnhancedChecksum checksum = new EnhancedChecksum();
        checksum.update( ChecksumUtil.calculateForLenMod( fileNamer.getMergedIndexFileName() ) );
        checksum.update( ChecksumUtil.calculateForLenMod( fileNamer.getSearchIndexFileName() ) );
        for ( IInfo info : getInfos() ) {
            checksum.update( ChecksumUtil.calculateForLenMod( info.getDataFileName() ) );
            checksum.update( ChecksumUtil.calculateForLenMod( info.getIndexFileName() ) );
        }
        return checksum.getValue();
    }

    public boolean isCorrupted() throws IOException {
        // ensure merged/search indexed exist
        if ( !Files.exists( fileNamer.getMergedIndexFileName() )
                || !Files.exists( fileNamer.getSearchIndexFileName() ) ) {
            return true;
        }

        // ensure all data files and their index files exist
        for ( IInfo info : getInfos() ) {
            if ( !Files.exists( info.getDataFileName() )
                    || !Files.exists( info.getIndexFileName() ) ) {
                return true;
            }
        }

        // generate checksum and compare with previous/last commited
        currentIndexChecksum = calculateIndexChecksum();
        if ( loadedIndexChecksum != currentIndexChecksum || loadedIndexChecksum == 0 ) {
            return true;
        }

        return false;
    }

    private void calculateDirtyAndRemovedInfos( EngineModel model ) throws IOException {
        TimeMeasurer measurer = new TimeMeasurer();
        LOG.info( "Validating indexes..." );

        // determine indexes that need to be rebuilt: 1) absent ones 2) with incorrect checksum
        removedInfos = new ArrayList<IInfo>();
        dirtyInfos = new ArrayList<IInfo>();
        for ( IInfo info : model.getInfos() ) {
            // filter removed dictionaries
            if ( !Files.exists( info.getDataFileName() ) ) {
                removedInfos.add( info );
                continue;
            }

            // compare checksums
            long expectedChecksum = info.getDataFileChecksum();
            long actualChecksum = ChecksumUtil.calculateForLenMod( info.getDataFileName() );

            if ( expectedChecksum != actualChecksum ) {
                // case 1: index's data file has incorrect checksum
                dirtyInfos.add( info );

                LOG.info( "Invalidating: data file changed: " + info );
            }

            // check case 2: index file is absent
            if ( !Files.exists( info.getIndexFileName() ) ) {
                dirtyInfos.add( info );

                LOG.info( "Invalidating: index doesn't exist: " + info );
            }
        }

        // TODO replace with merged index existance check
        if ( model.getInfos().isEmpty() ) {
            LOG.info( "Invalidating: there are no dictionaries" );
        }

        if ( !Files.exists( fileNamer.getMergedIndexFileName() ) ) {
            // TODO add checksum check
            LOG.info( "Invalidating: merged index file \"" + fileNamer.getMergedIndexFileName() + "\" doesn't exist" );
            //} else if ( ChecksumUtil.calculate( getMergedIndexFileName() ) != in) {
        }

        if ( !Files.exists( fileNamer.getSearchIndexFileName() ) ) {
            // TODO FIXME
            LOG.info( "Invalidating: search index file \"" + fileNamer.getSearchIndexFileName() + "\" doesn't exist" );
        }

        LOG.info( "Validating indexes: " + removedInfos.size() + " removed, " + dirtyInfos.size() + " dirty.");

        LOG.info( "Validating indexes: done within " + measurer );
    }

    private static class InfoReaderPair {
        private IInfo info;
        private IDictionaryIndex reader;

        public InfoReaderPair( IInfo info, IDictionaryIndex reader ) {
            this.info = info;
            this.reader = reader;
        }

        public IInfo getInfo() {
            return info;
        }

        public IDictionaryIndex getReader() {
            return reader;
        }
    }

    static interface IVersioner {
        void throwUnknownVersion( String existingVersion, String currentVersion ) throws UnknownCacheVersionException;
        void updateVersion_0_5( String existingVersion, String currentVersion ) throws IOException;
    }

    private class Versioner implements IVersioner {
        public void throwUnknownVersion( String existingVersion, String currentVersion ) throws UnknownCacheVersionException {
            throw new UnknownCacheVersionException( existingVersion, VERSION_CURRENT );
        }
        public void updateVersion_0_5( String existingVersion, String currentVersion ) throws IOException {
            LOG.info( "Found cache version that requires search index rebuild" );
            Files.delete( fileNamer.getSearchIndexFileName() );
        }
    }
}
