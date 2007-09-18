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

package ja.centre.gui.concurrent;

import ja.centre.util.assertions.Arguments;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;

public class TaskSequence {
    private static final Log LOG = LogFactory.getLog( TaskSequence.class );

    private int priority;
    private IThrowableProcessor processor;

    private ArrayList<ITask> tasks = new ArrayList<ITask>();

    public TaskSequence( IThrowableProcessor processor ) {
        this( Thread.NORM_PRIORITY, processor );
    }

    public TaskSequence( int priority, IThrowableProcessor processor ) {
        Arguments.assertNotNull( "processor", processor );

        this.priority = priority;
        this.processor = processor;
    }

    public TaskSequence addTask( ITask task ) {
        tasks.add( task );
        return this;
    }

    public TaskSequence addGuiTask( ITask task ) {
        tasks.add( wrapGuiTask( task ) );
        return this;
    }

    public TaskSequence addGuiTaskParallel( ITask task ) {
        tasks.add( wrapGuiTaskParallel( task ) );
        return this;
    }

    public void run() {
        Thread thread = new Thread( new Runnable() {
            public void run() {
                runAndWaint();
            }
        } );
        thread.setPriority( priority );
        thread.start();
    }

    public void runAndWaint() {
        for ( Iterator iterator = tasks.iterator(); iterator.hasNext(); ) {
            ITask task = (ITask) iterator.next();
            LOG.info( "Running task \"" + task + "\"" );
            try {
                task.run();
            } catch ( final Throwable t ) {
                try {
                    wrapGuiTask( new ITask() {
                        public void run() {
                            processor.process( t );
                        }
                    } ).run();
                } catch ( Throwable tt ) {
                    throw new RuntimeException( "Throwable processor caused an exception", tt );
                }
            }
        }
    }

    private static ITask wrapGuiTask( final ITask task ) {
        return new ITask() {
            public void run() throws Exception {
                SwingUtilities.invokeAndWait( new Runnable() {
                        public void run() {
                            try {
                                task.run();
                            } catch( Exception e ) {
                                throw new RuntimeException( e );
                            }
                        }
                    } );
            }
            public String toString() {
                return task.toString();
            }
        };
    }

    private static ITask wrapGuiTaskParallel( final ITask task ) {
        return new ITask() {
            public void run() throws Exception {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        try {
                            task.run();
                        } catch( Exception e ) {
                            throw new RuntimeException( e );
                        }
                    }
                } );
            }
            public String toString() {
                return task.toString();
            }
        };
    }
}
