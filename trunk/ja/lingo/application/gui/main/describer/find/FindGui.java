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

package ja.lingo.application.gui.main.describer.find;

import info.clearthought.layout.TableLayout;
import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.centre.gui.actionbinder.config.NListenerGroup;
import ja.centre.gui.mediator.EmptyFieldMediator;
import ja.centre.gui.resources.Resources;
import ja.centre.gui.util.IGui;
import ja.centre.gui.util.SelectAllOnEscapeListener;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.util.Buttons;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.Gaps;
import ja.lingo.application.util.CheckBoxes;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;

public class FindGui implements IGui {
    private Resources resources = Resources.forProperties( getClass() );

    @NListener(type = ActionListener.class, mappings = "actionPerformed > close")
    private JButton closeButton;

    @NListenerGroup( {
        @NListener( type = KeyListener.class, mappings = "keyPressed > onKeyPressed" ),
        @NListener( property = "document", type = DocumentListener.class, mappings = {
            "insertUpdate  > onDocumentChanged",
            "removeUpdate  > onDocumentChanged",
            "changedUpdate > onDocumentChanged"
        } )
    } )
    private JTextField searchField;

    @NListener(type = ActionListener.class, mappings = "actionPerformed > next")
    private JButton nextButton;

    @NListener(type = ActionListener.class, mappings = "actionPerformed > previous")
    private JButton previousButton;

    private JCheckBox caseSensetiveCheckBox;

    private Model model;
    private JToolBar gui;

    private Color foundFore;
    private Color foundBack;

    private final Color notFoundFore = Color.WHITE;
    private final Color notFoundBack = new Color( 0xff6666);

    public FindGui( Model model ) {
        this.model = model;

        this.model.addApplicationModelListener( new ModelAdapter() {
            public void find_sendFeedback( boolean found ) {
                searchField.setForeground( found ? foundFore : notFoundFore );
                searchField.setBackground( found ? foundBack : notFoundBack );
            }
        } );

        closeButton = Buttons.closeCross();

        searchField = Components.textField();
        searchField.setColumns( 10 );

        // NOTE: will not work with dynamic LF change
        foundFore = searchField.getForeground();
        foundBack = searchField.getBackground();

        nextButton = Buttons.next();
        previousButton = Buttons.previous();

        caseSensetiveCheckBox = CheckBoxes.caseSensetive();

        gui = Components.toolBar();
        gui.setLayout( new TableLayout( new double[][] { {
                TableLayout.PREFERRED,  // 0: X
                TableLayout.PREFERRED,  // 1: Find:
                Gaps.GAP3,
                TableLayout.FILL,       // 3: text field
                Gaps.GAP3,
                TableLayout.PREFERRED,  // 5: next
                TableLayout.PREFERRED,  // 6: previous
                TableLayout.PREFERRED   // 7: case sensetive
        }, {
                TableLayout.PREFERRED
        } } ) );

        gui.add( closeButton, "0, 0" );
        gui.add( resources.label( "find" ), "1, 0" );
        gui.add( searchField, "3, 0, full, center" );
        gui.add( nextButton, "5, 0" );
        gui.add( previousButton, "6, 0" );
        gui.add( caseSensetiveCheckBox, "7, 0" );

        Gaps.compound( gui, new EtchedBorder(), Gaps.border2() );

        new EmptyFieldMediator( searchField, previousButton );
        new EmptyFieldMediator( searchField, nextButton );

        SelectAllOnEscapeListener.register( searchField );

        ActionBinder.bind( this );

        close();
    }

    public void show() {
        searchField.selectAll();
    }

    public JComponent getGui() {
        return gui;
    }

    public void close() {
        gui.setVisible( false );
    }

    public void next() {
        find( true );
    }

    public void previous() {
        find( false );
    }

    private void find( boolean forwardDirection ) {
        searchField.selectAll();
        model.find( getSearchText(), false, forwardDirection,
                caseSensetiveCheckBox.isSelected(),
                false/*wholeWordsOnlyCheckBox.isSelected()*/ );
        //searchField.requestFocus();
    }

    public void onKeyPressed( KeyEvent e ) {
        boolean shift = (e.getModifiers() & KeyEvent.SHIFT_MASK) > 0;
        boolean enter = e.getKeyCode() == KeyEvent.VK_ENTER;

        boolean down = e.getKeyCode() == KeyEvent.VK_DOWN;
        boolean up = e.getKeyCode() == KeyEvent.VK_UP;

        if ( down || (enter && !shift) ) {
            next();
        } else if ( up || (enter && shift) ) {
            previous();
        }
    }

    public void onDocumentChanged() {
        model.find( getSearchText(), true, true,
                caseSensetiveCheckBox.isSelected(), false );
    }
    private String getSearchText() {
        return searchField.getText().trim();
    }

    public void showAndRequestFocus() {
        gui.setVisible( true );
        searchField.requestFocus();
        searchField.selectAll();
    }
}
