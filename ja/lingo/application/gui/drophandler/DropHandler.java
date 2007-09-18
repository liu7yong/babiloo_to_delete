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

package ja.lingo.application.gui.drophandler;

import ja.centre.gui.resources.Resources;
import ja.lingo.application.model.Model;
import ja.lingo.application.util.messages.Messages;
import ja.lingo.engine.IEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;

public class DropHandler extends TransferHandler {
    private static final Log LOG = LogFactory.getLog( ja.lingo.application.gui.drophandler.DropHandler.class );

    private Resources resources = Resources.forProperties( getClass() );

    private IEngine engine;
    private Model model;

    public DropHandler( IEngine engine, Model model ) {
        this.engine = engine;
        this.model = model;
    }


    public boolean canImport( JComponent c, DataFlavor[] flavors ) {
        if ( engine.getFinder().isEmpty() ) {
            return false;
        }

        boolean canImport = Arrays.asList( flavors ).contains( DataFlavor.stringFlavor );

        if ( !canImport ) {
            LOG.warn( "Can't import from flavors " + Arrays.asList( flavors ) );
        }

        return canImport;
    }

    public boolean importData( JComponent c, Transferable t ) {
        if ( engine.getFinder().isEmpty() ) {
            return false;
        }

        if ( canImport( c, t.getTransferDataFlavors() ) ) {
            try {
                String value = (String) t.getTransferData( DataFlavor.stringFlavor );

                model.navigateAndTranslate( value.trim() );
                model.main_showAtTop();

                return true;
            } catch ( Throwable tt ) {
                LOG.warn( "Could not drop object", tt );
                Messages.info( c, resources.text( "couldNotDropObject" ) );
            }
        }
        return false;
    }
}
