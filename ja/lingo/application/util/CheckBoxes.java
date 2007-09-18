package ja.lingo.application.util;

import ja.centre.gui.resources.Resources;

import javax.swing.*;

public class CheckBoxes {
    private static Resources resources = Resources.forProperties( CheckBoxes.class );

    private CheckBoxes() {
    }
    public static JCheckBox caseSensetive() {
        return checkBox( "caseSenstive" );
    }

    private static JCheckBox checkBox( String key ) {
        JCheckBox checkBox = new JCheckBox( resources.text( key ) );
        checkBox.setMnemonic( resources.stroke( key ).getKeyCode() );
        checkBox.setOpaque( false );
        checkBox.setFocusable( false );

        return checkBox;
    }
}
