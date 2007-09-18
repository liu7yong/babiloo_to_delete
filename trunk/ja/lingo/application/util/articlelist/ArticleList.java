package ja.lingo.application.util.articlelist;

import ja.centre.gui.actionbinder.ActionBinder;
import ja.centre.gui.actionbinder.config.NListener;
import ja.centre.gui.util.IGui;
import ja.lingo.application.model.Model;
import ja.lingo.application.util.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ArticleList implements IGui {
    private ArticleListMenu menu;

    @NListener(type = MouseListener.class, mappings = {
        "mousePressed  > mousePressedOrReleased",
        "mouseReleased > mousePressedOrReleased"
    })
    private JList list;

    private JPanel gui;

    public ArticleList( Model model, boolean enableAccels, boolean bookmark ) {
        list = Components.list();
        list.setFocusable( false );

        menu = new ArticleListMenu( list, model, enableAccels, bookmark );


        gui = new JPanel( new BorderLayout() );
        gui.add( Components.scrollVertical( list ), BorderLayout.CENTER );

        ActionBinder.bind( this );
    }

    public void mousePressedOrReleased( MouseEvent e ) {
        if ( !SwingUtilities.isLeftMouseButton( e ) ) {
            return;
        }

        if ( list.getModel().getSize() == 0 ) {
            return;
        }
        menu.translateSelected();
    }

    public JList getList() {
        return list;
    }
    public ArticleListMenu getMenu() {
        return menu;
    }

    public JComponent getGui() {
        return gui;
    }

    public void setHighlight( String highlight ) {
        menu.setHighlight( highlight );
    }
}
