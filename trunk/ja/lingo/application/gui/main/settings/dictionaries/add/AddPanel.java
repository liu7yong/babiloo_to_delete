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

import info.clearthought.layout.TableLayout;
import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.centre.gui.actionbinder.config.NListenerGroup;
import ja.centre.gui.browser.Browser;
import ja.centre.gui.components.filechooser.FileChooser;
import ja.centre.gui.model.ILabelBuilder;
import ja.centre.gui.model.StaticListModel;
import ja.centre.gui.resources.Resources;
import ja.centre.gui.util.CardPanel;
import ja.centre.util.assertions.Arguments;
import ja.lingo.application.util.Buttons;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.Gaps;
import static ja.lingo.application.util.Gaps.GAP5;
import ja.lingo.application.util.messages.Messages;
import ja.lingo.application.util.misc.Threads;
import ja.lingo.application.util.progress.ITitledMonitor;
import ja.lingo.application.util.progress.ProgressUtil;
import ja.lingo.application.util.progress.engine.Monitors;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.reader.IDictionaryReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class AddPanel {
    private static final Log LOG = LogFactory.getLog( AddPanel.class );

    private Resources resources = Resources.forProperties( AddPanel.class );

    private JPanel gui;

    @NListenerGroup( {
            @NListener( type = ListSelectionListener.class, mappings = "valueChanged > onReaderSelected" ),
            @NListener( type = KeyListener.class,           mappings = "keyPressed > onReaderListKeyPressed" ),
            @NListener( type = MouseListener.class,         mappings = "mouseClicked > onReaderListClicked" )
    } )
    private JList readerList;

    @NListenerGroup( {
        @NListener( property = "field.document", type = DocumentListener.class, mappings = {
                "insertUpdate  > onFileFieldEdited",
                "removeUpdate  > onFileFieldEdited",
                "changedUpdate > onFileFieldEdited" } ),
        @NListener( property = "chooser", type = ActionListener.class, mappings = "actionPerformed > onFileChoosen")

    } )
    private FileChooser fileChooser;

    private JComboBox encodingComboBox;

    private CardPanel encodingCardPanel;

    @NListener( type = ActionListener.class, mappings = "actionPerformed > onContinue" )
    private JButton continueButton;

    @NListener( type = ActionListener.class, mappings = "actionPerformed > onHide" )
    private JButton closeButton;

    private JEditorPane editorPane;

    @NListener( type =  WindowListener.class, mappings = "windowClosing > onHide")
    private JDialog parentDialog;
    private IEngine engine;
    private JComboBox encodingAutoComboBox;

    public AddPanel( JDialog parentDialog, IEngine engine ) {
        Arguments.assertNotNull( "parentDialog", parentDialog );
        Arguments.assertNotNull( "engine", engine );

        this.parentDialog = parentDialog;
        this.engine = engine;

        fileChooser = new FileChooser();

        encodingComboBox = new JComboBox();
        encodingAutoComboBox = new JComboBox( new String[] { resources.text( "encoding_auto" ) } );
        encodingAutoComboBox.setEnabled( false );

        encodingCardPanel = new CardPanel();
        encodingCardPanel.add( encodingComboBox );
        encodingCardPanel.add( encodingAutoComboBox );

        readerList = Components.list( new StaticListModel<IDictionaryReader>(
                new ReaderLabelBuilder(), engine.getReaders() ) );
        readerList.setSelectedIndex( 0 );

        editorPane = Components.editorPane();
        editorPane.addHyperlinkListener( new HyperlinkListener() {
            public void hyperlinkUpdate( HyperlinkEvent e ) {
                if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                    Browser.openUrl( e.getURL().toExternalForm() );
                }
            }
        } );

        continueButton  = Buttons.continue1();
        continueButton.setDefaultCapable( true );

        closeButton = Buttons.cancel();

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 2, GAP5, GAP5 ) );
        buttonPanel.add( continueButton );
        buttonPanel.add( closeButton );

        JPanel listReaderPanel = new JPanel( new BorderLayout() );
        listReaderPanel.add( resources.label( "reader" ), BorderLayout.NORTH );
        listReaderPanel.add( new JScrollPane( readerList ), BorderLayout.CENTER );

        readerList.setPreferredSize( new Dimension( 50, 50 ) );
        listReaderPanel.setPreferredSize( new Dimension( 100, 100 ) );

        JPanel descriptionReaderPanel = new JPanel( new BorderLayout() );
        descriptionReaderPanel.add( resources.label( "readerDescription" ), BorderLayout.NORTH );
        descriptionReaderPanel.add( new JScrollPane( editorPane ), BorderLayout.CENTER );

        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                listReaderPanel, descriptionReaderPanel );
        splitPane.setContinuousLayout( true );
        splitPane.setDividerLocation( 130 );

        gui = new JPanel( new TableLayout( new double[][] { {
                TableLayout.PREFERRED,
                GAP5,
                TableLayout.FILL
            }, {
                TableLayout.FILL,           // 0: reader panel
                GAP5,
                TableLayout.PREFERRED,      // 2: file
                GAP5,
                TableLayout.PREFERRED,      // 4: encoding
                GAP5 * 2,
                TableLayout.PREFERRED       // 6: button panel
            } } ) );

        gui.add( splitPane,                                 "0, 0, 2, 0" );

        gui.add( resources.label( "file" ),      "0, 2" );
        gui.add( fileChooser.getGui(),                      "2, 2");

        gui.add( resources.label( "encoding" ),  "0, 4" );
        gui.add( encodingCardPanel.getGui(),                "2, 4" );

        gui.add( buttonPanel,                               "0, 6, 2, 6, right, center");

        Gaps.applyBorder7( gui );

        ActionBinder.bind( this );

        if ( encodingComboBox.getModel().getSize() > 0 ) {
            encodingComboBox.setSelectedIndex( 0 );
        }

        // filters
        for ( IDictionaryReader reader : engine.getReaders() ) {
            fileChooser.getChooser().addChoosableFileFilter( reader.getFileFilter() );
        }

        onReaderSelected();
        onFileFieldEdited();
   }

    public JComponent getGui() {
        return gui;
    }

    public void setReaders( List<IDictionaryReader> readers ) {
        ( (StaticListModel<IDictionaryReader>) readerList.getModel() ).setEntities( readers );

        readerList.setSelectedIndex( 0 );
        onReaderSelected();
    }

    public void onReaderSelected() {
        editorPane.setText( getSelectedReader().getDescription() );
        fileChooser.getChooser().setFileFilter( getSelectedReader().getFileFilter() );

        encodingCardPanel.show( getSelectedReader().getSupportedEncodings().isEmpty()
            ? encodingAutoComboBox : encodingComboBox );
        encodingComboBox.setModel( new DefaultComboBoxModel(
                getSelectedReader().getSupportedEncodings().toArray() ) );
    }

    public void onReaderListClicked( MouseEvent e ) {
        if ( !SwingUtilities.isLeftMouseButton( e ) ) {
            return;
        }

        if ( e.getClickCount() != 2 ) {
            return;
        }

        fileChooser.askForFile( parentDialog );
    }

    public IDictionaryReader getSelectedReader() {
        int selectedIndex = readerList.getSelectedIndex();
        return (IDictionaryReader) ( (StaticListModel) readerList.getModel() ).getEntity( selectedIndex );
    }

    public JButton getContinueButton() {
        return continueButton;
    }

    public void reset() {
        fileChooser.resetSelectedFile();
        //continueButton.setEnabled( false );
    }

    public FileChooser getFileChooser() {
        return fileChooser;
    }

    public void onReaderListKeyPressed( KeyEvent e ) {
        if ( e.getKeyCode() == KeyEvent.VK_ENTER && !fileChooser.hasSelectedFile() ) {
            fileChooser.askForFile();
            e.consume();
        }
    }
    public void onFileFieldEdited() {
        continueButton.setEnabled( fileChooser.hasSelectedFile() );
    }

    public void onFileChoosen() {
        IDictionaryReader readerToSelect = null;

        // get currently selected
        for ( IDictionaryReader reader : engine.getReaders() ) {
            if ( reader.getFileFilter() == fileChooser.getChooser().getFileFilter() ) {
                readerToSelect = reader;
                break;
            }
        }

        // if "all files" was selected
        if ( readerToSelect == null ) {
            for ( IDictionaryReader reader : engine.getReaders() ) {
                if ( reader.getFileFilter().accept( fileChooser.getChooser().getSelectedFile() ) ) {
                    readerToSelect = reader;
                    break;
                }
            }
            }

        // select
        if ( readerToSelect != null ) {
            int index = ((StaticListModel<IDictionaryReader>) readerList.getModel()).indexOf( readerToSelect );
            readerList.setSelectedIndex( index );
        }
    }

    public void onContinue() {
        final String fileName = fileChooser.getSelectedPath();
        // check existance again
        if ( !fileChooser.hasSelectedFile() ) {
            Messages.info( gui,
                    resources.text( "removedFile", fileName ),
                    resources.text( "couldNotAddTitle" )
            );
            onFileFieldEdited();
            return;
        }

        if ( engine.contains( fileName ) ) {
            Messages.info( gui,
                    resources.text( "duplicateDictionary", fileName ),
                    resources.text( "couldNotAddTitle" ) );
            return;
        }


        // go try parse
        final String fileEncoding = (String) encodingComboBox.getSelectedItem();
        final IDictionaryReader reader = getSelectedReader();

        final ITitledMonitor controller = ProgressUtil.start( parentDialog );

        Threads.startInBackground( new Runnable() {
            public void run() {
                try {
                    // TODO sync???? fileName, fileEncoding, reader
                    engine.addDictionary( fileName, fileEncoding, reader,
                            Monitors.add( controller ) );

                    // give feedback: finished
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            onHide();
                        }
                    } );
                } catch ( IOException t ) {
                    LOG.error( "IOException caught while parsing", t );
                        Messages.info( gui,
                                resources.text( "parsingFailed" ),
                                resources.text( "couldNotAddTitle" ) );
                } catch ( Throwable t ) {
                    LOG.error( "Throwable caught while parsing", t );
                    
                        Messages.internalError( parentDialog, t );
                } finally {
                    ProgressUtil.stop( parentDialog );
                }
            }
        } );
    }

    public void onHide() {
        parentDialog.setVisible( false );
    }

    public void requestFocus() {
        readerList.requestFocus(); // TODO has no effect
    }

    private static class ReaderLabelBuilder implements ILabelBuilder<IDictionaryReader> {
        public String getLabel( IDictionaryReader reader ) {
            return reader.getTitle();
        }
    }
}
