package ja.centre.gui.util;

import ja.centre.gui.resources.Resources;

public abstract class BaseGui implements IGui {
    protected Resources resources = Resources.forProperties( getClass() );
}
