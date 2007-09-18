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

public abstract class ModelAdapter implements IModelListener {
    public void initialize( Preferences preferences ) {
    }

    public void main_showAtTop() {
    }
    public void main_showOrHide() {
    }

    public void find_show() {
    }
    public void find( String text, boolean fromStart, boolean forwardDirection, boolean caseSensetive, boolean wholeWordsOnly ) {
    }
    public void find_sendFeedback( boolean found ) {
    }

    public void export( IArticle article ) {
    }
    public void export( IArticleList articleList ) {
    }

    public void settings_show() {
    }
    public void settings_dictionaries_deleteSelected() {
    }
    public void settings_enabled( boolean enabled ) {
    }
    public void settings_add() {
    }

    public void help_show() {
    }
    public void help_showLicensingInfo() {
    }

    public void dropzone_hideTemporary() {
    }

    public void translate( IArticle article, String highlight ) {
    }
    public void translateNotFound( String articleTitle ) {
    }

    public void popOut( String articleTitle ) {
    }
    public void popOut( String articleTitle, String highlight ) {
    }
    public void popOut( IArticle article, String highlight ) {
    }

    public void suggest( String text ) {
    }

    public void navigate( String title ) {
    }
    public void navigateAndTranslate( String title ) {
    }
    public void requestFocusInNavigator() {
    }

    public void dispose( Preferences preferences ) {
    }

    public void settingsUpdated( Preferences preferences ) {
    }

    public void bookmark( IArticle article ) {
    }
}
