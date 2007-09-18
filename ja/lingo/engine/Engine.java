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
import ja.centre.util.io.Files;
import ja.centre.util.io.lock.ILock;
import ja.centre.util.io.lock.LockedException;
import ja.centre.util.io.lock.Locks;
import ja.centre.util.measurer.TimeMeasurer;
import ja.lingo.engine.beans.IArticle;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.beans.Info;
import ja.lingo.engine.dictionaryindex.builder.DictionaryIndexBuilder;
import ja.lingo.engine.dictionaryindex.builder.IDictionaryIndexBuilder;
import ja.lingo.engine.dictionaryindex.builder.sorter.DictionaryIndexSorter;
import ja.lingo.engine.dictionaryindex.reader.DictionaryIndex;
import ja.lingo.engine.dictionaryindex.reader.IDictionaryIndex;
import ja.lingo.engine.mergedindex.ChannelMergedIndex;
import ja.lingo.engine.mergedindex.IMergedIndex;
import ja.lingo.engine.mergedindex.MergedIndexBuilder;
import ja.lingo.engine.mergedindex.MergedIndexMerger;
import ja.lingo.engine.monitor.IAddMonitor;
import ja.lingo.engine.monitor.ICompileMonitor;
import ja.lingo.engine.monitor.NullMonitor;
import ja.lingo.engine.reader.IDictionaryReader;
import ja.lingo.engine.reader.IParser;
import ja.lingo.engine.reader.Readers;
import ja.lingo.engine.searchindex.ISearchIndex;
import ja.lingo.engine.searchindex.ISearchIndexBuilder;
import ja.lingo.engine.searchindex.SearchIndex;
import ja.lingo.engine.searchindex.SearchIndexBuilder;
import ja.lingo.engine.util.EngineFiles;
import ja.lingo.engine.util.comparators.ArticleComparator;
import ja.lingo.engine.util.comparators.CollatingStringComparator;
import ja.lingo.engine.util.scorer.ProgressScorer;
import ja.lingo.engine.util.scorer.ScoringDictionaryIndex;
import ja.lingo.engine.util.scorer.ScoringDictionaryIndexBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Engine implements IEngine {
    private static final Log LOG = LogFactory.getLog( Engine.class );

    private static final Comparator<String> COMPARATOR_TITLE = new CollatingStringComparator();
    private static final ArticleComparator COMPARATOR_ARTICLE = new ArticleComparator();

    private static final IExporter EXPORTER = new Exporter();
    
    private ILock lock;

    private IMergedIndex mergedIndex;
    private ISearchIndex searchIndex;

    private EngineModelManager manager;
    private EngineFileNamer fileNamer;
    private IFinder finder;

    private EngineListeners listeners = new EngineListeners();

    private EngineState state = EngineState.UNCOMPILED;

    public Engine() throws UnknownCacheVersionException, IOException, LockedException {
        this( EngineFiles.calculateWorking() );
    }
    public Engine( String workingDirectory ) throws UnknownCacheVersionException, IOException, LockedException {
        Arguments.assertNotNull( "workingDirectory", workingDirectory );

        try {
            fileNamer = new EngineFileNamer( workingDirectory );

            lockDown();

            manager = new EngineModelManager( fileNamer );

            // clean unused indexes
            for ( IInfo info : manager.getRemovedInfos() ) {
                Files.deleteQuietly( info.getIndexFileName() );
            }

            rebuildDirtyInfos();

            finder = new Finder( this );

            compile( new NullMonitor() );
        } finally {
            if ( !isCompiled() ) {
                close();

                if ( lock != null ) {
                    lock.release();
                }
            }
        }

        /**
        // todo remove
        Writer writer = new OutputStreamWriter( new FileOutputStream( "c:/temp/1.txt" ), "UTF-16LE" );
        for ( int i = 0; i < getFinder().size(); i++ ) {
            IArticle article = getFinder().get( i );

            out:
            for ( int j = 0; j < article.size(); j++ ) {
                String body = article.getBody( j );
                for ( int k = 0; k < body.length(); k++ ) {
                    char c = body.charAt( k );
                    if ( c >= '\u250c' && c <= '\u25ab' ) {
                        writer.write( c + " - article = " + article.getTitle() );
                        writer.write( "\r\n" );
                        break out;
                    }
                }
            }
        }
        writer.close();
        /**/
    }

    public void addDictionary( String dataFileName, String dataFileEncoding, IDictionaryReader reader ) throws IOException {
        addDictionary( dataFileName, dataFileEncoding, reader, new NullMonitor() );
    }
    public void addDictionary( String dataFileName, String dataFileEncoding, IDictionaryReader reader, IAddMonitor monitor ) throws IOException {
        state.assertOpen();

        dataFileName = Files.getCanonicalPath( dataFileName );

        // check if already added
        if ( contains( dataFileName ) ) {
            return;
        }

        monitor.parsing();

        LOG.info( "Adding \"" + dataFileName + "\"..." );


        // build +1 index + add
        String indexFileName = fileNamer.calculateNextIndexFileName(); // TODO introduce better id generator

        // open reader
        IParser parser = reader.createParser( new Info( dataFileName, dataFileEncoding ), monitor );

        // build index
        IInfo builtInfo = buildIndex( parser, indexFileName, monitor );
        manager.enqueueForAdd( builtInfo );

        setState( EngineState.UNCOMPILED );

        listeners.dictionaryAdded( builtInfo );
    }
    public boolean contains( String dataFileName ) {
        for ( IInfo info : getInfos() ) {
            if ( info.getDataFileName().equals( dataFileName ) ) {
                return true;
            }
        }
        return false;
    }

    public void remove( IInfo info ) {
        state.assertOpen();

        LOG.info( "Removing \"" + info.getDataFileName() + "\"..." );

        manager.enqueForRemoval( info );

        setState( EngineState.UNCOMPILED );

        listeners.dictionaryDeleted( info );
    }

    public void swapDictionaries( int index0, int index1 ) {
        manager.swapDictionaries( index0, index1 );

        setState( EngineState.UNCOMPILED );

        listeners.dictionariesSwaped( index0, index1 );
    }

    public List<IDictionaryReader> getReaders() {
        return Readers.getReaders();
    }

    public void close() throws IOException {
        if ( state == EngineState.CLOSED ) {
            return;
        }

        Files.closeQuietly( mergedIndex );
        Files.closeQuietly( searchIndex );

        if ( manager != null ) {
            closeIndexes( manager.getIndexes() );
        }

        setState( EngineState.CLOSED );
    }

    public IMergedIndex getMergedIndex() {
        state.assertCompiled();
        return mergedIndex;
    }
    public ISearchIndex getSearchIndex() {
        state.assertCompiled();
        return searchIndex;
    }

    public List<IInfo> getInfos() {
        state.assertOpen();
        return manager.getInfos();
    }

    public boolean isCompiled() {
        return state == EngineState.COMPILED;
    }

    public void compile( ICompileMonitor monitor ) throws IOException {
        state.assertOpen();

        if ( state == EngineState.COMPILED ) {
            return;
        }

        LOG.info( "Compiling ..." );

        Files.close( mergedIndex );
        mergedIndex = null;

        Files.close( searchIndex );
        searchIndex = null;

        deleteNotNeededIndexes();

        boolean needsRebuild = manager.isCorrupted();
        try {
            if ( needsRebuild ) {
                buildMergedIndex( monitor );
            }
            mergedIndex = readMergedIndex();

            if ( needsRebuild ) {
                buildSearchIndex( mergedIndex, monitor );
            }
            searchIndex = readSearchIndex();
        } catch ( IOException e ) {
            Files.closeQuietly( mergedIndex );
            Files.closeQuietly( searchIndex );
            throw e;
        }

        // save model
        manager.save();

        LOG.info( "Compiling: done" );

        setState( EngineState.COMPILED );
    }

    public void addEngineListener( IEngineListener listener ) {
        listeners.add( listener );
    }

    public Comparator<String> getTitleComparator() {
        return COMPARATOR_TITLE;
    }
    public Comparator<IArticle> getArticleComparator() {
        return COMPARATOR_ARTICLE;
    }

    public IFinder getFinder() {
        return finder;
    }
    public IExporter getExporter() {
        return EXPORTER;
    }
    
    private void lockDown() throws IOException, LockedException {
        // ensure directory exists
        Files.ensureDirectoryExists( fileNamer.getWorkingDirectory() );

        // acquire lock
        lock = Locks.forFileDeleting( fileNamer.getLockFileName() );

        // rebuild all cashes if previous session crashed
        if ( lock.wasOverwritten() ) {
            LOG.info( "Lock file \"" + fileNamer.getLockFileName()
                    + "\" was overwritten. This shows that previous session was ended incorrectly. Deleting cache..." );

            // TODO uncomment for release
            //Files.deleteRecusrsively( fileNamer.getCacheDirectory() );
        }

        Files.ensureDirectoryExists( fileNamer.getCacheDirectory() );

        // clean temp
        EngineFiles.cleanTemp();
    }

    private void buildMergedIndex( ICompileMonitor monitor ) throws IOException {
        LOG.info( "Building merged index..." );

        TimeMeasurer measurer = new TimeMeasurer();

        monitor.buildingMergedIndex();

        MergedIndexMerger merger = new MergedIndexMerger( monitor );
        for ( IDictionaryIndex index : manager.getIndexes() ) {
            LOG.info( "Queuing " + index.getInfo().getCapacity() + " articles from: " + index.getInfo().getDataFileName() );
            merger.addReaderContents( index );
        }

        merger.build( new MergedIndexBuilder( fileNamer.getMergedIndexFileName() ) );

        LOG.info( "Building merged index: done within " + measurer );
    }
    private IMergedIndex readMergedIndex() throws IOException {
        LOG.info( "Reading merged index..." );
        TimeMeasurer measurer = new TimeMeasurer();
        IMergedIndex mergedIndex = new ChannelMergedIndex( fileNamer.getMergedIndexFileName(), manager.getIndexFileNameToReaderMap() );
        LOG.info( "Reading merged index: done within " + measurer );
        return mergedIndex;
    }
    private void buildSearchIndex( IMergedIndex mergedIndex, ICompileMonitor monitor ) throws IOException {
        LOG.info( "Building search index..." );
        TimeMeasurer measurer = new TimeMeasurer();

        monitor.buildingSearchIndex();
        ProgressScorer scorer = new ProgressScorer( monitor, mergedIndex.size() );

        ISearchIndexBuilder builder = new SearchIndexBuilder( fileNamer.getSearchIndexFileName() );
        for ( int i = 0; i < mergedIndex.size(); i++ ) {
            builder.add( mergedIndex.getArticleTitle( i ), i );
            scorer.increase();
        }
        builder.close();

        LOG.info( "Building search index: done within " + measurer );
    }

    private ISearchIndex readSearchIndex() throws IOException {
        LOG.info( "Reading search index..." );
        TimeMeasurer measurer = new TimeMeasurer();

        ISearchIndex searchIndex = new SearchIndex( fileNamer.getSearchIndexFileName(), COMPARATOR_TITLE );

        LOG.info( "Reading search index: done within " + measurer );
        return searchIndex;
    }
    private void rebuildDirtyInfos() throws IOException {
        if ( manager.getDirtyInfos().isEmpty() ) {
            return;
        }

        LOG.info( "Rebuilding needed indexes..." );

        TimeMeasurer measurer = new TimeMeasurer();
        for ( IInfo info : manager.getDirtyInfos() ) {
            LOG.info( "Rebuilding index: " + info );
            // open parser
            IParser parser = info.getReader().createParser( info, new NullMonitor() );

            // build index
            IInfo builtInfo;
            builtInfo = buildIndex( parser, info.getIndexFileName(), new NullMonitor() );

            // TODO 2 different cases:
            // TODO 1) when index just deleted - then reuse old
            // TODO 2) when data file changed - then replace with new info
            // replace with old info with new one
            manager.enqueueForAdd( builtInfo );
        }
        LOG.info( "Rebuilding needed indexes: done within " + measurer );
    }

    private void setState( EngineState state ) {
        this.state = state;
        this.state.fireEvents( listeners );
    }

    private static IInfo buildIndex( IParser parser, String indexFileName, IAddMonitor monitor ) throws IOException {
        String rawIndexFileName = null;
        try {
            rawIndexFileName = EngineFiles.createTemp( "index" );

            // dumping to temporary unsorted index
            IDictionaryIndexBuilder rawBuilder = new DictionaryIndexBuilder( rawIndexFileName );
            parser.buildIndex( rawBuilder );
            IInfo info = rawBuilder.getInfo();

            // create sorted index
            LOG.info( "Sorting \"" + rawIndexFileName + "\"" );
            monitor.sorting();

            DictionaryIndex index = new DictionaryIndex( info, rawIndexFileName );
            IDictionaryIndexBuilder builder = new DictionaryIndexBuilder( indexFileName );

            ProgressScorer scorer = new ProgressScorer( monitor, index.size() * 2 );
            DictionaryIndexSorter.sort(
                new ScoringDictionaryIndex( index, scorer ),
                    new ScoringDictionaryIndexBuilder( builder, scorer ),
                    info, COMPARATOR_TITLE );

            monitor.finish();

            return builder.getInfo();
        } finally {
            Files.deleteQuietly( rawIndexFileName );
        }
    }

    private void closeIndexes( List<IDictionaryIndex> indexes ) throws IOException {
        LOG.info( "Closing indexes..." );
        try {
            for ( IDictionaryIndex reader : indexes ) {
                Files.closeQuietly( reader );
            }
            lock.release();
        } finally {
            EngineFiles.cleanTemp();
        }
    }
    private void deleteNotNeededIndexes() {
        LOG.info( "Closing not needed indexes..." );
        Map<IInfo, IDictionaryIndex> forRemovalMap = manager.ejectForRemovalMap();
        for ( Map.Entry<IInfo, IDictionaryIndex> entry : forRemovalMap.entrySet() ) {
            Files.closeQuietly( entry.getValue() );
            Files.deleteQuietly( entry.getKey().getIndexFileName() );
        }
    }
}
