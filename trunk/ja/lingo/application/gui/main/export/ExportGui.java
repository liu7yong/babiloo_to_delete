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

package ja.lingo.application.gui.main.export;

import ja.centre.gui.resources.Resources;
import ja.centre.util.io.Files;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.util.messages.Messages;
import ja.lingo.application.util.misc.Threads;
import ja.lingo.application.util.progress.ITitledMonitor;
import ja.lingo.application.util.progress.ProgressUtil;
import ja.lingo.application.util.progress.engine.Monitors;
import ja.lingo.application.util.JaFilter;
import ja.lingo.engine.IArticleList;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.beans.IArticle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportGui {
    private static final Log LOG = LogFactory.getLog( ExportGui.class );
    private static final Resources RESOURCES = Resources.forProperties( ExportGui.class );

    public static final JaFilter HTML = new JaFilter(
            RESOURCES.text( "htmlFiles" ), ".html", ".htm" );

    public static final JaFilter HTML_TARGZ =  new JaFilter(
            RESOURCES.text( "tarGzFiles" ), ".tar.gz", ".tgz" );

    private IEngine engine;

    private JFrame parentFrame;
    private JFileChooser chooser;


    public ExportGui( IEngine engine, Model model, JFrame parentFrame ) {
        this.engine = engine;
        this.parentFrame = parentFrame;

        chooser = new JFileChooser( "." );

        model.addApplicationModelListener( new ModelAdapter() {
            public void export( IArticle article ) {
                saveAs( article );
            }

            public void export( IArticleList articleList ) {
                showAll( articleList );
            }
        } );
    }

    private void saveAs( IArticle article ) {
        String fileName = askFile( HTML,
                article.getTitle(),
                RESOURCES.text( "saveAs" ) );

        if ( fileName != null ) {
            try {
                engine.getExporter().toFile( fileName, article );
            } catch ( Throwable t ) {
                showExportError( t );
            }
        }
    }

    private void showAll( final IArticleList articleList ) {
        String suggestedFileName = articleList.size() + " articles on " + new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( new Date() );

        final String fileName = askFile( HTML_TARGZ,
                suggestedFileName,
                RESOURCES.text( "saveAll" ) );

        if ( fileName == null ) {
            return;
        }

        final ITitledMonitor controller = ProgressUtil.start( parentFrame );


        Threads.startInBackground( new Runnable() {
            public void run() {
                try {
                    engine.getExporter().toFile( fileName, articleList, Monitors.export( controller ) );
                } catch ( Throwable t ) {
                    showExportError( t );
                } finally {
                    ProgressUtil.stop( parentFrame );
                }
            }
        } );
    }


    private String askFile( JaFilter filter, String fileName, String dialogTitle ) {
        chooser.setDialogTitle( dialogTitle );
        chooser.resetChoosableFileFilters();
        chooser.setFileFilter( filter );
        chooser.setSelectedFile( new File( filter.appendExtensionIfNeeded(
                Files.removeUnacceptableSymbols( fileName ) ) ) );

        if ( chooser.showSaveDialog( parentFrame ) == JFileChooser.APPROVE_OPTION ) {
            return filter.appendExtensionIfNeeded( chooser.getSelectedFile().getAbsolutePath() );
        }
        return null;
    }

    private void showExportError( Throwable t ) {
        LOG.warn( "Exception occured during export to HTML", t );
        Messages.error( parentFrame, t.getMessage(), RESOURCES.text( "exportError" ) );
    }
}
