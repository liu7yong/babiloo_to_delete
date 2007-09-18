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

package ja.lingo.application.gui.main.suggest;

import ja.centre.gui.concurrent.ATask;
import ja.centre.gui.concurrent.EdtWrapper;
import ja.centre.gui.concurrent.IThrowableProcessor;
import ja.centre.gui.concurrent.TaskSequence;
import ja.centre.gui.resources.Resources;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.messages.Messages;
import ja.lingo.application.util.misc.Strings;
import ja.lingo.application.util.misc.Threads;
import ja.lingo.application.util.progress.ProgressBarMonitor;
import ja.lingo.application.util.progress.IMonitor;
import ja.lingo.engine.IEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class SuggestGui {
    private static Resources resources = ja.centre.gui.resources.Resources.forProperties( SuggestGui.class );

    private static final int MIN_SEARCH_LENGTH = 3;

    private static final int ITEMS_PER_PAGE = 20;
    private static final int ITEMS_MAX_COUNT = ITEMS_PER_PAGE * 10;


    private static final int INVOKER_XY_SHIFT = 5;

    private Model model;
    private IEngine engine;
    private JComponent invoker;

    private JFrame owner;
    private ActionListener translateListener;

    private JDialog dialog;
    private IMonitor monitor;


    public SuggestGui( final Model model, IEngine engine, JComponent invoker ) {
        this.model = model;
        this.engine = engine;
        this.invoker = invoker;

        model.addApplicationModelListener( new ModelAdapter() {
            public void suggest( String text ) {
                SuggestGui.this.suggest( text );
            }
        } );

        owner = (JFrame) SwingUtilities.getWindowAncestor( invoker );

        translateListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final String text = ((JMenuItem) e.getSource()).getText();
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        model.navigateAndTranslate( text );
                    }
                } );
            }
        };

        JProgressBar progressBar = createProgressBar();

        dialog = Components.dialogModal( owner );
        dialog.setContentPane( progressBar );
        dialog.setUndecorated( true );

        monitor = (IMonitor) EdtWrapper.nonWaiting( new ProgressBarMonitor( progressBar ) );
    }

    private void suggest( final String subject ) {
        if ( subject.length() < MIN_SEARCH_LENGTH ) {
            Messages.info( owner, resources.text( "minSearchLength", MIN_SEARCH_LENGTH ) );
            return;
        }

        final JPopupMenu menu = Components.popupMenu();
        final Map<Integer, List<String>>[] holder = new Map[1];// TODO ugly! not safe! consider better way!

        TaskSequence sequence = new TaskSequence( Threads.BACKGROUND_PRIORITY, new IThrowableProcessor() {
            public void process( Throwable t ) {
                dialog.setVisible( false );
                Messages.internalError( owner, t );
            }
        } ).addGuiTaskParallel( new ATask( "show progress" ) {
            public void run() {
                adjustLocationSizeAndShow();
            }
        } ).addTask( new ATask( "search fuzzily" ) {
            public void run() {
                synchronized ( holder ) {
                    holder[0] = engine.getFinder().suggest( subject, monitor );
                }
            }
        } ).addGuiTask( new ATask( "show results" ) {
            public void run() {
                int addedCount;
                synchronized ( holder ) {
                    addedCount = buildMenu( menu, holder[0] );
                }

                dialog.setVisible( false );
                model.requestFocusInNavigator();

                if ( addedCount == 0 ) {
                    Messages.info( owner, resources.text( "noMatches", Strings.cutIfNecessary( subject ) ) );
                } else {
                    menu.show( invoker, INVOKER_XY_SHIFT, INVOKER_XY_SHIFT );
                }
            }
        } );
        sequence.run();
    }

    private void adjustLocationSizeAndShow() {
        dialog.setSize( Math.min( 200, invoker.getWidth() - 15 ), 10 );
        dialog.setLocation(
                INVOKER_XY_SHIFT + (int) invoker.getLocationOnScreen().getX(),
                INVOKER_XY_SHIFT + (int) invoker.getLocationOnScreen().getY() );

        dialog.setVisible( true );
    }

    private JProgressBar createProgressBar() {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted( false );
        progressBar.setBorder( BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder( Color.GRAY, 1 ),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder( 1, 1, 1, 1 ),
                        progressBar.getBorder()
                )
        ) );
        return progressBar;
    }

    private int buildMenu( JPopupMenu menu, Map<Integer, List<String>> distanceToTitlesMap ) {
        IMenuBuilder builder = new PopupMenuBuilder( menu );

        int totalCount = calculateTotalCount( distanceToTitlesMap );
        int addedCount = 0;
        for ( int rating = 0; rating < distanceToTitlesMap.size(); rating++ ) {
            for ( String title : distanceToTitlesMap.get( rating ) ) {
                if ( addedCount == ITEMS_MAX_COUNT ) {
                    builder.add( createLeftItem( totalCount - addedCount ) );
                    return addedCount;
                }

                if ( addedCount % ITEMS_PER_PAGE == 0/* break every ITEMS_PER_PAGE item */
                        && addedCount != 0/* but skip the first item */
                        && totalCount - addedCount != 1/* don't break if 1 item left */ ) {
                    JMenu moreMenu = createMoreMenu( Math.min( ITEMS_PER_PAGE, totalCount - addedCount ) );
                    builder.add( moreMenu );
                    builder = new MenuBuilder( moreMenu );
                }

                builder.add( createMatchItem( rating, title, translateListener ) );
                addedCount++;
            }
        }
        return addedCount;
    }

    private int calculateTotalCount( Map<Integer, List<String>> distanceToTitlesMap ) {
        int totalSize = 0;
        for ( Map.Entry<Integer, List<String>> entry : distanceToTitlesMap.entrySet() ) {
            totalSize += entry.getValue().size();
        }
        return totalSize;
    }

    private JMenuItem createMatchItem( int rating, String title, ActionListener translateListener ) {
        JMenuItem item = new JMenuItem( title );
        item.setIcon( resources.icon( "match" + rating ) );
        item.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        item.addActionListener( translateListener );
        item.setActionCommand( "" );
        return item;
    }

    private JMenu createMoreMenu( int moreCount ) {
        JMenu menu = new JMenu( resources.text( "more", moreCount ) );
        menu.setIcon( resources.icon( "match3" ) );
        return menu;
    }

    private JMenuItem createLeftItem( int leftCount ) {
        JMenuItem item = new JMenuItem( resources.text( "more", leftCount ) );
        item.setIcon( resources.icon( "match3" ) );
        item.setEnabled( false );
        return item;
    }

    private static interface IMenuBuilder {
        void add( JMenuItem item );
    }

    private static class PopupMenuBuilder implements IMenuBuilder {
        private JPopupMenu menu;

        public PopupMenuBuilder( JPopupMenu menu ) {
            this.menu = menu;
        }

        public void add( JMenuItem item ) {
            menu.add( item );
        }
    }

    private static class MenuBuilder implements IMenuBuilder {
        private JMenu menu;

        public MenuBuilder( JMenu menu ) {
            this.menu = menu;
        }

        public void add( JMenuItem item ) {
            menu.add( item );
        }
    }
}
