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

import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.centre.gui.actionbinder.config.NListenerGroup;
import ja.centre.gui.resources.Resources;
import ja.centre.gui.util.CardPanel;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.ModelAdapter;
import ja.lingo.application.model.Preferences;
import ja.lingo.application.util.messages.Messages;
import ja.lingo.engine.EngineListenerAdapter;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.beans.IInfo;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;

public class DictionariesGui {
    private Resources resources = Resources.forProperties( getClass() );

    private JSplitPane gui;

    @NListenerGroup ( {
        @NListener ( property = "addButton",      type = ActionListener.class,        mappings = "actionPerformed > add" ),
        @NListener ( property = "removeButton",   type = ActionListener.class,        mappings = "actionPerformed > remove" ),
        @NListener ( property = "moveUpButton",   type = ActionListener.class,        mappings = "actionPerformed > moveUp" ),
        @NListener ( property = "moveDownButton", type = ActionListener.class,        mappings = "actionPerformed > moveDown" ),
        @NListener ( property = "list",           type = ListSelectionListener.class, mappings = "valueChanged > listValueChanged" ),
        @NListener( property = "list.model",     type = ListDataListener.class,      mappings = {
                "intervalAdded   > listContentsChanged",
                "intervalRemoved > listContentsChanged",
                "contentsChanged > listContentsChanged"
        } )
    } )
    private InfoListPanel infoListPanel;
    private EditPanel editPanel;
    private AddHintPanel addHelpPanel;
    private CardPanel rightPanel;

    private IEngine engine;
    private Model model;

    public DictionariesGui( Model model, IEngine engine ) {
        this.engine = engine;
        this.model = model;

        this.engine.addEngineListener( new EngineListenerAdapter() {
            public void dictionaryAdded( final IInfo info ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        dictionariesChanged();
                        infoListPanel.setSelectedInfo( info );
                    }
                } );

            }
            public void dictionaryDeleted( IInfo info ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        dictionariesChanged();
                    }
                } );
            }

            public void dictionariesSwaped( final int index0, final int index1 ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        DictionariesGui.this.dictionariesSwaped( index0, index1 );
                    }
                } );
            }
        } );

        this.model.addApplicationModelListener( new ModelAdapter() {
            public void initialize( Preferences preferences ) {
                dictionariesChanged();
            }
        } );

        infoListPanel = new InfoListPanel();
        editPanel = new EditPanel();

        addHelpPanel = new AddHintPanel();

        rightPanel = new CardPanel();
        rightPanel.add( editPanel );
        rightPanel.add( addHelpPanel );

        gui = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true,
                infoListPanel.getGui(), rightPanel.getGui() );
        gui.setDividerLocation( 120 );

        switchToAddHelpPanel();

        ActionBinder.bind( this );
    }

    public InfoListPanel getListPanel() {
        return infoListPanel;
    }

    public void add() {
        model.settings_add();
    }
    public void remove() {
        if ( askRemoveConfirmation() ) {
            engine.remove( infoListPanel.getSelectedInfo() );
        }
    }
    public void moveUp() {
        int selectedInfoIndex = infoListPanel.getSelectedInfoIndex();
        engine.swapDictionaries( selectedInfoIndex, selectedInfoIndex - 1 );
    }
    public void moveDown() {
        int selectedInfoIndex = infoListPanel.getSelectedInfoIndex();
        engine.swapDictionaries( selectedInfoIndex, selectedInfoIndex + 1 );
    }

    public void listValueChanged( ListSelectionEvent e ) {
        // strip "unselected" events
        if ( e.getValueIsAdjusting() ) {
            return;
        }

        if ( ((JList) e.getSource()).getSelectedIndex() == -1 ) {
            return;
        }

        IInfo info = infoListPanel.getSelectedInfo();
        editPanel.setDictionaryInfo( info );
    }
    public void listContentsChanged( ListDataEvent e ) {
        if ( ((ListModel) e.getSource()).getSize() == 0 ) {
            switchToAddHelpPanel();
        } else {
            switchToEditPanel();
        }
    }

    public JComponent getGui() {
        return gui;
    }

    private void switchToAddHelpPanel() {
        rightPanel.show( addHelpPanel );
    }
    private void switchToEditPanel() {
        rightPanel.show( editPanel );
    }

    private void dictionariesSwaped( int index0, int index1 ) {
        int selectedInfoIndex = infoListPanel.getSelectedInfoIndex();

        dictionariesChanged();

        if ( selectedInfoIndex == index0 ) {
            infoListPanel.setSelectedInfoIndex( index1 );
        } else if ( selectedInfoIndex == index1 ) {
            infoListPanel.setSelectedInfoIndex( index0 );
        }
    }
    private void dictionariesChanged() {
        java.util.List<IInfo> infos = engine.getInfos();

        int index = Math.min(
                infoListPanel.getSelectedInfoIndex(),
                infos.size() - 1 );

        infoListPanel.setInfos( infos );

        if ( index >= 0 ) {
            infoListPanel.setSelectedInfoIndex( index );
        }
    }

    private boolean askRemoveConfirmation() {
        return Messages.confirm( gui,
                resources.text( "remove_message" ),
                resources.text( "remove_title" ),
                resources.text( "remove_confirm" )
                );
    }
}
