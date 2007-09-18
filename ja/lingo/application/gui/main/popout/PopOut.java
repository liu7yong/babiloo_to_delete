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

package ja.lingo.application.gui.main.popout;

import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.util.messages.Messages;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.beans.IArticle;

import javax.swing.*;

public class PopOut {
    public PopOut( Model model, final IEngine engine, final JComponent invoker ) {
        model.addApplicationModelListener( new ModelAdapter() {
            public void popOut( String articleTitle ) {
                IArticle article = engine.getFinder().find( articleTitle );
                if ( article == null ) {
                    Messages.infoArticleNotFound( invoker, articleTitle );
                } else {
                    popOut( article, article.getTitle() );
                }
            }

            public void popOut( String articleTitle, String highlight ) {
                IArticle article = engine.getFinder().find( articleTitle );
                if ( article == null ) {
                    Messages.infoArticleNotFound( invoker, articleTitle );
                } else {
                    popOut( article, highlight );
                }
            }

            public void popOut( IArticle article, String highlight ) {
                new PopOutWindow( engine, invoker, article, highlight );
            }
        } );
    }
}
