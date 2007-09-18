package ja.lingo.application.gui.main.search;

import info.clearthought.layout.TableLayout;
import ja.centre.gui.mediator.EmptyFieldMediator;
import ja.centre.gui.util.BaseGui;
import ja.lingo.application.util.Buttons;
import ja.lingo.application.util.Components;
import ja.lingo.application.util.CheckBoxes;

import javax.swing.*;

public class SearchNewPanel extends BaseGui {
    private JPanel gui;

    private JTextField searchField;

    private JButton newSearchButton;
    private JCheckBox caseSensetiveCheckBox;
    //private JCheckBox wholeWordsOnlyCheckBox;

    public SearchNewPanel() {
        searchField = Components.textField();
        newSearchButton = Buttons.search();

        caseSensetiveCheckBox = CheckBoxes.caseSensetive();
        //wholeWordsOnlyCheckBox = resources.checkbox( "wholeWordsOnly" );

        gui = new JPanel( new TableLayout( new double[][] {
                { TableLayout.FILL },
                {
                        TableLayout.PREFERRED,  // 0: Search for:
                        3,
                        TableLayout.PREFERRED,  // 2: text field
                        0,
                        TableLayout.PREFERRED,  // 4: case sensetive?
                        0,
                        0, //TableLayout.PREFERRED,  // 6: whole words only?
                        10,
                        TableLayout.PREFERRED   // 8: Search
                }
        }) );
        gui.add( resources.label( "searchFor" ), "0, 0" );

        gui.add( searchField, "0, 2" );

        gui.add( caseSensetiveCheckBox, "0, 4" );
        //gui.add( wholeWordsOnlyCheckBox, "0, 6" );

        gui.add( newSearchButton, "0,  8, right, center" );

        new EmptyFieldMediator( searchField, newSearchButton );
    }

    public JComponent getGui() {
        return gui;
    }

    public JTextField getSearchField() {
        return searchField;
    }
    public JButton getSearchButton() {
        return newSearchButton;
    }

    public boolean isCaseSensetive() {
        return caseSensetiveCheckBox.isSelected();
    }
    public JCheckBox getCaseSensetiveCheckBox() {
        return caseSensetiveCheckBox;
    }
}
