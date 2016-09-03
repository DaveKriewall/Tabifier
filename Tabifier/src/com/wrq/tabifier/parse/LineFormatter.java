/*
 * Copyright (c) 2003, 2010, Dave Kriewall
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.wrq.tabifier.parse;

import java.util.Iterator;

/**
 * Performs finish formatting on lines, aligning their tokens according to their assigned columns (tabstops) and
 * replacing spaces with tabs appropriately.
 */
public final class LineFormatter
{
    private final Line         line;
    private final boolean      use_tab_char;
    private final boolean      smart_tabs;
    private final int          tab_size;
    private final int          indentationSpaces;   // number of spaces allocated for left margin.
    private       int          lastAbsoluteTabstop;
    private final StringBuffer sb;
    private       int          column;
    private final int          indent;

    /**
     *
     * @param line                 line to be formatted.
     * @param use_tab_char         true if tabs should be used instead of spaces.
     * @param  smart_tabs          true if only the first <indentation> spaces should be replaced by
     *                             tab characters.
     * @param tab_size             number of spaces per tab character.
     * @param indent               number of spaces per indent level.
     */
    public LineFormatter(Line    line,
                         boolean use_tab_char,
                         boolean smart_tabs,
                         int     tab_size,
                         int     indent       )
    {
        this.line              = line;
        sb                     = new StringBuffer();
        column                 =            0;
        this.use_tab_char      = use_tab_char;
        this.smart_tabs        = smart_tabs;
        this.tab_size          = tab_size;
        this.indent            = indent;
        this.indentationSpaces = line.getIndentLevel() * indent;
    }

    public final String getValue()
    {
        return sb.toString();
    }

    /**
     *
     * @return true if statement alignment was changed.
     */
    public final boolean alignStatement()
    {
        lastAbsoluteTabstop = indentationSpaces;
        sb.setLength(0);
        Iterator tokenIterator        = line.getTokens().iterator();
        while (tokenIterator.hasNext())
        {
            AlignableToken token = (AlignableToken) tokenIterator.next();
            if (token.isWhiteSpace())
            {
                // if a newline, remove trailing spaces from the line (which could happen with a final semicolon,
                // for example) and append the newline.
                if (token.getValue().indexOf('\n') >= 0)
                {
                    while (sb.length()                >    0 &&
                           sb.charAt(sb.length() - 1) == ' '   )
                    {
                        sb.setLength(sb.length() - 1);
                    }
                    sb.append(token.getValue());
                    column = 0;
                }
                continue;
            }
            if (token.getColumn() != null)
            {
                int tabstop = token.getLinePosition();
                if (line.getIndentBias() > 0) {
                    tabstop = tabstop + line.getIndentBias() * indent;
                }
                if (tabstop >= 0)
                {
                    tabstop += indentationSpaces;
                }
                if (column > tabstop) {
                    /**
                     * a trailing space from the previous token has pushed us past the tabstop for the next token
                     * (which has a leading space).  So drop that many characters from the output buffer so we're back
                     * to the correct column.
                     */
                    int drop = column - tabstop;
                    while (drop-- > 0) {
                        if (sb.charAt(sb.length() - 1) == ' ') {
                            sb.setLength(sb.length() - 1);
                            column -= 1;
                        }
                        else break;
                    }
                }
                pad(indentationSpaces, tabstop);
            }
            else
            {
                pad(indentationSpaces, -1);
            }
            if ((token.getValue() != token.getOriginalValue() ||
                 sb.length() > 0 && sb.charAt(sb.length() - 1) == ' ')
                                                                       && use_tab_char && !smart_tabs)
            {
                // alternate representation may have embedded spaces; these may need to be converted to tabs.
                // Also possible that token begins with a space which could be combined with preceding spaces.
                // Combine preceding spaces (not including indentationSpaces) with the token and tabify it.
                String t              = token.getValue();
                int    i              = sb.length();
                int    nSpacesDropped = 0;
                while (i                >  indentationSpaces &&
                       i                >                  0 &&
                       sb.charAt(i - 1) == ' '                 )
                {
                    t = " " + t;
                    sb.setLength(i - 1);
                    nSpacesDropped++;
                    i--;
                }
                sb.append(tabify(t, column - nSpacesDropped, tab_size));
            }
            else
            {
                sb.append(token.getValue());
            }
            column += token.getWidth();
        }
        return true; // TODO - could be optimized to actually check to see if spacing was changed.
    }

    private void pad(final int indentLevel, int tabstop)
    {
        if (use_tab_char)
        {
            column = tabTo(column, indentLevel);
        }
        if (tabstop < 0)
        {
            /**
             * determine an absolute tabstop from the relative spacing given.
             */
            if (column < indentLevel)
            {
                // this is the first token on the line; just align it at the indent level.
                tabstop = indentLevel;
            }
            else if (column != lastAbsoluteTabstop)
            {
                /**
                 * we have emitted some non-blank text since the last absolute tabstop; so the
                 * minimum spacing demanded by the column type is required.
                 */
                tabstop = column + ((-tabstop) - 1);
            }
        }
        if (use_tab_char && !smart_tabs)
        {
            /**
             * generate tab characters within the line.
             */
            column = tabTo(column, tabstop);
        }
        while (column < tabstop)
        {
            sb.append(' ');
            column++;
        }
        if (tabstop > 0)
        {
            lastAbsoluteTabstop = tabstop;
        }
    }

    /**
     * Emit as many tabs as possible to move cursor from currentColumn up to (but not beyond)
     * desiredColumn.
     *
     * @param  currentColumn
     * @param  desiredColumn
     * @return                     updated current column (after emitting as many tab characters as
     *                             possible).
     */
    private int tabTo(int currentColumn, int desiredColumn)
    {
        while (currentColumn + (tab_size - (currentColumn % tab_size)) <= desiredColumn)
        {
            sb.append('\t');
            currentColumn += (tab_size - (currentColumn % tab_size));
        }
        return currentColumn;
    }


    /**
     * Replace any sequence of spaces with an equivalent set of tab characters, if possible. Assume
     * that the string begins in column 0.
     *
     * @param  s                   string to be tabified.
     * @return                     string whose spaces have been replaced by equivalent tab
     *                             characters.
     */
    public static String tabify(final String s,
                                      int    startingColumn,
                                      int    tab_size       )
    {
        final StringBuffer n      = new StringBuffer(s.length());
              int          column = startingColumn;
        for (int charIndex = 0; charIndex < s.length();)
        {
            final char c = s.charAt(charIndex);
            if (c == '\n')
            {
                n.append(c);
                charIndex++;
                column = 0;
            }
            else if (c == '\t')
            {
                n.append(c);
                charIndex++;
                column += (tab_size - (column % tab_size));
            }
            else if (c != ' ')
            {
                n.append(c);
                charIndex++;
                column++;
            }
            else
            {
                //
                // count number of consecutive spaces.
                //
                int n_spaces = 1;
                for (int j = charIndex + 1; j < s.length(); j++)
                {
                    if (s.charAt(j) == ' ')
                    {
                        n_spaces++;
                    }
                    else
                        break;
                }
                //
                // determine if one or more tab characters can replace these spaces.
                //
                int rmndr;
                while (n_spaces >= (rmndr = tab_size - (column % tab_size)))
                {
                    n.append('\t');
                    n_spaces  -= rmndr;
                    charIndex += rmndr;
                    column    += rmndr;
                }
                //
                // append any remaining spaces that couldn't be handled by tabs.
                //
                charIndex += n_spaces;
                column    += n_spaces;
                while (n_spaces > 0)
                {
                    n.append(' ');
                    n_spaces--;
                }
            }
        }
        return n.toString();
    }
}
