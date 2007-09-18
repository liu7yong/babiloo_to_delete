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

package ja.lingo.engine.dictionaryindex;

import ja.centre.util.assertions.Arguments;

public class Token {
    private int start;
    private int length;

    public Token( int start, int length ) {
        if ( start < 0 ) {
            Arguments.doThrow( "Argument \"start\" must be greater than or equal to 0. Actual value is: " + start );
        }

        if ( length < 0 ) {
            Arguments.doThrow( "Argument \"length\" must be greater than or equal to 0. Actual value is: " + length );
        }

        this.start = start;
        this.length = length;
    }


    public int getStart() {
        return start;
    }
    public int getLength() {
        return length;
    }

    public boolean equals( Object obj ) {
        if ( obj == this ) {
            return true;
        }

        if ( !(obj instanceof Token) ) {
            return false;
        }

        Token token = (Token) obj;
        return start == token.start && length == token.length;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + start;
        result = 37 * result + length;
        return result;
    }

    public String toString() {
        return  "Token (start=" + getStart() + ", length=" + getLength() + ")";
    }
}
