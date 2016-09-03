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
import com.wrq.tabifier.settings.RearrangeableColumnSetting;
import com.wrq.tabifier.settings.TabifierSettings;
import com.wrq.tabifier.util.Constraints;
import com.wrq.tabifier.util.SizedPanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.awt.*;

/**
 * ColumnSequence, ColumnChoice and TokenColumn objects are used to create a tree of columns
 * that control alignment of tokens and subsequent or dependent columns.
 * <p/>
 * Initially the tree is built by the document parser.  A base (dummy) ColumnSequence exists which
 * corresponds to the left margin, or tabstop position 0.  Other ColumnSequences
 * are built which depend on the the column node to the left.  "Depend on" means that all tokens of the column node to
 * the right are positioned after the longest token of the column node to the left.
 * <p/>
 * A ColumnChoice can serve as a branching point, so that different types of syntax can be aligned vertically.
 * <p/>
 * A ColumnSequence is a sequence of columns which occupy space horizontally (left-to-right) when rendered.
 * The sequence may contain TokenColumn objects (columns which contain AlignableTokens from Line objects) or
 * ColumnChoice objects.
 * @see ColumnChoice
 * @see TokenColumn
 */
public class ColumnSequence
{
    private static final Logger logger = Logger.getLogger("com.wrq.tabifier.parse.ColumnSequence");
    private final List<AlignableColumn> sequenceList;
    private final ColumnSequenceNodeType nodeType;
    private int tabstop;
    private int totalWidth;
    private final int tab_size;
    private final AlignableColumn parent;
    private boolean includeInDump;
    private final TabifierSettings settings;

    public ColumnSequence(ColumnSequenceNodeType nodeType, 
                          AlignableColumn parent, 
                          int tab_size,
                          TabifierSettings settings)
    {
        this.nodeType = nodeType;
        this.parent = parent;
        this.tab_size = tab_size;
        sequenceList = new LinkedList<>();
        this.settings = settings;
    }

    public final ColumnSequenceNodeType getNodeType()
    {
        return nodeType;
    }

    private void setTabstop(int tabstop)
    {
        this.tabstop = tabstop;
    }

    public final int getTotalWidth()
    {
        return totalWidth;
    }

    public AlignableColumn getParent()
    {
        return parent;
    }

    public final void dump(int indentBias)
    {
        dump("S1", indentBias);
    }

    public String getName()
    {
        return nodeType.name;
    }

    public final List<AlignableColumn> getSequenceList()
    {
        return sequenceList;
    }

    public final ColumnChoice appendChoiceColumn(ColumnSetting setting, 
                                                 AlignableColumnNodeType nodeType)
    {
        ColumnChoice result = new ColumnChoice(setting, nodeType, tab_size, this, settings);
        sequenceList.add(result);
        return result;
    }

    private AlignableColumn find(AlignableColumnNodeType nodeType)
    {
        return findNth(nodeType, 1);
    }

    public final TokenColumn findTokenColumn(AlignableColumnNodeType nodeType)
    {
        return (TokenColumn) find(nodeType);
    }
    public final ModifierTokenColumn findModifierTokenColumn(AlignableColumnNodeType nodeType)
    {
        return (ModifierTokenColumn) find(nodeType);
    }
    public final ColumnChoice findColumnChoice(AlignableColumnNodeType nodeType)
    {
        return (ColumnChoice) find(nodeType);
    }
    /**
     * A ColumnSequence can contain multiple occurrences of a node type in its list (for example, when declaring
     * more than one variable or field per line.)  This method finds the nth occurrence (1-relative).
     * 
     * @param nodeType type of node to search for.
     * @param n        ordinal number indicating which occurrence of the nodeType to return.
     * @return AlignableColumn at the indicated position.
     */
    public AlignableColumn findNth(AlignableColumnNodeType nodeType, int n)
    {
        int nOccurrences = 0;
        for (AlignableColumn icc : sequenceList)
        {
            if (icc.getNodeType() == nodeType)
            {
                nOccurrences++;
                if (nOccurrences == n)
                {
                    return icc;
                }
            }
        }
        return null;
    }

    public final TokenColumn appendTokenColumn(ColumnSetting setting, AlignableColumnNodeType nodeType)
    {
        TokenColumn result = new TokenColumn(setting, nodeType, tab_size, this, settings);
        sequenceList.add(result);
        return result;
    }

    public final TokenColumn appendModifierTokenColumn(RearrangeableColumnSetting setting)
    {
        ModifierTokenColumn result = new ModifierTokenColumn(setting,
                com.wrq.tabifier.parse.AlignableColumnNodeType.ASSIGNMENT_MODIFIERS,
                tab_size,
                this,
                settings);
        sequenceList.add(result);
        return result;
    }

    /**
     * recursively aligns this ColumnSequence object by aligning the objects in the sequence list, and
     * calculating the total width.
     * 
     * @param startColumn tabstop where this ColumnSequence object begins.
     */
    public final void align(int startColumn, int indentBias)
    {
        setTabstop(startColumn);
        for (AlignableColumn alignableColumn : getSequenceList())
        {
            alignableColumn.align(indentBias);
        }
        calculateWidth(indentBias);
    }

    public void calculateWidth(int indentBias)
    {
        /**
         * calculate totalWidth to be the the difference between the last token's startColumn + max_width, and the
         * first token's startColumn.  It is difficult to simply add the maxWidth of each alignable column since
         * depending on leading or trailing whitespace and the column alignment settings, the actual width of the
         * column may be different by one.
         */
        int oldTotalWidth = totalWidth;
        totalWidth = 0;
        if (getSequenceList().size() > 0)
        {
            AlignableColumn lastColumn = null;
            ListIterator<AlignableColumn> li = getSequenceList().listIterator(getSequenceList().size());
            while (li.hasPrevious())
            {
                lastColumn = li.previous();
                if (lastColumn.maxWidth > 0) break;
            }
            if (lastColumn != null)
            {
                totalWidth = lastColumn.getTabstop() + lastColumn.getMaxWidth() - tabstop;
            }
        }
//        if (oldTotalWidth != totalWidth) {
//            logger.debug("calculateWidth for sequence " +
//                    getName()  +
//                    (getParent() == null ? "" : " of " + getParent().getName()) +
//                    ": tabstop=" +
//                    tabstop + ", totalWidth=" + totalWidth);
//        }

        if (parent != null && totalWidth != oldTotalWidth)
        {
            /**
             * this column sequence may have been the longest, and now it is shorter or longer.  Give the parent
             * an opportunity to recalculate max width.
             */
            parent.calculateMaxWidth(false, indentBias);
        }
    }

    public final void dump(String prepend, int indentBias)
    {
        // shorten up the debugging display.
        if (!includeInDump) return;
        String abbr = ColumnChoice.abbreviated(prepend);
        logger.debug(abbr + " == SEQ START ==");
        logger.debug(abbr + " ColumnSequence " + getName() + ", tabstop=" + tabstop + ", total width=" + totalWidth);
        if (this.sequenceList.size() > 0 && totalWidth > 0)
        {
            logger.debug(abbr + "    Choice/Token Sequence (size " + sequenceList.size() + "):");
            ListIterator/*<AlignableColumn>*/ li = sequenceList.listIterator();
            int i = 1;
            while (li.hasNext())
            {
                AlignableColumn icc = (AlignableColumn) li.next();
                String prefix = prepend + (icc instanceof ColumnChoice ? ".C" : ".T");
                icc.dump(prefix + i, indentBias);
                i++;
            }
        }
        logger.debug(abbr + " == SEQ END ====");
    }

    public void clearTokens(Line except, int indentBias)
    {
        tabstop = 0;
        totalWidth = 0;

        for (AlignableColumn alignableColumn : getSequenceList())
        {
            alignableColumn.clearTokens(except, indentBias);
        }
    }

    public String toString()
    {
        return nodeType.name;
    }

    public final void calculateAlternateRepresentations(int indentBias)
    {
        for (AlignableColumn alignableColumn : getSequenceList())
        {
            alignableColumn.calculateAlternateRepresentations(indentBias);
        }
    }

    public boolean determineNodesToDump(int indentBias)
    {
        includeInDump = false;
        for (AlignableColumn column : getSequenceList())
        {
            includeInDump |= column.determineNodesToDump(indentBias);
        }
        return includeInDump;
    }

    public void resetValues()
    {
        tabstop = 0;
        totalWidth = 0;
        for (AlignableColumn column : getSequenceList())
        {
            column.resetValues();
        }
    }

    public JPanel display(boolean reduceClutter)
    {
        // if reduce clutter is true, and sequence contains only one item, return the panel for that item
        // instead of the panel for this column sequence.
        SizedPanel panel = new SizedPanel();
        Constraints constraints = new Constraints(GridBagConstraints.NORTHWEST);
        final String title = nodeType.name + " at " + tabstop + " total width: " + totalWidth;
//        final Dimension preferredSize = new JLabel(title).getPreferredSize();
//        preferredSize.width += 10;
//        panel.setMinimumSize(preferredSize);
        //, "Seq:" + nodeType.name + ", tabstop=" + tabstop + ", total width=" + totalWidth));
//        JLabel name = new JLabel("Seq:" + nodeType.name);
//        panel.add(name);
//        panel.add(new JLabel("tabstop=" + tabstop));
//        panel.add(new JLabel("total width=" + totalWidth));
//        GridBagConstraints constraints = new GridBagConstraints();
//        constraints.weightx = constraints.weighty = 1;
        constraints.insets = new Insets(4, 4, 4, 4);
        for (AlignableColumn column : getSequenceList())
        {
            if (column.getMaxWidth() > 0 || !reduceClutter)
            {
                JPanel columnPanel = column.display(reduceClutter);
                panel.add(columnPanel, constraints.weightedNextCol());
            }
//            constraints.gridx++;
        }
        panel.setTitle(title);
        return panel;
    }
}
