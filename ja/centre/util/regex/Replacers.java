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

package ja.centre.util.regex;

public class Replacers {
    private Replacers() {
    }

    public static String replaceAll( String body, IReplacer... replacers ) {
        for ( IReplacer replacer : replacers ) {
            body = replacer.replace( body );
        }
        return body;
    }

    public static IReplacer plain( String subject, String replaceTo ) {
        return new PlainReplacer( subject, replaceTo );
    }
    public static IReplacer plain( String subject, String replaceTo, String ifContains ) {
        return new PlainIfReplacer( subject, replaceTo, ifContains );
    }

    public static IReplacer regex( String pattern, String replaceTo ) {
        return new RegexReplacer( pattern, replaceTo, false );
    }
    public static IReplacer regex( String pattern, String replaceTo, String ifContains ) {
        return new RegexeIfReplacer( pattern, replaceTo, ifContains );
    }

    public static IReplacer regexFirst( String pattern, String replaceTo ) {
        return new RegexReplacer( pattern, replaceTo, true );
    }
}
