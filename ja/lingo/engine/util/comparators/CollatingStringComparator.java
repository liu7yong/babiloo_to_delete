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

package ja.lingo.engine.util.comparators;

import ja.centre.util.assertions.States;

import java.text.CollationKey;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Comparator;

public class CollatingStringComparator implements Comparator<String> {
    private Collator collator;

    static final int SEGMENT_1_START = ' ';
    static final int SEGMENT_1_END = 'A' - 1;
    static final int SEGMENT_2_START = 'Z' + 1;
    static final int SEGMENT_2_END = 127;

    public CollatingStringComparator() {
        try {
            collator = new RuleBasedCollator( DEFAULTRULES );
        } catch ( ParseException e ) {
            States.shouldNeverReachHere( e );
        }
    }

    // TODO optimize? sort(4276)
    public int compare( String source, String target ) {
        // TODO optimize? sort(1772)
        if ( isInBoostRange( source ) && isInBoostRange( target ) ) {
            return source.compareTo( target );
        }

        // TODO optimize? sort(2273)
        return getCollationKey( source ).compareTo( getCollationKey( target ) );

        //return collator.compare( source, target );
    }
    public CollationKey getCollationKey( String s ) {
        return collator.getCollationKey( s );
    }

    public int compareIgnoreAccents( String source, String target ) {
        int strength = collator.getStrength();

        collator.setStrength( Collator.PRIMARY );
        int result = compare( source, target );
        collator.setStrength( strength );

        return result;
    }


    static boolean isInBoostRange( String text ) {
        for ( int i = 0; i < text.length(); i++ ) {
            char c = text.charAt( i );

            if ( !((c >= SEGMENT_1_START && c <= SEGMENT_1_END)
                    || (c >= SEGMENT_2_START && c <= SEGMENT_2_END)) ) {
                return false;
            }
        }
        return true;
    }

    public boolean equals( Object o ) {
        if ( o instanceof CollatingStringComparator ) {
            CollatingStringComparator that = (CollatingStringComparator) o;
            return this.collator.equals( that.collator );
        } else {
            return false;
        }
    }

    public int hashCode() {
        return collator.hashCode();
    }

    final static String DEFAULTRULES = ""
            // no FRENCH accent order by default, add in French Delta
            // IGNORABLES (up to first < character)
            // COMPLETELY IGNORE format characters
            + "='\u200B'=\u200C=\u200D=\u200E=\u200F"
            // Control Characters
            + "=\u0000 =\u0001 =\u0002 =\u0003 =\u0004" //null, .. eot
            + "=\u0005 =\u0006 =\u0007 =\u0008 ='\u0009'" //enq, ...
            + "='\u000b' =\u000e" //vt,, so
            + "=\u000f ='\u0010' =\u0011 =\u0012 =\u0013" //si, dle, dc1, dc2, dc3
            + "=\u0014 =\u0015 =\u0016 =\u0017 =\u0018" //dc4, nak, syn, etb, can
            + "=\u0019 =\u001a =\u001b =\u001c =\u001d" //em, sub, esc, fs, gs
            + "=\u001e =\u001f =\u007f"                   //rs, us, del
            //....then the C1 Latin 1 reserved control codes
            + "=\u0080 =\u0081 =\u0082 =\u0083 =\u0084 =\u0085"
            + "=\u0086 =\u0087 =\u0088 =\u0089 =\u008a =\u008b"
            + "=\u008c =\u008d =\u008e =\u008f =\u0090 =\u0091"
            + "=\u0092 =\u0093 =\u0094 =\u0095 =\u0096 =\u0097"
            + "=\u0098 =\u0099 =\u009a =\u009b =\u009c =\u009d"
            + "=\u009e =\u009f"
            // IGNORE except for secondary, tertiary difference
            // Spaces
            + ";'\u2000';'\u2001';'\u2002';'\u2003';'\u2004'"  // spaces
            + ";'\u2005';'\u2006';'\u2007';'\u2008';'\u2009'"  // spaces
            + ";'\u200A';'\u3000';'\uFEFF'"                // spaces
            + ";'\r' ;'\t' ;'\n';'\f';'\u000b'"  // whitespace

            // Non-spacing accents

            + ";\u0301"          // non-spacing acute accent
            + ";\u0300"          // non-spacing grave accent
            + ";\u0306"          // non-spacing breve accent
            + ";\u0302"          // non-spacing circumflex accent
            + ";\u030c"          // non-spacing caron/hacek accent
            + ";\u030a"          // non-spacing ring above accent
            + ";\u030d"          // non-spacing vertical line above
            + ";\u0308"          // non-spacing diaeresis accent
            + ";\u030b"          // non-spacing double acute accent
            + ";\u0303"          // non-spacing tilde accent
            + ";\u0307"          // non-spacing dot above/overdot accent
            + ";\u0304"          // non-spacing macron accent
            + ";\u0337"          // non-spacing short slash overlay (overstruck diacritic)
            + ";\u0327"          // non-spacing cedilla accent
            + ";\u0328"          // non-spacing ogonek accent
            + ";\u0323"          // non-spacing dot-below/underdot accent
            + ";\u0332"          // non-spacing underscore/underline accent
            // with the rest of the general diacritical marks in binary order
            + ";\u0305"          // non-spacing overscore/overline
            + ";\u0309"          // non-spacing hook above
            + ";\u030e"          // non-spacing double vertical line above
            + ";\u030f"          // non-spacing double grave
            + ";\u0310"          // non-spacing chandrabindu
            + ";\u0311"          // non-spacing inverted breve
            + ";\u0312"          // non-spacing turned comma above/cedilla above
            + ";\u0313"          // non-spacing comma above
            + ";\u0314"          // non-spacing reversed comma above
            + ";\u0315"          // non-spacing comma above right
            + ";\u0316"          // non-spacing grave below
            + ";\u0317"          // non-spacing acute below
            + ";\u0318"          // non-spacing left tack below
            + ";\u0319"          // non-spacing tack below
            + ";\u031a"          // non-spacing left angle above
            + ";\u031b"          // non-spacing horn
            + ";\u031c"          // non-spacing left half ring below
            + ";\u031d"          // non-spacing up tack below
            + ";\u031e"          // non-spacing down tack below
            + ";\u031f"          // non-spacing plus sign below
            + ";\u0320"          // non-spacing minus sign below
            + ";\u0321"          // non-spacing palatalized hook below
            + ";\u0322"          // non-spacing retroflex hook below
            + ";\u0324"          // non-spacing double dot below
            + ";\u0325"          // non-spacing ring below
            + ";\u0326"          // non-spacing comma below
            + ";\u0329"          // non-spacing vertical line below
            + ";\u032a"          // non-spacing bridge below
            + ";\u032b"          // non-spacing inverted double arch below
            + ";\u032c"          // non-spacing hacek below
            + ";\u032d"          // non-spacing circumflex below
            + ";\u032e"          // non-spacing breve below
            + ";\u032f"          // non-spacing inverted breve below
            + ";\u0330"          // non-spacing tilde below
            + ";\u0331"          // non-spacing macron below
            + ";\u0333"          // non-spacing double underscore
            + ";\u0334"          // non-spacing tilde overlay
            + ";\u0335"          // non-spacing short bar overlay
            + ";\u0336"          // non-spacing long bar overlay
            + ";\u0338"          // non-spacing long slash overlay
            + ";\u0339"          // non-spacing right half ring below
            + ";\u033a"          // non-spacing inverted bridge below
            + ";\u033b"          // non-spacing square below
            + ";\u033c"          // non-spacing seagull below
            + ";\u033d"          // non-spacing x above
            + ";\u033e"          // non-spacing vertical tilde
            + ";\u033f"          // non-spacing double overscore
            //+ ";\u0340"          // non-spacing grave tone mark == \u0300
            //+ ";\u0341"          // non-spacing acute tone mark == \u0301
            + ";\u0342;"
            //+ "\u0343;" // == \u0313
            + "\u0344;\u0345;\u0360;\u0361"    // newer
            + ";\u0483;\u0484;\u0485;\u0486"    // Cyrillic accents

            + ";\u20D0;\u20D1;\u20D2"           // symbol accents
            + ";\u20D3;\u20D4;\u20D5"           // symbol accents
            + ";\u20D6;\u20D7;\u20D8"           // symbol accents
            + ";\u20D9;\u20DA;\u20DB"           // symbol accents
            + ";\u20DC;\u20DD;\u20DE"           // symbol accents
            + ";\u20DF;\u20E0;\u20E1"           // symbol accents


            // other punctuation

            + "<'\u0020';'\u00A0'"                  // spaces
            + "<'\u0021'"        // exclamation point
            + "<'\"'"            // quotation marks

            + "<'\u0023'"        // number sign
            + "<'\u0024'"        // dollar sign
            + "<'\u0025'"        // percent sign
            + "<'\u0026'"        // ampersand
            + "<'\u0027'"        // apostrophe
            + "<'\u0028'"        // left parenthesis
            + "<'\u0029'"        // right parenthesis
            + "<'\u002a'"        // asterisk
            + "<'\u002b'"        // plus sign
            + "<'\u002c'"        // comma (spacing)

            + ",'\u002D';\u00AD"                // dashes
            + ";\u2010;\u2011;\u2012"           // dashes
            + ";\u2013;\u2014;\u2015"           // dashes
            + ";\u2212"                         // dashes

            + "<'\u002e'"        // period/full stop
            + "<'\u002f'"        // slash

            // NUMERICS
            + "<0<1<2<3<4<5<6<7<8<9"
            + "<\u00bc<\u00bd<\u00be"   // 1/4,1/2,3/4 fractions

            + "<'\u003a'"        // colon
            + "<'\u003b'"        // semicolon

            + "<'\u003c'"        // less-than sign
            + "<'\u003d'"        // equal sign
            + "<'\u003e'"        // greater-than sign

            + "<\u00ab"          // left angle quotes
            + "<\u00bb"          // right angle quotes

            + "<'\u003f'"        // question mark
            + "<\u00bf"          // inverted question mark

            + "<'\u0040'"          // at sign

            + "<'\u005b'"        // left bracket
            + "<'\\'"            // backslash
            + "<'\u005d'"        // right bracket

            + "<'\u005e'"        // circumflex accent (spacing)

            + "<'\u005f'"        // underline/underscore (spacing)
            + "<\u00af"          // overline or macron (spacing)
            + "<\u00a1"          // inverted exclamation point

            + "<\u00b4"          // acute accent (spacing)
            + "<'\u0060'"        // grave accent (spacing)

            + "<\u00a8"          // diaresis/umlaut accent (spacing)
            + "<\u00b7"          // middle dot (spacing)
            + "<\u00b8"          // cedilla accent (spacing)

            + "<\u00a7"          // section symbol
            + "<\u00b6"          // paragraph symbol
            + "<\u00a9"          // copyright symbol
            + "<\u00ae"          // registered trademark symbol
            + "<\u00a4"          // international currency symbol
            + "<\u0e3f"          // baht sign
            + "<\u00a2"          // cent sign
            + "<\u20a1"          // colon sign
            + "<\u20a2"          // cruzeiro sign
            + "<\u20ab"          // dong sign
            + "<\u20ac"          // euro sign
            + "<\u20a3"          // franc sign
            + "<\u20a4"          // lira sign
            + "<\u20a5"          // mill sign
            + "<\u20a6"          // naira sign
            + "<\u20a7"          // peseta sign
            + "<\u00a3"          // pound-sterling sign
            + "<\u20a8"          // rupee sign
            + "<\u20aa"          // new shekel sign
            + "<\u20a9"          // won sign
            + "<\u00a5"          // yen sign

            + "<\u00b1"          // plus-or-minus sign
            + "<\u00f7"          // divide sign
            + "<\u00d7"          // multiply sign
            + "<\u00ac"          // end of line symbol/logical NOT symbol
            + "<\u00a6"          // broken vertical line
            + "<\u00b0"          // degree symbol
            + "<\u00b5"          // micro symbol

            // NON-IGNORABLES
            + "<a,A"
            + "<b,B"
            + "<c,C"
            + "<d,D"
            + "<\u00F0,\u00D0"                  // eth
            + "<e,E"
            + "<f,F"
            + "<g,G"
            + "<h,H"
            + "<i,I"
            + "<j,J"
            + "<k,K"
            + "<l,L"
            + "<m,M"
            + "<n,N"
            + "<o,O"
            + "<p,P"
            + "<q,Q"
            + "<r,R"
            + "<s, S & SS,\u00DF"             // s-zet
            + "<t,T"
            + "& TH, \u00DE &TH, \u00FE "     // thorn
            + "<u,U"
            + "<v,V"
            + "<w,W"
            + "<x,X"
            + "<y,Y"
            + "<z,Z"
            + "&AE,\u00C6"                    // ae & AE ligature
            + "&AE,\u00E6"
            + "&OE,\u0152"                    // oe & OE ligature
            + "&OE,\u0153"

            + "<'\u007b'"        // left brace
            + "<'\u007c'"        // vertical line/logical OR symbol
            + "<'\u007d'"        // right brace
            + "<'\u007e'"        // tilde accent (spacing)

            // cyrillic symbols
            + "<\u0482 & Z"
            + "<\u0430,\u0410<\u0431,\u0411<\u0432,\u0412"
            + "<\u0433,\u0413;\u0491,\u0490;\u0495,\u0494;\u0453,\u0403;\u0493,\u0492"
            + "<\u0434,\u0414<\u0452,\u0402<\u0435,\u0415;\u04bd,\u04bc;\u0451,\u0401;\u04bf,\u04be<\u0454,\u0404"
            + "<\u0436,\u0416;\u0497,\u0496;\u04c2,\u04c1<\u0437,\u0417;\u0499,\u0498<\u0455,\u0405<\u0438,\u0418"
            + "<\u0456,\u0406;\u04c0<\u0457,\u0407<\u0439,\u0419<\u0458,\u0408"
            + "<\u043a,\u041a;\u049f,\u049e;\u04c4,\u04c3;\u049d,\u049c;\u04a1,\u04a0;\u045c,\u040c;\u049b,\u049a"
            + "<\u043b,\u041b<\u0459,\u0409<\u043c,\u041c"
            + "<\u043d,\u041d;\u0463;\u04a3,\u04a2;\u04a5,\u04a4;\u04bb,\u04ba;\u04c8,\u04c7"
            + "<\u045a,\u040a<\u043e,\u041e;\u04a9,\u04a8<\u043f,\u041f;\u04a7,\u04a6<\u0440,\u0420"
            + "<\u0441,\u0421;\u04ab,\u04aa<\u0442,\u0422;\u04ad,\u04ac<\u045b,\u040b<\u0443,\u0423;\u04af,\u04ae"
            + "<\u045e,\u040e<\u04b1,\u04b0<\u0444,\u0424<\u0445,\u0425;\u04b3,\u04b2<\u0446,\u0426;\u04b5,\u04b4"
            + "<\u0447,\u0427;\u04b7;\u04b6;\u04b9,\u04b8;\u04cc,\u04cb<\u045f,\u040f<\u0448,\u0428<\u0449,\u0429"
            + "<\u044a,\u042a<\u044b,\u042b<\u044c,\u042c<\u044d,\u042d<\u044e,\u042e<\u044f,\u042f<\u0461,\u0460"
            + "<\u0462<\u0465,\u0464<\u0467,\u0466<\u0469,\u0468<\u046b,\u046a<\u046d,\u046c<\u046f,\u046e"
            + "<\u0471,\u0470<\u0473,\u0472<\u0475,\u0474;\u0477,\u0476<\u0479,\u0478<\u047b,\u047a<\u047d,\u047c"
            + "<\u047f,\u047e<\u0481,\u0480"
            ;
}
