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

package ja.lingo.application.gui.main.history;

import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.lingo.application.model.History;
import ja.lingo.application.model.IHistoryListener;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.util.MenuItems;
import ja.lingo.application.util.articlelist.ArticleList;
import ja.lingo.application.util.articlelist.IArticleListMenuSupport;
import ja.lingo.engine.AArticleList;
import ja.lingo.engine.IArticleList;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.beans.IArticle;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class HistoryGui {
    @NListener( property = "list", type = ListSelectionListener.class, mappings = {
        "valueChanged > updateSelected"
    })
    private ArticleList articleList;

    private History history;
    private IEngine engine;

    @NListener( type = ActionListener.class, mappings = "actionPerformed > clearHistory" )
    private JMenuItem clearHistoryItem;

    public HistoryGui( Model model, IEngine engine, History history ) {
        this.engine = engine;
        this.history = history;

        model.addApplicationModelListener( new ModelAdapter() {
            public void bookmark( IArticle article ) {
                HistoryGui.this.history.bookmark( article.getTitle() );
            }
        } );

        HistoryListenerImpl historyModel = new HistoryListenerImpl();
        this.history.addHistoryListener( historyModel );

        clearHistoryItem = MenuItems.clearHistory();

        articleList = new ArticleList( model, false, false );
        articleList.getList().setModel( historyModel );

        articleList.getMenu().addSeparator();
        articleList.getMenu().add( clearHistoryItem );

        // TODO bind menu to finder, resolve non-anavaible articles (skip?)

        ActionBinder.bind( this );
    }

    public JComponent getGui() {
        return articleList.getGui();
    }

    public void updateSelected( ListSelectionEvent e ) {
        if ( e.getValueIsAdjusting() ) {
            return;
        }

        history.setSelected( articleList.getList().getSelectedIndex() );
    }

    public void clearHistory() {
        history.clear();
    }

    private class HistoryListenerImpl extends AbstractListModel implements IHistoryListener, IArticleListMenuSupport {
        private IArticle getEntity( int index ) {
            return engine.getFinder().find( history.getTitle( index ) );
        }

        public IArticle getSelectedArticle() {
            return getEntity( articleList.getList().getSelectedIndex() );
        }

        public IArticleList getArticleListForExport() {
            return new AArticleList() {
                private ArrayList<IArticle> aritcleList = calculateSortedUniqueArticles();

                public IArticle get( int index ) {
                    return aritcleList.get( index );
                }
                public int size() {
                    return aritcleList.size();
                }
            };
        }

        private ArrayList<IArticle> calculateSortedUniqueArticles() {
            SortedSet<IArticle> articles = new TreeSet<IArticle>( engine.getArticleComparator() );

            for ( int i = 0; i < getSize(); i++ ) {
                IArticle article = getEntity( i );

                if ( article != null ) {
                    articles.add( article );
                }
            }

            return new ArrayList<IArticle>( articles );
        }

        public int getSize() {
            return history.size();
        }

        public Object getElementAt( int index ) {
            return history.getTitle( index );
        }

        public void contentsChanged() {
            fireContentsChanged( this, 0, Integer.MAX_VALUE );
            articleList.getList().setSelectedIndex( history.getSelectedIndex() );
        }

        public void selectionChanged( int index ) {
            articleList.getList().setSelectedIndex( index );
            articleList.getList().ensureIndexIsVisible( index );

        }

        public void selectionChangedByUser( int index ) {
            articleList.getMenu().translateSelected();
        }
    }
}
