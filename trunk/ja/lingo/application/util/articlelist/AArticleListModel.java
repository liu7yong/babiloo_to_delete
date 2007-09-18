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

package ja.lingo.application.util.articlelist;

import ja.lingo.engine.beans.IArticle;
import ja.lingo.engine.IArticleList;
import ja.lingo.engine.AArticleList;
import ja.centre.gui.model.AListModel;
import ja.centre.gui.model.ILabelBuilder;
import ja.centre.util.assertions.Arguments;

import javax.swing.*;

abstract class AArticleListModel extends AListModel<IArticle> implements IArticleListMenuSupport {
    private JList list;

    public AArticleListModel( JList list ) {
        super( new ArticleLabelBuilder() );

        Arguments.assertNotNull( "list", list );
        this.list = list;
    }

    public IArticle getSelectedArticle() {
        return getEntity( list.getSelectedIndex() );
    }

    public IArticleList getArticleListForExport() {
        return new AArticleList() {
            public IArticle get( int index ) {
                return getEntity( index );
            }

            public int size() {
                return getSize();
            }
        };
    }

    private static class ArticleLabelBuilder implements ILabelBuilder<IArticle> {
        public String getLabel( IArticle article ) {
            return article.getTitle();
        }
    }

}
