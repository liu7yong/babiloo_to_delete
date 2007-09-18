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

package ja.lingo.application.gui.main.settings.dictionaries.add;

import ja.centre.gui.resources.Resources;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.util.Components;
import ja.lingo.engine.IEngine;

import javax.swing.*;

public class AddGui {
    private Resources resources = Resources.forProperties( getClass() );

    private JDialog parentDialog;
    private JDialog dialog;

    private AddPanel addPanel;

    private Model model;

    public AddGui( Model model, IEngine engine, JDialog parentDialog ) {
        this.parentDialog = parentDialog;

        this.model = model;
        this.model.addApplicationModelListener( new ModelAdapter() {
            public void settings_add() {
                showWithDefaults();
            }
        } );


        dialog = Components.dialogModal( parentDialog );

        addPanel = new AddPanel( dialog, engine );

        dialog.setTitle( resources.text( "title" ) );
        dialog.getRootPane().setDefaultButton( addPanel.getContinueButton() );
        dialog.setContentPane( addPanel.getGui() );
    }

    public void showWithDefaults() {
        addPanel.reset();

        dialog.setSize( 520, 400 );
        dialog.setLocationRelativeTo( parentDialog );
        dialog.setVisible( true );

        addPanel.requestFocus();
    }
}
