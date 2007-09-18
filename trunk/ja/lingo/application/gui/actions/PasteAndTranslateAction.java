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

package ja.lingo.application.gui.actions;

import ja.lingo.application.model.Model;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

class PasteAndTranslateAction extends AAction {
    private static final Log LOG = LogFactory.getLog( PasteAndTranslateAction.class );

    private Model model;

    public PasteAndTranslateAction( Model model ) {
        this.model = model;

        Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener( new FlavorListener() {
            public void flavorsChanged( FlavorEvent e ) {
                setEnabled( ((Clipboard) e.getSource()).isDataFlavorAvailable( DataFlavor.stringFlavor ) );
            }
        } );
    }

    public void actionPerformed( ActionEvent e ) {
        try {
            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData( DataFlavor.stringFlavor );
            data = data.trim();
            model.navigateAndTranslate( data );
            model.main_showAtTop();
        } catch ( UnsupportedFlavorException e1 ) {
            LOG.error( "Could not paste from system clipboard", e1 );
        } catch ( IOException e1 ) {
            LOG.error( "Could not paste from system clipboard", e1 );
        }
    }
}
