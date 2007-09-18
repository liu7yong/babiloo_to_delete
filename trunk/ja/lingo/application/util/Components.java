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

package ja.lingo.application.util;

import com.xduke.xswing.DataTipManager;
import ja.centre.gui.components.enhancedwindow.EnhancedDialog;
import ja.centre.gui.components.enhancedwindow.EnhancedFrame;
import ja.centre.gui.components.highlightlist.HighlightList;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Components {
    private Components() {
    }

    public static JFrame frame() {
        return new EnhancedFrame();
    }
    public static JDialog dialog( JDialog owner ) {
        return new EnhancedDialog( owner );
    }
    public static JDialog dialogModal( JDialog owner ) {
        return new EnhancedDialog( owner, true );
    }

    public static JDialog dialog( JFrame owner ) {
        return new EnhancedDialog( owner );
    }
    public static JDialog dialogModal( JFrame owner ) {
        return new EnhancedDialog( owner, true );
    }

    public static JList list( ListModel model ) {
        HighlightList list = new HighlightList( model );
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        DataTipManager.get().register( list );

        return list;
    }
    public static JList list() {
        return list( new DefaultListModel() );
    }

    public static JTextField textField() {
        return bindMenu( createTextField() );
    }
    public static JTextField textFieldDisabled() {
        return bindMenu( disable( createTextField() ) );
    }

    public static JTextArea textArea() {
        return bindMenu( createTextArea() );
    }
    public static JTextArea textAreaDisabled() {
        return bindMenu( disable( createTextArea() ) );
    }

    public static JToolBar toolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable( false );
        toolBar.setRollover( true );
        return toolBar;
    }

    public static JPopupMenu popupMenu() {
        // NOTE: hack against "first item selected" 
        JMenuItem item = new JMenuItem();
        item.setVisible( false );

        JPopupMenu menu = new JPopupMenu();
        menu.add( item );
        return menu;
    }

    public static <T extends JTextComponent> T bindMenu( T component ) {
        new CutCopyPasteMenu( component );
        return component;
    }

    private static <T extends JTextComponent> T disable( T component ) {
        component.setEditable( false );
        component.setOpaque( false );
        component.setBorder( BorderFactory.createEmptyBorder() );
        component.setCursor( Cursor.getPredefinedCursor( Cursor.TEXT_CURSOR ) );
        return component;
    }

    public static JEditorPane editorPane() {
        JEditorPane editorPane = createEditorPane();
        editorPane.setContentType( "text/html" );
        editorPane.setEditable( false );
        editorPane.setBorder( BorderFactory.createEmptyBorder() );
        editorPane.setCursor( Cursor.getPredefinedCursor( Cursor.TEXT_CURSOR ) );
        return editorPane;
    }

    // NOTE: hacks to set the caret in the beginning after setText() was called
    private static JEditorPane createEditorPane() {
        return new JEditorPane() {
            public void setText( String t ) {
                super.setText( t );
                setCaretPosition( 0 );
            }
        };
    }
    private static JTextField createTextField() {
        return new JTextField() {
            public void setText( String t ) {
                super.setText( t );
                setCaretPosition( 0 );
            }
        };
    }
    private static JTextArea createTextArea() {
        return attachClipboardActions( new JTextArea() {
            public void setText( String t ) {
                super.setText( t );
                setCaretPosition( 0 );
            }
        } );
    }
    private static <T extends JTextComponent> T attachClipboardActions( T t ) {
        //// ctrl ins
        //t.getKeymap().addActionForKeyStroke(
        //        KeyStroke.getKeyStroke( KeyEvent.VK_INSERT, KeyEvent.CTRL_MASK ),
        //        new DefaultEditorKit.CopyAction() );
        return t;
    }

    public static JLabel labelRollover( String text, ImageIcon icon, final ActionListener listener ) {
        JLabel label = new JLabel();
        label.setIcon( icon );
        label.setBorder( BorderFactory.createEmptyBorder() );
        label.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        label.setOpaque( false );
        label.setText( text );
        label.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( !SwingUtilities.isLeftMouseButton( e ) ) {
                    return;
                }
                listener.actionPerformed( new ActionEvent( e.getSource(), e.getID(), "" ) );
            }
        } );
        return label;
    }
    public static JScrollPane scrollVertical( JComponent content ) {
        return new JScrollPane( content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
    }
}
