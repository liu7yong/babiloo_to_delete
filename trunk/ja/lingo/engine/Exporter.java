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

import static ja.lingo.engine.ExporterHtmlTemplate.*;
import ja.lingo.engine.beans.IArticle;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.util.EngineFiles;
import ja.centre.util.assertions.Arguments;
import org.apache.commons.compress.tar.TarEntry;
import org.apache.commons.compress.tar.TarOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.util.zip.GZIPOutputStream;

class Exporter implements IExporter {
    private static final Log LOG = LogFactory.getLog( Exporter.class );

    private static final String EXPORT_ENCODING = "UTF-8";

    public String toHtml( IArticle article, String highlight ) {
        Arguments.assertNotNull( "article", article );
        Arguments.assertNotNull( "highlight", highlight );
        
        StringBuilder builder = new StringBuilder();
        builder.append( getHeader() );
        appendBodies( article, builder, highlight );
        builder.append( getFooter() );

        return builder.toString();
    }

    public void toFile( String fileName, IArticle article ) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream( fileName );
            fos.write( toHtmlStandalone( article, true ).getBytes( EXPORT_ENCODING ) );
        } finally {
            if ( fos != null ) {
                try {
                    fos.close();
                } catch ( IOException e ) {
                    LOG.error( "Exception caught when tried to close FileOutputStream", e );
                }
            }
        }
    }

    public void toFile( String fileName, IArticleList articleList, IMonitor monitor ) throws IOException {
        File listTempFile = EngineFiles.createTempFile( "html_export_list.html" );

        TarOutputStream tos = null;
        try {
            tos = new TarOutputStream( new GZIPOutputStream(
                    new BufferedOutputStream( new FileOutputStream( fileName ) ) ) );

            DecimalFormat format = createFormat( articleList.size() - 1 );

            OutputStream listOs = null;
            try {
                listOs = new BufferedOutputStream( new FileOutputStream( listTempFile ) );

                writeArticles( articleList, tos, listOs, format, monitor );
            } finally {
                if ( listOs != null ) {
                    try {
                        listOs.close();
                    } catch ( IOException e ) {
                        LOG.error( "Exception caught when tried to close list HTML FileOutputStream", e );
                    }
                }
            }

            putListFile( listTempFile, tos );

            putIndexFile( articleList, tos );

            putCssFile( tos );

            tos.finish();

            monitor.finish();

        } finally {
            if ( tos != null ) {
                try {
                    tos.close();
                } catch ( IOException e ) {
                    LOG.error( "Exception caught when tried to close TarOutputStream", e );
                }
            }

            listTempFile.delete();
        }
    }

    private String toHtmlStandalone( IArticle article, boolean embedCss ) {
        StringBuilder builder = new StringBuilder();
        builder.append( getStandaloneHeader( article.getTitle() ) );

        if ( embedCss ) {
            builder.append( getStandaloneCssEmbeddable() );
        } else {
            builder.append( getStandaloneCssReferenced() );
        }
        builder.append( getStandaloneHeader2() );
        appendBodies( article, builder );
        builder.append( getStandaloneFooter() );

        return builder.toString();
    }

    private void putCssFile( TarOutputStream tos ) throws IOException {
        byte[] cssRawBytes = getStandaloneCssRaw().getBytes( EXPORT_ENCODING );
        TarEntry entry = new TarEntry( "style.css" );
        entry.setSize( cssRawBytes.length );
        tos.putNextEntry( entry );
        tos.write( cssRawBytes );
        tos.closeEntry();
    }

    private void putIndexFile( IArticleList articleList, TarOutputStream tos ) throws IOException {
        byte[] indexFileBytes = getIndexFileContent( articleList ).getBytes( EXPORT_ENCODING );

        TarEntry entry = new TarEntry( "index.html" );
        entry.setSize( indexFileBytes.length );
        tos.putNextEntry( entry );
        tos.write( indexFileBytes );
        tos.closeEntry();
    }

    private void putListFile( File listTempFile, TarOutputStream tos ) throws IOException {
        // list: append as a TAR entry
        TarEntry entry = new TarEntry( "list.html" );
        entry.setSize( listTempFile.length() );
        tos.putNextEntry( entry );

        FileInputStream fis = null;
        try {
            fis = new FileInputStream( listTempFile );
            tos.copyEntryContents( fis );
        } finally {
            if ( fis != null ) {
                try {
                    fis.close();
                } catch ( IOException e ) {
                    LOG.error( "Exception caught when tried to close list HTML FileInputStream", e );
                }
            }
        }
        tos.closeEntry();
    }

    private void writeArticles( IArticleList articleList, TarOutputStream tos, OutputStream listOs, DecimalFormat format, IMonitor monitor ) throws IOException {
        // list: prefix
        listOs.write( getListFileHeader().getBytes( EXPORT_ENCODING ) );

        monitor.start( 0, articleList.size() );

        // traverse through articles
        for ( int i = 0; i < articleList.size(); i++ ) {
            IArticle article = articleList.get( i );
            String articleFileName = format.format( i ) + ".html";

            byte[] bytes = toHtmlStandalone( article, false ).getBytes( EXPORT_ENCODING );
            TarEntry entry = new TarEntry( articleFileName );
            entry.setSize( bytes.length );

            tos.putNextEntry( entry );
            tos.write( bytes );
            tos.closeEntry();

            // list: entry
            listOs.write( getListFileEntry( articleFileName,
                    articleList.get( i ).getTitle() ).getBytes( EXPORT_ENCODING ) );

            monitor.update( i );
        }

        // list: suffix
        listOs.write( getListFileFooter().getBytes( EXPORT_ENCODING ) );
    }

    private DecimalFormat createFormat( int maximumValue ) {
        StringBuilder builder = new StringBuilder();

        int maximumValueLength = ("" + maximumValue).length();
        for ( int i = 0; i < maximumValueLength; i++ ) {
            builder.append( "0" );
        }

        return new DecimalFormat( builder.toString() );
    }
}
