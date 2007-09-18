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

package ja.lingo.application.gui.main.help;


import ja.centre.gui.browser.Browser;
import ja.centre.gui.resources.Resources;
import ja.centre.util.assertions.States;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.WasEverShownHelper;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;

public class HelpGui {
    private Resources resources = Resources.forProperties( HelpGui.class );

    private WasEverShownHelper<JDialog> toggler;
    private JEditorPane editorPane;

    public HelpGui( Model model, JFrame parentFrame ) throws HeadlessException {
        model.addApplicationModelListener( new ModelAdapter() {
            public void help_show() {
                try {
                    editorPane.setPage( resources.url( "help" ) );
                    HelpGui.this.show();
                } catch ( IOException e ) {
                    States.shouldNeverReachHere( e );
                }
            }
            public void help_showLicensingInfo() {
                try {
                    editorPane.setPage( resources.url( "licensing" ) );
                    HelpGui.this.show();
                } catch ( IOException e ) {
                    States.shouldNeverReachHere( e );
                }
            }
        } );

        editorPane = Components.editorPane();
        editorPane.addHyperlinkListener( new HyperlinkListener() {
            public void hyperlinkUpdate( HyperlinkEvent e ) {
                if ( HyperlinkEvent.EventType.ACTIVATED.equals( e.getEventType() ) ) {
                    try {
                        if ( "file".equals( e.getURL().getProtocol() ) ) {
                            ((JEditorPane) e.getSource()).setPage( e.getURL() );
                        } else {
                            Browser.openUrl( e.getURL().toExternalForm() );
                        }
                    } catch ( IOException e1 ) {
                        throw new RuntimeException( e1 );
                    }
                }
            }
        } );


        JScrollPane scroller = new JScrollPane( editorPane );
        scroller.setBorder( null );

        JDialog dialog = Components.dialog( parentFrame );
        dialog.setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
        dialog.setTitle( resources.text( "title" ) );
        dialog.setContentPane( scroller );

        toggler = new WasEverShownHelper<JDialog>( dialog, 390, 560 );
    }

    public void show() {
        toggler.show();
        editorPane.scrollRectToVisible( new Rectangle( 0, 0, 1, 1 ) );
    }
}
