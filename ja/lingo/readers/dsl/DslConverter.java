package ja.lingo.readers.dsl;

import ja.centre.util.regex.Replacers;
import ja.centre.util.regex.IReplacer;
import ja.lingo.engine.beans.IInfo;
import ja.lingo.engine.reader.util.BaseConverter;

import java.util.HashMap;
import java.util.Map;

class DslConverter extends BaseConverter {
    private Map<Character, String> trans = new HashMap<Character, String>();

    private static final String TAG_TRANS = "[t]";
    private static final String TAG_TRANS_CLOSE = "[/t]";

    private static final IReplacer TAB_REPLACER = Replacers.regex( "\t", "" );

    private static final IReplacer[] replacers = {
            // first \r\n
            Replacers.regexFirst( "\r\n", "" ),

            // transcription
            Replacers.regex( "(\\\\\\[)?\\[t\\]", "<span class=\"trans\">[" ), // open
            Replacers.regex( "\\[/t\\](\\\\\\])?", "]</span>" ), // close

            // sound TODO insert link to media
            Replacers.regex( "\\[s\\].*\\[/s\\]", "" ),

            // comment
            Replacers.regex( "\\{\\{.*\\}\\}", "" ),

            // remove all misc tags
            Replacers.regex( "\\[/?(com|trn|p|ex|lang|!trs)\\]", "" ),
            Replacers.regex( "\\[lang\\s.*?\\]", "" ), // TODO merge with previous

            // biu
            Replacers.regex( "\\[(/?[biu])\\]", "<$1>" ),

            // ident
            Replacers.regex( "\\[m(\\d)\\]", "<div class=\"m$1\">" ), // open
            Replacers.regex( "\\[/m\\](\r\n|\r|\n)", "</div>" ), // close - nl
            Replacers.regex( "\\[/m\\]",     "</div>" ), // close

            // color
            Replacers.regex( "\\[c\\]", "<font color=\"green\">" ), // open
            Replacers.regex( "\\[c\\s(\\S+)]", "<font color=\"green\">" ), // other TODO convert $1 to HTML-3 colors
            Replacers.regex( "\\[\\*\\]", "<font color=\"gray\">" ), // gray
            Replacers.regex( "\\[/(c|\\*)\\]", "</font>" ), // close

            // ref: list item
            Replacers.regex( "-\\s\\[ref\\](.*?)\\[/ref\\]", "- <font color=\"navy\">$1</font><br>" ),

            // ref TODO merge into one
            Replacers.regex( "\\[ref\\]", "<font color=\"navy\">" ), // open
            Replacers.regex( "\\[ref\\s.*?\\]", "<font color=\"navy\">" ), // with attrs
            Replacers.regex( "\\[/ref\\]", "</font>" ), // close

            // all unutilized NLs -> to <br>
            Replacers.regex( "(\r\n|\r|\n)", "<br>" ),

            // unescape, must be the last replacement
            Replacers.regex( "\\\\([^\\\\])", "$1" ),
    };


    public DslConverter( IInfo info ) {
        super( info );

        initTransMap();
    }

    protected String convertTitle( String title ) {
        if ( title.contains( "\\ " ) ) {
            title = title.replace( "\\ ", " " );
        }
        return title;
    }

    protected String convertBody( String title, String body ) {
        body = TAB_REPLACER.replace( body );

        // tilde replacer
        if ( body.contains( "~" ) ) {
            body = Replacers.regex( "\\~", title ).replace( body );
        }

        body = convertTranscription( body );

        return Replacers.replaceAll( body, replacers );
    }

    private String convertTranscription( String dsl ) {
        StringBuilder buf = new StringBuilder( dsl );
        int fromIndex = 0;
        for ( ; ; ) {
            // next [t]
            int indexOfT = buf.indexOf( TAG_TRANS, fromIndex );
            if ( indexOfT < 0 ) {
                break;
            }
            indexOfT += TAG_TRANS.length();

            // next [/t]
            int indexOfTT = buf.indexOf( TAG_TRANS_CLOSE, indexOfT );
            for ( int i = indexOfT; i < indexOfTT; i++ ) {
                String replace = trans.get( buf.charAt( i ) );
                if ( replace != null ) {
                    buf.replace( i, i + 1, replace );

                    // take into account possbile 2-symbols replace
                    int shift = replace.length() - 1;
                    i += shift;
                    indexOfTT += shift;
                }
            }
            fromIndex = indexOfTT;
        }
        dsl = buf.toString();
        return dsl;
    }

    private void initTransMap() {
        trans.put( '\'', "\u02c8" );// ' (IPA)
        trans.put( '\u002c', "\u02cc" );// , (IPA)              - OR \u02cc

        trans.put( '\u00a0', "t\u0283" );// (ts)     - CHange    - OR \u02a7
        trans.put( '\u00a4', "\u0062" );// b
        trans.put( '\u00a6', "\u0077" );// w
        trans.put( '\u00a7', "\u0066" );// f
        trans.put( '\u00a9', "\u0073" );// s
        trans.put( '\u00ab', "\u0074" );// t
        trans.put( '\u00ac', "\u0064" );// d
        trans.put( '\u00ad', "\u006e" );// n
        trans.put( '\u00ae', "\u006c" );// l
        trans.put( '\u00b0', "\u006b" );// k
        trans.put( '\u00b1', "\u0261" );// g (IPA)
        trans.put( '\u00b5', "\u0061" );// a
        trans.put( '\u00b6', "\u028a" );// U (IPA)
        trans.put( '\u00b7', "\u00e3" );// (a~) (IPA)
        trans.put( '\u00bb', "\u0258" );// (e*//) (IPA)
        trans.put( '\u0402', "i:" );// i:       - skI
        trans.put( '\u0403', "\u0251:" );// a: (IPA) - chAnce
        trans.put( '\u0404', "\u007a" );// z
        trans.put( '\u0405', "oe" );// (o~e)
        trans.put( '\u0406', "\u0068" );// h
        trans.put( '\u0407', "\u0072" );// r
        trans.put( '\u0408', "\u0070" );// p
        trans.put( '\u0409', "\u0292" );// 3        - abraSion
        trans.put( '\u040a', "\u014b" );// nj       - thiNG
        trans.put( '\u040b', "\u03b8" );// O-       - THink
        trans.put( '\u040c', "\u0075" );// u
        trans.put( '\u040e', "\u026a" );// I (IPA)
        trans.put( '\u040f', "\u0283" );// s        - SHot
        trans.put( '\u0428', "\u0061" );// a
        trans.put( '\u0440', "\u014f" );// o\/ (IPA)
        trans.put( '\u0443', "\u00f0" );// (o/) (IPA)
        trans.put( '\u0452', "\u0076" );// v
        trans.put( '\u0453', "u:" );// u:       - trUE
        trans.put( '\u0454', "\u0254\u00f8" );// (c*_o/)
        trans.put( '\u0455', "\u012d" );// i\/ (IPA)
        trans.put( '\u0456', "\u006a" );// j
        trans.put( '\u0457', "\u0173" );// 4 (IPA)
        trans.put( '\u0458', "\u0153" );// (oe) (IPA)
        trans.put( '\u045e', "\u0065" );// e
        trans.put( '\u0490', "\u006d" );// m
        trans.put( '\u0491', "\u025b" );// E (IPA)
        trans.put( '\u201a', "\u0254:" );// c*:      - OUGHt
        trans.put( '\u201e', "\u0259:" );// e*:      - 'twERE
        trans.put( '\u2018', "\u0251" );// a (IPA)
        trans.put( '\u2020', "\u0259" );// e*       - 'Em
        trans.put( '\u2021', "\u00e6" );// (ae)     - rAg
        trans.put( '\u2026', "\u028c" );// /\       - dUck
        trans.put( '\u2030', "\u00f0" );// o\       - THis
        trans.put( '\u2039', "d\u0292" );// (d3)     - chanGE    - OR \u02a4
        trans.put( '\u20ac', "\u0254" );// c*       - dOg
        trans.put( '\u2116', "ao" );// (A_O)
    }
}
