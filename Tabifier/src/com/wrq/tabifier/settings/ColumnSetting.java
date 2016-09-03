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
package com.wrq.tabifier.settings;

/**
 * ColumnSetting assists in calculating column alignments when these are based on cascading column
 * widths.
 */
public class ColumnSetting
        extends BooleanSetting
{
    /**
     * number of spaces or tabs to append to previous column when aligning.
     */
    private int nCharacters;
    /**
     * true = nCharacters refers to tabs; false = nCharacters refers to spaces.
     */
    private boolean tabs;
    /**
     * additional indentation for the column.  This provides the capability for if-statement then/else statements
     * to be indented one level from the if/else portions.
     */
    private int additionalIndentLevel;

    public ColumnSetting(final boolean initialValue, final int nCharacters, final String name)
    {
        super(initialValue, name);
        this.nCharacters = nCharacters;
        this.tabs = false;
        additionalIndentLevel = 0;
    }

    public ColumnSetting(final boolean initialValue, final String name)
    {
        this(initialValue, 0, name);
    }

    public ColumnSetting(final boolean initialValue, final int nCharacters, final int addlIndent, final String name)
    {
        this(initialValue, nCharacters, name);
        this.additionalIndentLevel = addlIndent;
    }

    private ColumnSetting(final ColumnSetting columnSetting, final String settingName)
    {
        super(columnSetting.get(), settingName);
        this.nCharacters = columnSetting.nCharacters;
        this.tabs = columnSetting.tabs;
        this.additionalIndentLevel = columnSetting.additionalIndentLevel;
    }

    /**
     * Determines if this column is aligned, or flows freely after prior column.
     */
    public final boolean isAligned()
    {
        return get();
    }

    public final void setAligned(final boolean aligned)
    {
        set(aligned);
        notifyChangeListeners();
    }

    public final int getCharacters()
    {
        return nCharacters;
    }

    public final void setCharacters(final int c)
    {
        nCharacters = c;
        notifyChangeListeners();
    }

    public final boolean isTabs()
    {
        return tabs;
    }

    public final void setTabs(final boolean t)
    {
        tabs = t;
        notifyChangeListeners();
    }

    public final int getAdditionalIndentLevel()
    {
        return additionalIndentLevel;
    }

    public boolean equals(final Object obj)
    {
        if (obj instanceof ColumnSetting)
        {
            final ColumnSetting cs = (ColumnSetting) obj;
            return (super.equals(obj) &&
                    cs.nCharacters == nCharacters &&
                    cs.tabs == tabs);
        }
        else
            return false;
    }


    protected Object clone() throws CloneNotSupportedException
    {
        return new ColumnSetting(this, this.settingName);
    }

    protected void readValue(String s)
    {
        set(false);
        if (s.indexOf("align ") == 0)
        {
            set(true);
            s = s.substring(6);
        }
        final int i = s.indexOf(' ');
        if (i < 0)
        {
            // old tabifier settings.
            nCharacters = 1;
            tabs = false;
            return;
        }
        nCharacters = Integer.parseInt(s.substring(0, i));
        tabs = s.substring(i + 1).equals("tabs");
        additionalIndentLevel = 0;
        final int addlIndent = s.indexOf(" additionalIndentLevel=");
        if (addlIndent >= 0)
        {
            additionalIndentLevel = Integer.parseInt(s.substring(addlIndent + " additionalIndentLevel=".length(),
                    s.length()));
        }
    }

    protected String writeValue()
    {
        final StringBuffer b = new StringBuffer(64);
        if (get())
        {
            b.append("align ");
        }
        b.append(Integer.toString(nCharacters));
        b.append(" ");
        b.append(tabs ? "tabs" : "spaces");
        if (additionalIndentLevel > 0)
        {
            b.append(" additionalIndentLevel=" + additionalIndentLevel);
        }
        return b.toString();
    }

    /**
     * Return the new column by adding the appropriate number of tabs or spaces.
     *
     * @param  column              starting column.
     * @param  tab_spacing         number of spaces added by a tab character.
     * @param  hasLeadingSpace     true if all tokens in this column begin with a space (e.g., " = ")
     * @return                     column after adding indicated number of tabs or spaces.
     */
    public final int apply(int column, final int tab_spacing, final boolean hasLeadingSpace)
    {
        if (tabs)
        {
            column = ((column + nCharacters * tab_spacing) / tab_spacing) * tab_spacing;
        }
        else
            column += nCharacters;
        if (nCharacters > 0 && hasLeadingSpace) column--;
        return column;
    }

    public String toString()
    {
        return settingName + ": align=" + this.isAligned() + "; "
                + nCharacters + (tabs ? " tabs" : " spaces");
    }
}
