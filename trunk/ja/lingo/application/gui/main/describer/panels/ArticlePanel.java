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

package ja.lingo.application.gui.main.describer.panels;

import ja.centre.gui.util.IGui;
import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;
import ja.lingo.application.util.Components;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.beans.IArticle;

import javax.swing.*;

public class ArticlePanel implements IGui {
    private IEngine engine;

    private JScrollPane editorPaneScrollPane;
    private JEditorPane editorPane;

    private IArticle article;
    private String highlight;

    public ArticlePanel( IEngine engine, boolean attachDefaultMenu ) {
        Arguments.assertNotNull( "engine", engine );

        this.engine = engine;

        editorPane = Components.editorPane();

        if ( attachDefaultMenu ) {
            Components.bindMenu( editorPane );
        }

        editorPaneScrollPane = new JScrollPane( editorPane );
        editorPaneScrollPane.setFocusable( false );
        editorPaneScrollPane.setBorder( null );
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public JComponent getGui() {
        return editorPaneScrollPane;
    }

    public boolean hasArticle() {
        return article != null;
    }

    public IArticle getArticle() {
        States.assertNotNull( article, "No article was set before" );
        return article;
    }

    public void setArticle( IArticle article, String highlight ) {
        this.article = article;
        this.highlight = highlight;

        editorPane.setText( engine.getExporter().toHtml( article, getHighlight() ) );
    }

    public String getHighlight() {
        return highlight != null ? highlight : article.getTitle();
    }
}
