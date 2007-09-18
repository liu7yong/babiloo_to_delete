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

package ja.lingo.application.gui.main.popout;

import ja.centre.gui.resources.Resources;
import ja.lingo.application.gui.main.describer.panels.ArticlePanel;
import ja.lingo.application.util.Components;
import ja.lingo.engine.IEngine;
import ja.lingo.engine.beans.IArticle;

import javax.swing.*;
import java.awt.*;

class PopOutWindow {
    private Resources resources = ja.centre.gui.resources.Resources.forProperties( getClass() );

    public PopOutWindow( IEngine engine, JComponent invoker, IArticle article, String highlight ) {
        ArticlePanel articlePanel = new ArticlePanel( engine, true );
        articlePanel.setArticle( article, highlight );

        JFrame frame = Components.frame();
        frame.setIconImage( resources.icon( "title" ).getImage() );
        frame.setTitle( articlePanel.getArticle().getTitle() );
        frame.setContentPane( articlePanel.getGui() );
        frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

        //Point location = new Point( 0, 0 );
        int prototypeWidth = 400;
        int prototypeHeight = 300;

        if ( invoker != null ) {
            prototypeWidth = invoker.getWidth();
            prototypeHeight = invoker.getHeight();
            //location = invoker.getLocationOnScreen();
        }

        // adjust size
        frame.pack();
        frame.setSize( frame.getWidth(), frame.getHeight() + 50 );

        if ( frame.getWidth() > prototypeWidth ) {
            frame.setSize( prototypeWidth, frame.getHeight() );
        }

        if ( frame.getHeight() > prototypeHeight ) {
            frame.setSize( frame.getWidth(), prototypeHeight );
        }

        // adjust location
        //int XY_SHIFT = 5;
        Point location = MouseInfo.getPointerInfo().getLocation();
        location.translate( -frame.getWidth() / 2, -10 ); // TODO rewrite magic -10 with "captionHeight / 2"
        frame.setLocation( location );

        // show
        frame.setVisible( true );
    }
}
