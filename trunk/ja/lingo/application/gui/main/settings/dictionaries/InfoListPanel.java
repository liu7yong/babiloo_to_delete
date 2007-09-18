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

import ja.centre.gui.mediator.ListNoneOrFirstSelectedMediator;
import ja.centre.gui.mediator.ListNoneOrLastSelectedMediator;
import ja.centre.gui.mediator.ListNoneSelectedMediator;
import ja.centre.gui.model.ILabelBuilder;
import ja.centre.gui.model.StaticListModel;
import ja.centre.gui.util.IGui;
import ja.lingo.application.util.Buttons;
import ja.lingo.application.util.Components;
import ja.lingo.engine.beans.IInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InfoListPanel implements IGui {
    private JPanel gui;
    private JList list;

    private JButton addButton;
    private JButton removeButton;
    private JButton moveUpButton;
    private JButton moveDownButton;

    public InfoListPanel() {
        list = Components.list( new StaticListModel<IInfo>( new DictionaryInfoLabelBuilder() ) );
        list.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        addButton    = Buttons.add();
        removeButton = Buttons.remove();
        moveUpButton = Buttons.moveUp();
        moveDownButton = Buttons.moveDown();

        new ListNoneSelectedMediator( list, removeButton );
        new ListNoneOrFirstSelectedMediator( list, moveUpButton );
        new ListNoneOrLastSelectedMediator( list, moveDownButton );

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable( false );
        toolBar.setRollover( true );
        toolBar.setBorder( BorderFactory.createEtchedBorder() );

        toolBar.add( addButton );
        toolBar.add( removeButton );
        toolBar.add( moveUpButton );
        toolBar.add( moveDownButton );

        JScrollPane listScrollPane = new JScrollPane( list );
        listScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        // layout
        gui = new JPanel();
        gui.setLayout( new BorderLayout() );
        gui.add( toolBar,                   BorderLayout.NORTH );
        gui.add( listScrollPane,   BorderLayout.CENTER );
    }

    public void setInfos( List<IInfo> dictionaryInfos ) {
        getListModel().setEntities( dictionaryInfos );

        // setSelected first if not empty
        if ( !dictionaryInfos.isEmpty() ) {
            list.setSelectedIndex( 0 );
        }
    }

    public int getSelectedInfoIndex() {
        return list.getSelectedIndex();
    }
    public void setSelectedInfoIndex( int index ) {
        list.setSelectedIndex( index );
    }

    public IInfo getSelectedInfo() {
        return (IInfo) getListModel().getEntity( list.getSelectedIndex() );
    }
    public void setSelectedInfo( IInfo info ) {
        list.setSelectedIndex( getListModel().getEntities().indexOf( info ) );
    }

    private StaticListModel<IInfo> getListModel() {
        return (StaticListModel<IInfo>) list.getModel();
    }
    
    public JComponent getGui() {
        return gui;
    }

    private static class DictionaryInfoLabelBuilder implements ILabelBuilder<IInfo> {
        public String getLabel( IInfo info ) {
            return info.getTitle();
        }
    }
}
