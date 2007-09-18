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


import ja.centre.gui.util.BaseGui;
import ja.lingo.application.gui.actions.Actions;
import ja.lingo.application.gui.main.describer.DescriberGui;
import ja.lingo.application.gui.main.history.HistoryGui;
import ja.lingo.application.gui.main.navigator.NavigatorGui;
import ja.lingo.application.gui.main.search.SearchGui;
import ja.lingo.application.util.Gaps;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.theme.ShapedGradientTheme;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.tabbedpanel.titledtab.TitledTabProperties;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends BaseGui {
    private JPanel gui;

    private MainToolBar mainToolBar; // TODO convert to var

    private JSplitPane splitPane;

    public MainPanel( Actions actions, DescriberGui describerGui, NavigatorGui navigatorGui, HistoryGui historyGui, SearchGui searchGui ) {
        mainToolBar = new MainToolBar( actions );

        TabbedPanel tp = createTabbedPanel( navigatorGui, historyGui, searchGui );

        splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, tp, describerGui.getGui() );
        splitPane.setContinuousLayout( true );
        splitPane.setBorder( null );

        gui = new JPanel( new BorderLayout() );
        gui.add( mainToolBar.getGui(), BorderLayout.NORTH );
        gui.add( splitPane, BorderLayout.CENTER );

        // a hack to redraw contents TODO how to fix?
        tp.setSelectedTab( tp.getTabAt( 1 ) );
        tp.setSelectedTab( tp.getTabAt( 0 ) );

        Gaps.applyBorder2( gui );
    }
    private TabbedPanel createTabbedPanel( NavigatorGui navigatorGui, HistoryGui historyGui, SearchGui searchGui ) {
        ShapedGradientTheme theme = new ShapedGradientTheme();

        TabbedPanel tp = new TabbedPanel();
        tp.getProperties().setTabAreaOrientation( Direction.LEFT );
        tp.getProperties().addSuperObject( theme.getTabbedPanelProperties() );

        tp.addTab( createTab( "articles", navigatorGui.getGui(), theme.getTitledTabProperties() ) );
        tp.addTab( createTab( "history", historyGui.getGui(), theme.getTitledTabProperties() ) );
        tp.addTab( createTab( "search", searchGui.getGui(), theme.getTitledTabProperties() ) );

        return tp;
    }

    private TitledTab createTab( String key, JComponent content, TitledTabProperties properties ) {
        content.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );

        TitledTab tab = new TitledTab( resources.text( key ), resources.icon( key ), content, null );
        tab.getProperties().getNormalProperties().setDirection( Direction.UP );
        tab.getProperties().addSuperObject( properties );
        return tab;
    }

    public MainToolBar getMainToolBar() {
        return mainToolBar;
    }

    public JComponent getGui() {
        return gui;
    }

    public int getNavigatorDividerLocation() {
        return splitPane.getDividerLocation();
    }
    public void setNavigatorDividerLocation( int dividerLocation ) {
        splitPane.setDividerLocation( dividerLocation );
    }
}
