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

package ja.lingo.application.gui.actions.history;

import ja.lingo.application.gui.actions.AAction;
import ja.lingo.application.model.History;
import ja.lingo.application.model.HistoryAdapter;

import java.awt.event.ActionEvent;

public class HistoryBackAction extends AAction {
    private History history;

    public HistoryBackAction( History history ) {
        this.history = history;

        this.history.addHistoryListener( new HistoryAdapter() {
            public void selectionChanged( int index ) {
                updateEnablement();
            }
        } );
        updateEnablement();
    }

    protected void updateEnablement() {
        setEnabled( history.hasPrevious() );
    }

    public void actionPerformed( ActionEvent e ) {
        history.back();
    }
}
