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

package ja.lingo.application.gui.main.settings.dictionaries;

import info.clearthought.layout.TableLayout;
import ja.centre.gui.util.BaseGui;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.Gaps;
import ja.lingo.engine.beans.IInfo;

import javax.swing.*;

public class EditPanel extends BaseGui {
    private JPanel gui;

    private JTextField titleField;
    private JTextField articlesField;
    private JTextArea  descriptionArea;
    private JTextField fileField;
    private JTextField typeField;
    private JTextField encodingField;

    public EditPanel() {
        gui = new JPanel();
        Gaps.applyBorder5( gui );

        titleField          = Components.textFieldDisabled();
        articlesField       = Components.textFieldDisabled();

        descriptionArea = Components.textAreaDisabled();
        descriptionArea.setLineWrap( true );
        descriptionArea.setWrapStyleWord( true );
        descriptionArea.setRows( 10 );

        fileField           = Components.textFieldDisabled();
        typeField           = Components.textFieldDisabled();
        encodingField       = Components.textFieldDisabled();

        gui.setLayout( new TableLayout( new double[][] {
            {
                TableLayout.PREFERRED,          // 0: label
                Gaps.GAP5,
                TableLayout.FILL                // 2: field/area
            },
            {
                TableLayout.PREFERRED,          // 0: title
                Gaps.GAP5,
                TableLayout.PREFERRED,          // 2: capacity
                Gaps.GAP5,
                TableLayout.FILL,               // 4: description
                Gaps.GAP5,
                TableLayout.PREFERRED,          // 6: file
                Gaps.GAP5,
                TableLayout.PREFERRED,          // 8: type
                Gaps.GAP5,
                TableLayout.PREFERRED,          // 10: encoding
                Gaps.GAP5,
             }
        }) );
        gui.add( resources.label( "title" ),         "0,  0, left, top" );
        gui.add( resources.label( "articles" ),      "0,  2, left, top" );
        gui.add( resources.label( "description" ),   "0,  4, left, top" );
        gui.add( resources.label( "file" ),          "0,  6, left, top" );
        gui.add( resources.label( "type" ),          "0,  8, left, top" );
        gui.add( resources.label( "encoding" ),      "0, 10, left, top" );

        gui.add( titleField,                                    "2,  0, full, center" );
        gui.add( articlesField,                                 "2,  2, full, center" );
        gui.add( Components.scrollVertical( descriptionArea ),  "2,  4, full, full" );
        gui.add( fileField,                                     "2,  6, full, center" );
        gui.add( typeField,                                     "2,  8, full, center" );
        gui.add( encodingField,                                 "2, 10, full, center" );
    }

    public void setDictionaryInfo( IInfo info ) {
        titleField.setText(         info.getTitle() );
        articlesField.setText(      info.getCapacity() + "" );
        descriptionArea.setText(    info.getDescription() );
        fileField.setText(          info.getDataFileName() );
        typeField.setText(          info.getReader().getTitle() );
        encodingField.setText(      info.getDataFileEncoding() );
    }

    public JComponent getGui() {
        return gui;
    }
}