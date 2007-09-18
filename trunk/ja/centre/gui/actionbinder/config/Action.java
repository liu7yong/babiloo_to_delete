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

package ja.centre.gui.actionbinder.config;

import ja.centre.util.assertions.Arguments;
import ja.centre.util.assertions.States;

public class Action extends ActionContainer {
    private String method;

    public String getMethod() {
        return method;
    }

    void setMethod( String method ) {
        Arguments.assertNotNull( "method", method );

        this.method = method;
    }

    static Action logic() {
        return new LogicAction();
    }
    static Action condition() {
        return new Action();
    }

    private Action() {
    }

    private static class LogicAction extends Action {
        private static final String NESTED_NOT_SUPPORTED_MESSAGE = "Nested action is not supported";

        private LogicAction() {
        }

        public boolean hasAction() {
            return false;
        }

        public Action getAction() {
            throw States.doThrow( NESTED_NOT_SUPPORTED_MESSAGE );
        }
        public void setAction( Action action ) {
            throw States.doThrow( NESTED_NOT_SUPPORTED_MESSAGE );
        }
    }

}
