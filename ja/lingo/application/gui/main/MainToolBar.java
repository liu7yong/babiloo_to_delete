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

package ja.lingo.application.gui.main;

import ja.lingo.application.gui.actions.Actions;
import ja.lingo.application.util.Buttons;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.Gaps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainToolBar {
    private static final int BYTES_IN_MEGABYTE = 1024 * 1024;

    private JToolBar toolBar;
    private JComponent memoryBar;

    public MainToolBar( Actions actions ) {
        memoryBar = createMemoryBar();

        toolBar = Components.toolBar();
        toolBar.add( actions.getHistoryBackAction().button() );
        toolBar.add( actions.getHistoryForwardAction().button() );

        toolBar.addSeparator();

        toolBar.add( actions.getFindShowAction().button() );
        toolBar.add( actions.getPasteAndTranslateAction().button() );

        toolBar.addSeparator();

        toolBar.add( actions.getSettingsShowAction().button() );
        toolBar.add( actions.getHelpShowAction().button() );

        toolBar.add( memoryBar );
    }

    private JComponent createMemoryBar() {
        final JProgressBar progressBar = new JProgressBar();
        progressBar.setMinimum( 0 );
        progressBar.setStringPainted( true );

        Dimension size = new Dimension( 100, 24 );
        progressBar.setMinimumSize( size );
        progressBar.setPreferredSize( size );
        progressBar.setMaximumSize( size );

        Timer timer = new Timer( 500, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Runtime r = Runtime.getRuntime();
                int total = (int) (r.totalMemory());
                int used = total - (int) r.freeMemory();

                //total   = (int) ( r.maxMemory() );

                progressBar.setMaximum( total );
                progressBar.setValue( used );

                used /= BYTES_IN_MEGABYTE;
                total /= BYTES_IN_MEGABYTE;

                String text = used + "M of " + total + "M";

                progressBar.setString( text );
                progressBar.setToolTipText( text );
            }
        } );
        timer.start();

        JButton gcButton = Buttons.gc();
        gcButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.gc();
            }
        } );

        JToolBar toolBar = Components.toolBar();
        toolBar.add( Box.createHorizontalGlue() );
        toolBar.add( progressBar );
        toolBar.addSeparator( new Dimension( Gaps.GAP5, Gaps.GAP5 ) );
        toolBar.add( gcButton );
        toolBar.setBorder( BorderFactory.createEmptyBorder() );

        return toolBar;
    }

    public JComponent getGui() {
        return toolBar;
    }

    public void setMemoryBarVisibility( boolean visibility ) {
        memoryBar.setVisible( visibility );
    }
}
