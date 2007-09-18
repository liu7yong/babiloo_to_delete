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

package ja.lingo.application.gui.main.search;

import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.centre.gui.actionbinder.config.NListenerGroup;
import ja.centre.gui.concurrent.EdtWrapper;
import ja.centre.gui.util.CardPanel;
import ja.centre.gui.util.IGui;
import ja.centre.util.assertions.States;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.model.Preferences;
import ja.lingo.application.util.articlelist.MutableArticleListModel;
import ja.lingo.application.util.misc.Threads;
import ja.lingo.application.util.progress.ProgressBarMonitor;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.monitor.ISearchMonitor;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SearchGui implements IGui {
    private CardPanel cardPanel;
    
    @NListenerGroup({
        @NListener(property = "searchField", type = KeyListener.class, mappings = "keyPressed > onKeyPressed"),
        @NListener(property = "searchButton", type = ActionListener.class, mappings = "actionPerformed > onSearch"),
        // TODO use binding
        @NListener(property = "caseSensetiveCheckBox", type = ActionListener.class, mappings = "actionPerformed > onCaseChecked")
    })
    private SearchNewPanel newPanel;
    
    @NListenerGroup({
        @NListener(property = "stopButton", type = ActionListener.class, mappings = "actionPerformed > onStop"),
        @NListener(property = "newSearchButton", type = ActionListener.class, mappings = "actionPerformed > onSearchNew")
    })
    private SearchResultsPanel resultsPanel;
    
    private Model model;
    private IEngine engine;
    private Preferences preferences;
    
    public SearchGui( Model model, IEngine engine, Preferences preferences ) {
        this.model = model;
        this.engine = engine;
        this.preferences = preferences;
        
        newPanel = new SearchNewPanel();
        resultsPanel = new SearchResultsPanel( model );
        
        // TODO use binding
        newPanel.getCaseSensetiveCheckBox().setSelected( preferences.isSearchCaseSensetive() );
        
        cardPanel = new CardPanel();
        cardPanel.add( newPanel );
        cardPanel.add( resultsPanel );
        
        model.addApplicationModelListener( new ModelAdapter() {
            public void dispose( Preferences preferences ) {
                onStop();
                
                // TODO join thread instead wait
                try {
                    Thread.sleep( 100 );
                } catch ( InterruptedException e ) {
                    States.shouldNeverReachHere( e );
                }
            }
        } );
        
        ActionBinder.bind( this );
    }
    
    public JComponent getGui() {
        return cardPanel.getGui();
    }
    
    public void onKeyPressed( KeyEvent e ) {
        if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
            onSearch();
        }
    }
    
    public void onSearch() {
        cardPanel.show( resultsPanel );
        
        // TODO use binding
        preferences.setSearchCaseSensetive( newPanel.getCaseSensetiveCheckBox().isSelected() );
        
        // TODO show "Nothing found" on results insted pf empty list
        
        final String text = newPanel.getSearchField().getText();
        resultsPanel.switchToStarted( text );
        
        model.settings_enabled( false );
        
        final MutableArticleListModel builder = new MutableArticleListModel( resultsPanel.getList() );
        
        Threads.startInBackground( new Runnable() {
            public void run() {
                try {
                    engine.getFinder().search( text, builder, newPanel.isCaseSensetive(), new SearchMonitor() );
                    
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            resultsPanel.switchToFinished( text, builder.size() );
                        }
                    } );
                } finally {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            model.settings_enabled( true );
                        }
                    } );
                }
            }
        } );
    }
    
    public void onSearchNew() {
        cardPanel.show( newPanel );
        
        resultsPanel.switchToClean();
        
        newPanel.getSearchField().selectAll();
        newPanel.getSearchField().requestFocus();
    }
    
    public void onStop() {
        resultsPanel.switchToStopped(
                newPanel.getSearchField().getText(),
                resultsPanel.getList().getModel().getSize() );
    }
    
    public void onCaseChecked() {
        // TODO use binding
        preferences.setSearchCaseSensetive( newPanel.getCaseSensetiveCheckBox().isSelected() );
    }
    
    private class SearchMonitor implements ISearchMonitor {
        private IMonitor delegate;
        
        public SearchMonitor() {
            delegate = (IMonitor) EdtWrapper.nonWaiting(
                    new ProgressBarMonitor( resultsPanel.getProgressBar() ) );
        }
        public boolean isCanceled() {
            return !resultsPanel.isRunning();
        }
        
        public void start( int minimum, int maximum ) {
            delegate.start( minimum, maximum );
        }
        public void update( int value ) {
            delegate.update( value );
        }
        public void finish() {
            delegate.finish();
        }
    }
}
