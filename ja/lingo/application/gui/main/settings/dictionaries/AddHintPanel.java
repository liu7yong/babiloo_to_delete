package ja.lingo.application.gui.main.settings.dictionaries;

import ja.centre.gui.util.BaseGui;

import javax.swing.*;

public class AddHintPanel extends BaseGui {
    private JLabel gui;

    public AddHintPanel() {
        gui = new JLabel( resources.text( "addHelp", resources.url( "addHelp_0" ) ) );
        gui.setHorizontalAlignment( SwingConstants.CENTER );
    }

    public JComponent getGui() {
        return gui;
    }
}
