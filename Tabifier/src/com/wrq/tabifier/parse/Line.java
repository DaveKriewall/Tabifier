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

import java.util.LinkedList;
import java.util.List;

/**
 * Corresponds to a parsed line of Java code.  Contains a list of tokens.
 */
public final class Line
{

    private final List<AlignableToken> tokens;                    // tokens belonging to this line
    private       boolean              isEntirelyUnaligned;       // true if no tokens on the line belong to any column.  Don't reformat.
    private       int                  indentLevel;
    private       int                  indentBias;                // extra indentation for continuation lines
                  boolean              isBlankLine;
    private       boolean              immutable;                 // true while line is being formed, partially parsed
    private boolean calculatedOriginalWidth;
    private int originalWidth;
    private LineFormatter formatter;
    private boolean formatDiffers;

    public Line()
    {
        tokens                    = new LinkedList<>();
        isBlankLine               = true;
        isEntirelyUnaligned       = true;
    }

    public final List<AlignableToken> getTokens()
    {
        return tokens;
    }

    /**
     *
     * @return true if no token in the line is assigned to a TokenColumn.  This means the line has no tokens that
     * can be aligned.
     */
    public final boolean isEntirelyUnaligned()
    {
        return isEntirelyUnaligned;
    }

    public final boolean isBlankLine()
    {
        return isBlankLine;
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    public void setImmutable(boolean immutable)
    {
        this.immutable = immutable;
    }

    public final void newToken(AlignableToken token)
    {
        tokens.add(token);
        token.setLine(this);
    }

    public void updateBlankAndUnalignedFlags(AlignableToken token)
    {
        if (!token.isWhiteSpace())
        {
            isBlankLine = false;
        }
        if (token.getColumn() != null)
        {
            isEntirelyUnaligned = false;
        }
    }

    public final int getIndentLevel()
    {
        return indentLevel;
    }

    public final void setIndentLevel(int indentLevel)
    {
        this.indentLevel = indentLevel;
    }

    public int getIndentBias()
    {
        return indentBias;
    }

    public void setIndentBias(int indentBias)
    {
        this.indentBias = indentBias;
    }

    /**
      *
      * @return starting offset of first token in the line (usually whitespace).
      */
     public final int getStartOffset()
     {
         // find first AlignableToken and return its offset.
         //noinspection LoopStatementThatDoesntLoop
         for (AlignableToken t : tokens)
         {
             return t.getElement().getTextRange().getStartOffset() + t.getElementOffset();
         }
         return 0;
     }

    /**
     *
     * @return ending offset of last token in the line (a newline character.)
     */
    public final int getEndOffset()
    {
        AlignableToken token = tokens.get(tokens.size() - 1);
        return token.getElement().getTextRange().getStartOffset() +
               token.getElementOffset()                           +
               token.getElementLength();
    }

    public int getOriginalWidth()
    {
        return originalWidth;
    }

    public void calculateOriginalWidth()
    {
        if (!calculatedOriginalWidth) {
            calculatedOriginalWidth = true;
            originalWidth = 0;
            for (AlignableToken token : getTokens())
            {
                originalWidth += token.getWidth();
                if (token.getValue().indexOf('\n') >= 0)
                {
                    originalWidth--; // don't count newline character at end
                }
            }
        }
    }

    public int getTabifiedWidth()
    {
        int width = 0;
        for (AlignableToken token : getTokens())
        {
            if (!token.isWhiteSpace())
            {
                if (token.getLinePosition() + token.getWidth() > width)
                {
                    width = token.getLinePosition() + token.getWidth();
                }
            }
        }
        return width;
    }

    public boolean isFormatDiffers()
    {
        return formatDiffers;
    }

    public void formatLine(boolean use_tab_char,
                           boolean smart_tabs,
                           int tab_size,
                           int indent)
    {
        if (formatter == null) {
            formatter = new LineFormatter(this, use_tab_char, smart_tabs, tab_size, indent);
        }
        formatDiffers = formatter.alignStatement();
    }

    public String getFormattedLine()
    {
        return formatter.getValue();
    }

    public final String toString()
    {
        StringBuilder result = new StringBuilder(120);
        for (AlignableToken token : getTokens())
        {
            result.append(token.getValue());
        }
        return result.toString();
    }
}
