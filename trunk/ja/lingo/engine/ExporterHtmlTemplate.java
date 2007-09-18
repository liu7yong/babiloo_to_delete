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

package ja.lingo.engine;

import ja.lingo.application.util.misc.Strings;
import ja.lingo.engine.beans.IArticle;
import ja.lingo.engine.dictionaryindex.reader.IDictionaryIndex;
import ja.lingo.engine.util.CssHelper;

import java.util.regex.Pattern;

class ExporterHtmlTemplate {
    private static final String CSS_RAW = new CssHelper().asString();
    private static final String CSS_REFERENCED = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">";
    private static final String CSS_EMBEDDABLE = "<style type=\"text/css\">" + CSS_RAW + "</style>";

    private ExporterHtmlTemplate() {
    }

    public static void appendBodies( IArticle article, StringBuilder builder ) {
        appendBodies( article, builder, null ); // don't highlight
    }

    public static void appendBodies( IArticle article, StringBuilder builder, String highlight ) {
        // append bodies (with taking in mind the possibility of duplicated articles in one dictionary)
        IDictionaryIndex lastDictionaryIndex = null;
        builder.append( "<table width=100% border=0 cellspacing=0 cellpadding=5>" );
        for ( int i = 0; i < article.size(); i++ ) {
            if ( lastDictionaryIndex != article.getReader( i ) ) {
                builder.append( "<tr><td class=section><nobr><b>" )
                        .append( Strings.escapeHtml( article.getTitle() ) )
                        .append( "</b></nobr></td><td class=section align=right><nobr>" )
                        .append( Strings.escapeHtml( article.getReaderInfo( i ).getTitle() ) )
                        .append( "</nobr></td></tr>" );
            }

            builder.append( "<tr><td colspan=2>" )
                    .append( highlight( article.getBody( i ), highlight ) )
                    .append( "</td></tr>" );

            lastDictionaryIndex = article.getReader( i );
        }
        builder.append( "</table>" );
    }
    private static String highlight( String text, String highlight ) {
        if ( Strings.isEmpty( highlight ) ) {
            return text;
        }

        String target = "(" + Pattern.quote( Strings.escapeHtml( highlight ) ) + ")";
        String replacement = "<b>$1</b>";

        return Pattern.compile( target, Pattern.CASE_INSENSITIVE )
                .matcher( text ).replaceAll( replacement );
    }

    public static String getHeader() {
        return "<html><body>";
    }
    public static String getFooter() {
        return "</body></html>";
    }

    public static String getStandaloneCssReferenced() {
        return CSS_REFERENCED;
    }
    public static String getStandaloneCssEmbeddable() {
        return CSS_EMBEDDABLE;
    }
    public static String getStandaloneCssRaw() {
        return CSS_RAW;
    }
    public static String getStandaloneHeader( String title ) {
        return "<html><head><title>" + Strings.escapeHtml( title ) + "</title>";
    }
    public static String getStandaloneHeader2() {
        return "</head><body>";
    }
    public static String getStandaloneFooter() {
        return "</body></html>";
    }

    public static String getIndexFileContent( IArticleList articleList ) {
        return "<html><head><title>" + articleList.size()
                + " articles - exported by JaLingo</title></head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n"
                + "\n"
                + "<frameset cols=\"26%,74%\">\n"
                + "<frame src=\"list.html\" name=\"listFrame\">\n"
                + "<frame src=\"\" name=\"contentFrame\">\n"
                + "</frameset>\n"
                + "\n"
                + "<noframes><a href=\"list.html\">Index</a></noframes>\n"
                + "\n"
                + "</html>";
    }

    public static String getListFileHeader() {
        return ("<html><head><base target=\"contentFrame\"></head><body><pre>\n");
    }
    public static String getListFileEntry( String articleFileName, String title ) {
        return ("<a href=\"" + articleFileName + "\">"
                + title + "</a>\n");
    }
    public static String getListFileFooter() {
        return "</pre></body></html>";
    }
}
