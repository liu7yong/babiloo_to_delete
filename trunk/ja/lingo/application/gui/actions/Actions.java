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

import ja.lingo.application.gui.actions.history.HistoryBackAction;
import ja.lingo.application.gui.actions.history.HistoryForwardAction;
import ja.lingo.application.model.History;
import ja.lingo.application.model.Model;
import ja.lingo.application.model.Preferences;

public class Actions {
    private HistoryBackAction historyBackAction;
    private HistoryForwardAction historyForwardAction;

    private DropZoneHideAction dropZoneHideAction;

    private PasteAndTranslateAction pasteAndTranslateAction;
    private SettingsShowAction settingsShowAction;
    private FindShowAction findShowAction;
    private HelpShowAction helpShowAction;

    private VisitHomeAction visitHomeAction;
    private ShowLicenseInfoAction showLicenseInfoAction;

    private DownloadDictionariesAction downloadDictionariesAction;

    public Actions( Preferences prefences, Model model, History history ) {
        historyBackAction = new HistoryBackAction( history );
        historyForwardAction = new HistoryForwardAction( history );

        dropZoneHideAction = new DropZoneHideAction( prefences, model );

        pasteAndTranslateAction = new PasteAndTranslateAction( model );
        settingsShowAction = new SettingsShowAction( model );
        findShowAction = new FindShowAction( model );
        helpShowAction = new HelpShowAction( model );

        visitHomeAction = new VisitHomeAction();
        showLicenseInfoAction = new ShowLicenseInfoAction( model );

        downloadDictionariesAction = new DownloadDictionariesAction();
    }

    public IGuiAction getHistoryBackAction() {
        return historyBackAction;
    }
    public IGuiAction getHistoryForwardAction() {
        return historyForwardAction;
    }

    public IGuiAction getDropZoneHideAction() {
        return dropZoneHideAction;
    }

    public IGuiAction getPasteAndTranslateAction() {
        return pasteAndTranslateAction;
    }
    public IGuiAction getSettingsShowAction() {
        return settingsShowAction;
    }
    public IGuiAction getFindShowAction() {
        return findShowAction;
    }
    public IGuiAction getHelpShowAction() {
        return helpShowAction;
    }

    public IGuiAction getVisitHomeAction() {
        return visitHomeAction;
    }
    public IGuiAction getShowLicenseInfoAction() {
        return showLicenseInfoAction;
    }

    public IGuiAction getDownloadDictionariesAction() {
        return downloadDictionariesAction;
    }
}
