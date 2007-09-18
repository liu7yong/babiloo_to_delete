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

package ja.lingo.application.model;

import ja.centre.util.assertions.Arguments;
import ja.lingo.engine.IArticleList;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.beans.IArticle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private static final Log LOG = LogFactory.getLog( Model.class );

    private List<IModelListener> listeners = new ArrayList<IModelListener>();
    private Preferences preferences;
    private IEngine engine;

    public Model( IEngine engine, Preferences preferences ) {
        this.preferences = preferences;
        this.engine = engine;
    }

    public void addApplicationModelListener( IModelListener listener ) {
        Arguments.assertNotNull( "listener", listener );

        listeners.add( listener );
    }

    public void intialize() {
        for ( IModelListener listener : listeners ) {
            listener.initialize( preferences );
        }
    }

    public void main_showAtTop() {
        for ( IModelListener listener : listeners ) {
            listener.main_showAtTop();
        }
        requestFocusInNavigator(); // TODO hack?
    }
    public void main_showOrHide() {
        for ( IModelListener listener : listeners ) {
            listener.main_showOrHide();
        }
    }

    public void find_show() {
        for ( IModelListener listener : listeners ) {
            listener.find_show();
        }
    }
    public void find( String text, boolean fromStart, boolean forwardDirection, boolean caseSensetive, boolean wholeWordsOnly ) {
        for ( IModelListener listener : listeners ) {
            listener.find( text, fromStart, forwardDirection, caseSensetive, wholeWordsOnly);
        }
    }

    public void export( IArticle article ) {
        for ( IModelListener listener : listeners ) {
            listener.export( article );
        }
    }

    public void settings_show() {
        for ( IModelListener listener : listeners ) {
            listener.settings_show();
        }
    }
    public void settings_dictionaries_deleteSelected() {
        for ( IModelListener listener : listeners ) {
            listener.settings_dictionaries_deleteSelected();
        }
    }
    public void settings_enabled( boolean enabled ) {
        for ( IModelListener listener : listeners ) {
            listener.settings_enabled( enabled );
        }
    }
    public void settings_add() {
        for ( IModelListener listener : listeners ) {
            listener.settings_add();
        }
    }

    public void help_show() {
        for ( IModelListener listener : listeners ) {
            listener.help_show();
        }
    }
    public void help_showLicensingInfo() {
        for ( IModelListener listener : listeners ) {
            listener.help_showLicensingInfo();
        }
    }

    public void dropzone_hideTemporary() {
        for ( IModelListener listener : listeners ) {
            listener.dropzone_hideTemporary();
        }
    }

    public void translate( IArticle article ) {
        translate( article, article.getTitle() );
    }
    public void translate( IArticle article, String highlight ) {
        for ( IModelListener listener : listeners ) {
            listener.translate( article, highlight );
        }
    }
    public void translateNotFound( String articleTitle ) {
        for ( IModelListener listener : listeners ) {
            listener.translateNotFound( articleTitle );
        }
    }

    public void popOut( String articleTitle ) {
        for ( IModelListener listener : listeners ) {
            listener.popOut( articleTitle );
        }
    }
    public void popOut( String articleTitle, String highlight ) {
        for ( IModelListener listener : listeners ) {
            listener.popOut( articleTitle, highlight );
        }
    }
    public void popOut( IArticle article ) {
        popOut( article, article.getTitle() );
    }
    public void popOut( IArticle article, String highlight ) {
        for ( IModelListener listener : listeners ) {
            listener.popOut( article, highlight );
        }
    }

    public void suggest( String text ) {
        for ( IModelListener listener : listeners ) {
            listener.suggest( text );
        }
    }

    public void navigate( String title ) {
        for ( IModelListener listener : listeners ) {
            listener.navigate( title );
        }
    }
    public void navigateAndTranslate( String title ) {
        for ( IModelListener listener : listeners ) {
            listener.navigateAndTranslate( title );
        }
    }
    public void requestFocusInNavigator() {
        for ( IModelListener listener : listeners ) {
            listener.requestFocusInNavigator();
        }
    }

    public void dispose() {
        for ( IModelListener listener : listeners ) {
            try {
                listener.dispose( preferences );
            } catch ( Exception e ) {
                LOG.warn( "Exception caught during disposal: listener \""
                        + listener.getClass().getName() + "\"", e );
            }
        }

        try {
            LOG.info( "Saving preferences..." );
            Preferences.save( preferences );

            LOG.info( "Closng dictionary engine..." );
            engine.close();

        } catch ( Exception e ) {
            LOG.error( "Exception caught duruing dispose", e );
        }

        LOG.info( "Disposal completed" );

        System.exit( 0 );
    }
    public void settingsUpdated() {
        for ( IModelListener listener : listeners ) {
            listener.settingsUpdated( preferences );
        }
    }

    public void export( IArticleList articleList ) {
        for ( IModelListener listener : listeners ) {
            listener.export( articleList );
        }
    }
    public void find_sendFeedback( boolean found ) {
        for ( IModelListener listener : listeners ) {
            listener.find_sendFeedback( found );
        }
    }
    public void bookmark( IArticle article ) {
        for ( IModelListener listener : listeners ) {
            listener.bookmark( article );
        }
    }
}
