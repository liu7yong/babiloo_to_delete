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

package ja.lingo.readers.babiloo;

import ja.centre.util.resourcebundle.ResourceBundleFactory;
import ja.centre.util.assertions.States;
import ja.lingo.engine.observer.IEngineObserver;
import ja.lingo.engine.reader.IConverter;
import ja.lingo.engine.reader.IDictionaryReader;
import ja.lingo.engine.reader.IParser;
import ja.lingo.engine.reader.ReaderException;
import ja.lingo.engine.beans.IInfo;

import java.io.IOException;

/**
 * Copied from "ptksdict-1.1.6.zip", "\share\doc\Format-desc.txt":
 * <p/>
 * # $RCSfile: Format-desc.txt,v $
 * # $Author: swaj $
 * # $Revision: 1.9 $
 * #
 * # Copyright (c) Alexey Semenoff 2001-2006. All rights reserved.
 * # Distributed under GNU Public License.
 * #
 * <p/>
 * <p/>
 * Sdict file structure
 * ====================
 * <p/>
 * <p/>
 * Foreword
 * ---------
 * <p/>
 * File contains the following sections:
 * <p/>
 * 1. Header
 * 2. Dictionary information like title, copyright and version.
 * 3. Short index
 * 4. Full index
 * 5. Articles
 * <p/>
 * <p/>
 * <p/>
 * uint16_t, uint32_t are little endian;
 * utf-32le is also little endian.
 * <p/>
 * Articles, title, copyright, version are organized as a units.
 * Unit is universal storage container and looks like;
 * <p/>
 * struct {
 * uint32_t record_length;
 * utf8 record;
 * }
 * <p/>
 * <p/>
 * <p/>
 * Header
 * ------
 * <p/>
 * Structure, 43 (0x2b) byte length:
 * <p/>
 * +--------+------------+-----------+--------------------------------------------+
 * | Offset | Len, bytes | Content   |              Description                   |
 * +--------+------------+-----------+--------------------------------------------+
 * | 0x0    | 4          | uint8_t[] | Signature, 'sdct'                          |
 * | 0x4    | 3          | uint8_t[] | Input language                             |
 * | 0x7    | 3          | uint8_t[] | Output language                            |
 * | 0xa    | 1          | uint8_t   | Compression method            : (bytes 0-3)|
 * |        |            |           |             and index levels  : (bytes 4-7)|
 * | 0xb    | 4          | uint32_t  | Amount of words                            |
 * | 0xf    | 4          | uint32_t  | Length of short index                      |
 * | 0x13   | 4          | uint32_t  | Offset of 'title' unit                     |
 * | 0x17   | 4          | uint32_t  | Offset of 'copyright' unit                 |
 * | 0x1b   | 4          | uint32_t  | Offset of 'version' unit                   |
 * | 0x1f   | 4          | uint32_t  | Offset of short index                      |
 * | 0x23   | 4          | uint32_t  | Offset of full index                       |
 * | 0x27   | 4          | uint32_t  | Offset of articles                         |
 * +--------+------------+-----------+--------------------------------------------+
 * <p/>
 * 'short index', 'full index' and 'articles' are offsets from begin of the file.
 * Compression methods are '0' - none, '1' - gzip (Zlib), '2' -
 * bzip2. If some compression defined, the following sections expected
 * to be compressed: Short index, Articles.
 * <p/>
 * Index levels value means how many short index levels are used. By
 * default it contains 0x3X which means 3 levels.
 * <p/>
 * <p/>
 * <p/>
 * Note!  The only 3 levels are supported in all components, the other
 * ones are still experimental!
 * <p/>
 * <p/>
 * <p/>
 * Dictionary information
 * ----------------------
 * <p/>
 * There are 3 sections here: 'title', 'copyright' and 'version' stored
 * as 3 units.
 * Offsets 'title unit', 'copyright unit' and 'version unit' are from
 * begin of the file.
 * There is no strict order of storing dictionary information, default
 * order is 'title', 'copyright', 'version'.
 * <p/>
 * <p/>
 * <p/>
 * Short index
 * -----------
 * <p/>
 * Short index is the set of records:
 * <p/>
 * struct {
 * utf-32le[3] short_word;
 * uint32_t word_pointer;
 * }
 * <p/>
 * thus size of each element is 12 (0xc) bytes.
 * <p/>
 * Amount of records stored in header->'Length of short index'
 * <p/>
 * word_pointer points to the whole word from 'full index' and it is
 * relative against begin of 'full index' section, not begin of the
 * file.
 * <p/>
 * <p/>
 * <p/>
 * Full index
 * ----------
 * <p/>
 * Full index is set of the following records:
 * <p/>
 * struct {
 * uint16_t next_word;
 * uint16_t previous_word;
 * uint32_t article_pointer;
 * utf8[]   word;
 * }
 * <p/>
 * next_word and previous_word are relative against begin of the
 * record. article pointer points to article from 'articles' section
 * and it is relative against begin of 'articles' section, not begin
 * of the file.
 * <p/>
 * <p/>
 * <p/>
 * Articles
 * --------
 * <p/>
 * Articles are set of units, see unit description in foreword
 * chapter.
 *
 * Copied from "ptksdict-1.1.6.zip", "\share\dicts\README":
 * # $RCSfile: README,v $
 * # $Author: swaj $
 * # $Revision: 1.9 $
 * #
 * # Copyright (c) Alexey Semenoff 2001-2006. All rights reserved.
 * # Distributed under GNU Public License.
 * #
 *
 * HOW TO CREATE YOUR OWN DICTIONARY
 *
 * Look an examples in sample*.txt.
 *
 *
 * Every item looks like WORD___ARTICLE, no "\r", "\n" inside the article,
 * both WORD and ARTICLE are utf8-encoded text.
 *
 * Additionaly the following HTML-like tags can be used:
 * 	<br>                    - "\n"
 * 	<p>                     - "\n"
 * 	<b> ... </b>            - use bold font
 * 	<i> ... </i>            - italic font
 * 	<u> ... </u>            - underline
 * 	<l>, <li> ... , </l>    - list, like <ul><li> ...
 * 	<r>word</r>             - reference to other word, like <a href="word">word</a>
 * 	<t>trans</t>            - transcription	<t>trans</t>
 * 	<f>forms</f>            - word forms	<f>forms</f>
*/
public class BabilooReader implements IDictionaryReader {
    static final String NAME = "babiloo";
    private static final String TITLE = "Babiloo";
    private static final String DESCRIPTION = ResourceBundleFactory.asString( BabilooReader.class, ".html" );

    public IParser createParser( IInfo info, IEngineObserver observer ) throws ReaderException {
        return new BabilooParser( info.getDataFileName(), this, observer );
    }

    public IConverter createConvertor( IInfo info ) {
        try {
            return new BabilooConverter( info );
        } catch ( IOException e ) {
            throw States.shouldNeverReachHere( e ); // TODO raise correct exception
        }
    }

    public String getName() {
        return NAME;
    }

    public String getTitle() {
        return TITLE;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public boolean isEncodingRequired() {
        return false;
    }
}
