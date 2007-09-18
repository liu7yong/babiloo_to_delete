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

package ja.lingo.engine.reader;

import ja.centre.util.assertions.Arguments;
import ja.lingo.readers.dsl.DslReader;
import ja.lingo.readers.mova.MovaReader;
import ja.lingo.readers.ptkdictmysql.PtkDictMySqlReader;
import ja.lingo.readers.sdictionary.SDictionaryReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Readers {
    private static List<IDictionaryReader> readers;

    public static final DslReader DSL = new DslReader();
    public static final MovaReader MOVA = new MovaReader();
    public static final PtkDictMySqlReader PTKDICT = new PtkDictMySqlReader();
    public static final SDictionaryReader SDICT = new SDictionaryReader();

    static {
        readers = new ArrayList<IDictionaryReader>();
        readers.add( DSL );
        readers.add( MOVA );
        readers.add( PTKDICT );
        readers.add( SDICT );

        // TODO register other reader factories here: load from plugins/JARs
    }

    private Readers() {
    }

    public static List<IDictionaryReader> getReaders() {
        return Collections.unmodifiableList( readers );
    }
    public static IDictionaryReader getReaderFactoryByName( String readerName ) {
        Arguments.assertNotEmpty( readerName, "readerName" );

        for ( IDictionaryReader reader : readers ) {
            if ( reader.getName().equals( readerName ) ) {
                return reader;
            }
        }
        throw Arguments.doThrow( "Reader with name \"" + readerName + "\" is nor registered" );
    }


}
