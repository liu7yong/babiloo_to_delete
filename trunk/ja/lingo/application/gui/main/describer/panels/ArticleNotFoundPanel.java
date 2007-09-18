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
import ja.lingo.application.model.Model;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.Gaps;
import ja.lingo.application.util.misc.Strings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ArticleNotFoundPanel extends BaseGui {
    private JScrollPane gui;

    private JLabel notFoundLabel;
    private JLabel mostCloseLabel;
    private Component mostCloseLabelStrut;
    private JLabel suggestLabel;
    private JLabel settingsLabel;

    private String articleTitle;
    private String closestArticleTitle;

    private JPanel actionsPanel;

    public ArticleNotFoundPanel( final Model model ) {
        notFoundLabel = new JLabel();

        mostCloseLabel = Components.labelRollover( "",
                resources.icon( "showTheClosest" ),
                new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        model.navigateAndTranslate( closestArticleTitle );
                    }
                } );

        suggestLabel = Components.labelRollover(
                resources.text( "suggest" ),
                resources.icon( "suggest" ),
                new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        model.suggest( articleTitle );
                    }
                } );

        settingsLabel = Components.labelRollover(
                resources.text( "settings" ),
                resources.icon( "settings" ),
                new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        model.settings_show();
                    }
                } );

        // actions panel
        mostCloseLabel.setAlignmentX( 0 );
        mostCloseLabelStrut = Box.createVerticalStrut( 10 );
        suggestLabel.setAlignmentX( 0 );

        actionsPanel = new JPanel();
        actionsPanel.setOpaque( false );
        actionsPanel.setLayout( new BoxLayout( actionsPanel, BoxLayout.PAGE_AXIS ) );
        actionsPanel.add( mostCloseLabel );
        actionsPanel.add( mostCloseLabelStrut );
        actionsPanel.add( suggestLabel );
        actionsPanel.add( settingsLabel );

        // content panel
        JPanel panel = new JPanel();
        panel.setBackground( Color.WHITE );
        panel.setLayout( new TableLayout( new double[][] {
                { TableLayout.FILL },
                {
                        TableLayout.PREFERRED, // 0: not found
                        30,
                        TableLayout.PREFERRED, // 2: possible actions
                        10,
                        TableLayout.PREFERRED   // 4: actions
                }
        } ) );
        panel.add( notFoundLabel, "0, 0" );
        panel.add( resources.label( "possibleActions" ), "0, 2" );
        panel.add( actionsPanel, "0, 4, left, center" );
        Gaps.applyBorder( panel, 20 );

        gui = new JScrollPane( panel );
    }

    public JComponent getGui() {
        return gui;
    }

    public void update( String articleTitle, String closestArticleTitle, boolean hasArticles ) {
        this.articleTitle = articleTitle;
        this.closestArticleTitle = closestArticleTitle;

        notFoundLabel.setText( resources.text( "notFound", Strings.escapeHtml( Strings.cutIfNecessary( articleTitle ) ) ) );
        mostCloseLabel.setText( resources.text( "showTheClosest", Strings.escapeHtml( closestArticleTitle ) ) );

        mostCloseLabel.setVisible( closestArticleTitle.length() > 0 );
        mostCloseLabelStrut.setVisible( closestArticleTitle.length() > 0 );

        suggestLabel.setVisible( hasArticles );
        settingsLabel.setVisible( !hasArticles );
    }
}
