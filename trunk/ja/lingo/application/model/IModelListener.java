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

import ja.lingo.engine.IArticleList;
import ja.lingo.engine.beans.IArticle;

import java.util.EventListener;

public interface IModelListener extends EventListener {
    void initialize( Preferences preferences );

    void main_showAtTop();
    void main_showOrHide();

    void find_show();
    void find( String text, boolean fromStart, boolean forwardDirection, boolean caseSensetive, boolean wholeWordsOnly );
    void find_sendFeedback( boolean found );

    void export( IArticle article );
    void export( IArticleList articleList );

    void settings_show();
    void settings_dictionaries_deleteSelected();
    void settings_enabled( boolean enabled );
    void settings_add();

    void help_show();
    void help_showLicensingInfo();

    void dropzone_hideTemporary();

    void translate( IArticle article, String highlight );
    void translateNotFound( String articleTitle );

    void popOut( String articleTitle );
    void popOut( String articleTitle, String highlight );
    void popOut( IArticle article, String highlight );

    void suggest( String text );

    void navigate( String title );
    void navigateAndTranslate( String title );
    void requestFocusInNavigator();

    void dispose( Preferences preferences );

    void settingsUpdated( Preferences preferences );

    void bookmark( IArticle article );
}
