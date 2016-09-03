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

import com.wrq.tabifier.settings.ColumnSetting;
import com.wrq.tabifier.settings.TabifierSettings;
import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Base class for ColumnChoice and TokenColumn.  An AlignableColumn represents a tree of tokens aligned to the same
 * left margin.  If the AlignableColumn is a TokenColumn, then zero or more tokens have been assembled into one
 * physical column.  If the AlignableColumn is a ColumnChoice, then any number of types of sequences (one type for
 * each alignment group) may comprise the column.
 */
abstract public class AlignableColumn
{
    private static final Logger logger = Logger.getLogger("com.wrq.tabifier.parse.AlignableColumn");
    final ColumnSetting setting;
    final AlignableColumnNodeType nodeType;
    final ColumnSequence sequenceHead;
    int maxWidth;
    int tabstop;
    final int tab_size;
    boolean includeInDump;
    final protected TabifierSettings settings;
    protected AlignableColumn(ColumnSetting setting,
            AlignableColumnNodeType nodeType,
            final int tab_spacing,
            ColumnSequence sequenceHead,
            TabifierSettings settings)
    {
        this.setting = setting;
        this.nodeType = nodeType;
        this.tab_size = tab_spacing;
        this.sequenceHead = sequenceHead;
        this.settings = settings;
    }

    final ColumnSetting getColumnSetting()
    {
        return setting;
    }

    public final int getMaxWidth()
    {
        return maxWidth;
    }

    public final int getTabstop()
    {
        return tabstop;
    }

    private void setTabstop(int tabstop)
    {
        this.tabstop = tabstop;
    }

    public final AlignableColumnNodeType getNodeType()
    {
        return nodeType;
    }

    final ColumnSequence getSequenceHead()
    {
        return sequenceHead;
    }

    protected abstract boolean isAllTokensHaveLeadingSpace(int indentBias);

    protected abstract boolean isAllTokensHaveTrailingSpace(int tabstop, int indentBias);

    String getName()
    {
        return getNodeType().getName();
    }

    /**
     * Determine the tabstop of this column.  The old technique of taking the tabstop of the previous column and adding
     * its width no longer works because unaligned tokens can slide leftword into columns to the left or above the
     * immediately previous aligned column.  When this happens, the logical width of those columns is increased and may
     * exceed the previous column's tabstop + width.  The new technique is, consider all columns to the left of this
     * column, including those to the left of this column's parent, recursively.  Find the maximum of (tabstop + width)
     * and return that value.
     * 
     * @param currentColumn column whose tabstop is being determined.  Consider only columns preceding this one.
     * @return tabstop for this column.
     */
    final int determineTabstop(AlignableColumn currentColumn)
    {
        final int index = currentColumn.getSequenceHead().getSequenceList().indexOf(currentColumn);
        int tabstop = 0;
        for (int i = 0; i < index; i++)
        {
            AlignableColumn ac = (AlignableColumn) currentColumn.getSequenceHead().getSequenceList().get(i);
            if (ac.getTabstop() + ac.getMaxWidth() > tabstop)
            {
                tabstop = ac.getTabstop() + ac.getMaxWidth();
            }
        }
        final AlignableColumn parent = currentColumn.getSequenceHead().getParent();
        if (parent != null)
        {
            int maxParentTabstop = determineTabstop(parent);
            int parentTabstop = parent.getTabstop();
            if (maxParentTabstop > tabstop)
            {
                tabstop = maxParentTabstop;
            }
            if (parentTabstop > tabstop)
            {
                tabstop = parentTabstop;
            }
        }
        return tabstop;
    }

    public void align(int indentBias)
    {
        int tabstop = determineTabstop(this);
        boolean logical_width_exists = false;
        if (tabstop > 0)
        {
            /**
             * determine if any column in this sequence has non-zero width.  If so,
             * "logical width" exists and we have to apply spacing.
             */
            int index = sequenceHead.getSequenceList().indexOf(this);
            for (int i = index - 1; i >= 0; i--)
            {
                AlignableColumn ac = (AlignableColumn) sequenceHead.getSequenceList().get(i);
                if (ac.getMaxWidth() > 0)
                {
                    logical_width_exists = true;
                    break;
                }
            }
        }
        if (logical_width_exists)
        {
            /**
             * some column of non-zero width has been seen.
             * So pad the number of spaces or tabs requested by the user onto the
             * current tabstop, to get the tabstop at which this field will be aligned.
             * <p>
             * When a column of tokens such as "=" all have a preceding space (because of code style settings)
             * in their
             * alternate representations, we have to bias the number of spaces the user wants appended to
             * the previous column
             * by -1 so that the effect is what the user expects.  This is done inside the apply() method.
             */
            AlignableColumn root = this;
            while (true)
            {
                AlignableColumn parent = root.getSequenceHead().getParent();
                if (parent != null)
                    root = parent;
                else
                    break;
            }
            if (setting.isAligned() &&
                    (setting.getCharacters() > 0 || isAllTokensHaveLeadingSpace(indentBias)) &&
                    root.isAllTokensHaveTrailingSpace(tabstop, indentBias))
            {
                tabstop--;
            }
            tabstop = setting.apply(tabstop, tab_size, isAllTokensHaveLeadingSpace(indentBias));
        }
        else
        {
            tabstop += (setting.getAdditionalIndentLevel() * tab_size);
        }
        setTabstop(tabstop);
    }

    abstract void dumpDetails(String prefix, int indentBias);

    abstract void calculateAlternateRepresentations(int indentBias);

    abstract void calculateMaxWidth(boolean recurse, int indentBias);

    public void clearTokens(Line except, int indentBias)
    {
        maxWidth = 0;
        tabstop = 0;
    }

    public final String toString()
    {
        return nodeType.getName();
    }

    public static final String abbreviated(String prefix)
    {
        if (prefix.length() > 16)
        {
            return "<" + prefix.length() + ">" + prefix.substring(prefix.length() - 16);
        }
        else
            return prefix;
    }

    public final void dump(String prefix, int indentBias)
    {
        String abbr = abbreviated(prefix);
        if (includeInDump)
        {
            logger.debug(abbr + " == START == indentBias = " + indentBias);
            String className = this.getClass().getName();
            if (className.lastIndexOf(".") > 0)
            {
                className = className.substring(className.lastIndexOf(".") + 1);
            }
            logger.debug(abbr + " " + className + " " + getName() + ": tabstop=" + tabstop + ", max width=" + maxWidth);
            logger.debug(abbr + " settings: " + setting);
        }
        dumpDetails(prefix, indentBias);
        if (includeInDump)
        {
            logger.debug(abbr + " === END ===");
        }
    }

    public abstract boolean determineNodesToDump(int indentBias);

    public void resetValues()
    {
        maxWidth = 0;
        tabstop = 0;
    }

    public abstract JPanel display(boolean reduceClutter);
}
