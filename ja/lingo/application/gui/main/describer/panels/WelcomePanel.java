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

package ja.lingo.application.gui.main.describer.panels;

import info.clearthought.layout.TableLayout;
import ja.centre.gui.util.BaseGui;
import ja.lingo.application.JaLingoInfo;
import ja.lingo.application.gui.actions.Actions;
import ja.lingo.application.gui.actions.IGuiAction;
import ja.lingo.application.util.Gaps;
import ja.lingo.application.util.starlabel.StarLabel;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends BaseGui {
    private JComponent gui;

    public WelcomePanel( Actions actions ) {
        gui = new JPanel();
        gui.setLayout( new BorderLayout() );
        gui.add( new StarLabel( resources.text( "title" ), false ), BorderLayout.NORTH );
        gui.add( createWelcomePanel( actions ), BorderLayout.CENTER );
        gui.setBackground( Color.WHITE );
        gui.setBorder( null );
    }

    private JPanel createWelcomePanel( Actions actions ) {
        JPanel taskPanel = new JPanel();
        taskPanel.setBackground( Color.WHITE );
        taskPanel.setLayout( new TableLayout( new double[][] {
                { TableLayout.PREFERRED },
                {
                        TableLayout.PREFERRED,  // 0 help
                        10,
                        TableLayout.PREFERRED,  // 2 settings
                        10,
                        TableLayout.PREFERRED,  // 4 visit home
                        30,
                        TableLayout.PREFERRED,  // 6 lcensing
                }
        } ) );
        taskPanel.add( createButtonLabelPanel( "help", actions.getHelpShowAction() ), "0, 0, left, center" );
        taskPanel.add( createButtonLabelPanel( "settings", actions.getSettingsShowAction() ), "0, 2, left, center" );
        taskPanel.add( actions.getVisitHomeAction().rollover(), "0, 4, left, center" );
        taskPanel.add( actions.getShowLicenseInfoAction().rollover(), "0, 6, left, center" );
        Gaps.applyBorder( taskPanel, 20 );

        JLabel versionLabel = new JLabel( resources.text( "version", JaLingoInfo.VERSION ), SwingConstants.RIGHT );
        versionLabel.setForeground( Color.GRAY );
        Gaps.applyBorder7( versionLabel );

        JScrollPane taskScroll = new JScrollPane( taskPanel );
        taskScroll.setBorder( null );

        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBackground( Color.WHITE );
        panel.add( taskScroll, BorderLayout.CENTER );
        panel.add( versionLabel, BorderLayout.SOUTH );
        return panel;
    }

    private JComponent createButtonLabelPanel( String key, IGuiAction action ) {
        JLabel label = action.rollover();
        label.setText( resources.text( key ) );
        return label;
    }

    public JComponent getGui() {
        return gui;
    }
}
