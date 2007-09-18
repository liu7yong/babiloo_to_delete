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

package ja.centre.gui.components.filechooser;

import ja.centre.util.io.Files;
import ja.lingo.application.util.Components;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileChooser {
    private JPanel gui;

    private JTextField field;
    private JButton button;
    private JFileChooser chooser;

    public FileChooser() {
        field = Components.textField();// TODO must be on LF level

        button = new JButton( " ... " );
        button.addActionListener( new ChooseFileListener() );

        // TODO make button small as posible - rewrite this hack?? (with knowledge of LF)
        if ( button.getBorder() instanceof CompoundBorder ) {
            button.setBorder( ((CompoundBorder) button.getBorder()).getOutsideBorder() );
        }

        chooser = new JFileChooser( new File( "." ) );

        gui = new JPanel();
        gui.setLayout( new BorderLayout( 5, 5 ) );
        gui.add( field, BorderLayout.CENTER );
        gui.add( button, BorderLayout.EAST );
    }

    public JPanel getGui() {
        return gui;
    }

    public JTextField getField() {
        return field;
    }

    public JButton getButton() {
        return button;
    }

    public JFileChooser getChooser() {
        return chooser;
    }

    public void resetSelectedFile() {
        field.setText( "" );
    }

    public boolean hasSelectedFile() {
        return Files.exists( field.getText() );
    }

    public String getSelectedPath() {
        return field.getText();
    }

    private void setSelectedFile( File selectedFile ) {
        field.setText( selectedFile.getPath() );
    }

    public void askForFile() {
        askForFile( button );
    }

    public void askForFile( Component parent ) {
        chooser.setSelectedFile( new File( getSelectedPath() ) );
        int result = chooser.showOpenDialog( parent );

        if ( result == JFileChooser.APPROVE_OPTION ) {
            setSelectedFile( chooser.getSelectedFile() );
        }
    }


    private class ChooseFileListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            askForFile( button );
        }
    }
}
