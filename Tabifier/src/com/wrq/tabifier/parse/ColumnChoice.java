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
import com.wrq.tabifier.util.Constraints;
import com.wrq.tabifier.util.SizedPanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.awt.*;

/**
 * A ColumnChoice is used when zero or more ColumnSequences occupy the same vertical space.  For example, a
 * variable declaration initializer may be a constant (PsiLiteralExpression) or a method call.  Both the constant
 * and method call are to follow the assignment operator, but are aligned independently of each other.  In this
 * case, a ColumnChoice object is created to hold the initializer, and it contains two ColumnSequences: one for
 * expressions, the other for method calls.
 * @see ColumnSequence
 */
public final class ColumnChoice
        extends AlignableColumn
{
    private static final Logger               logger  = Logger.getLogger("com.wrq.tabifier.parse.ColumnChoice");
    private        final List<ColumnSequence> choices;

    public ColumnChoice(ColumnSetting           setting,
                        AlignableColumnNodeType nodeType,
                        int                     tab_spacing,
                        ColumnSequence          sequenceHead,
                        TabifierSettings        settings)
    {
        super(setting, nodeType, tab_spacing, sequenceHead, settings);
        choices = new LinkedList<>();
    }

    /**
     * A ColumnChoice column has no tokens of its own, so always return false.
     */
    public final boolean isAllTokensHaveLeadingSpace(int indentBias)
    {
        return false;
    }

    public boolean isAllTokensHaveTrailingSpace(int tabStop, int indentBias)
    {
        boolean                      result = true;
        ListIterator<ColumnSequence> li     = choices.listIterator();
        while (result && li.hasNext())
        {
            ColumnSequence                sequence =  li.next();
            ListIterator<AlignableColumn> cols     = sequence.getSequenceList().listIterator();
            while (result && cols.hasNext())
            {
                AlignableColumn column = cols.next();
                if (column instanceof TokenColumn) {
                    if (column.getTabstop() + column.getMaxWidth() == tabStop) {
                    /**
                     * this column ends where the parent ends, so check its tokens for trailing space.
                     */
                        result = column.isAllTokensHaveTrailingSpace(tabStop, indentBias);
                    }
                }
                else {
                    if (column.getTabstop() <= tabStop && column.getTabstop() + column.getMaxWidth() >= tabStop) {
                        /**
                         * this column encompasses the tab stop in question.  Dig deeper.
                         */
                        result = column.isAllTokensHaveTrailingSpace(tabStop, indentBias);

                    }
                }
            }
        }
        return result;
    }

    private ColumnSequence find(ColumnSequenceNodeType nodeType)
    {
        for (ColumnSequence columnSequence : choices)
        {
            if (columnSequence.getNodeType() == nodeType)
                return columnSequence;
        }
        return null;
    }

    public final ColumnSequence findOrAppend(ColumnSequenceNodeType nodeType)
    {
        ColumnSequence result = find(nodeType);
        if (result == null)
        {
            result = append(nodeType);
        }
        return result;
    }

    private ColumnSequence append(ColumnSequenceNodeType nodeType)
    {
        ColumnSequence result = ColumnSequenceFactory.createColumnSequence(nodeType, this, tab_size,
                settings); 
        choices.add(result);
        return result;
    }

    public final String getName()
    {
        return nodeType.getName();
    }

    public final void align(int indentBias)
    {
        super.align(indentBias);
        calculateMaxWidth(true, indentBias);
    }

    /**
     * When we are aligning the tokens in a column, we want to recursively visit all columns in
     * the column sequence belonging to this ColumnChoice.  But when we are updating a TokenColumn's
     * width and pushing that new width up to its ColumnSequence and possibly higher, we don't want to
     * realign other tokens; just add up the other values.
     * @param recurse true if each column sequence in this column choice should be aligned
     */
    void calculateMaxWidth(boolean recurse, int indentBias)
    {
        maxWidth = 0;
        for (ColumnSequence columnSequence : choices)
        {
            if (recurse)
            {
                columnSequence.align(tabstop, indentBias);
            }
            if (columnSequence.getTotalWidth() > maxWidth)
            {
                maxWidth = columnSequence.getTotalWidth();
            }
        }
        if (sequenceHead != null) {
            sequenceHead.calculateWidth(indentBias);
        }
    }

    void calculateAlternateRepresentations(int indentBias)
    {
        for (ColumnSequence columnSequence : choices)
        {
            columnSequence.calculateAlternateRepresentations(indentBias);
        }
    }

    public void clearTokens(Line except, int indentBias)
    {
        super.clearTokens(except, indentBias);
        for (ColumnSequence columnSequence : choices)
        {
            columnSequence.clearTokens(except, indentBias);
        }
    }

    public boolean determineNodesToDump(int indentBias)
    {
        includeInDump = false;
        for (ColumnSequence sequence : choices)
        {
            includeInDump |= sequence.determineNodesToDump(indentBias);
        }
        return includeInDump;
    }

    public final void dumpDetails(String prefix, int indentBias)
    {
        if (!includeInDump) return;
        String abbr = abbreviated(prefix);
        if (this.choices.size() > 0)
        {
            logger.debug(abbr + "    choices are: (size " + choices.size() + ")");
            int                          i  = 1;
            for (ColumnSequence choice : choices)
            {
                choice.dump(prefix + ".S" + i, indentBias);
                i++;
            }
        }
    }

    public void resetValues()
    {
        super.resetValues();
        for (ColumnSequence sequence : choices)
        {
            sequence.resetValues();
        }
    }

    public JPanel display(boolean reduceClutter)
    {
        SizedPanel panel = new SizedPanel();
        Constraints constraints = new Constraints(GridBagConstraints.NORTHWEST);
        String title = nodeType.getName() + " at " + tabstop + ", max width=" + maxWidth;
//        Dimension preferred = new JLabel(title).getPreferredSize();
//        preferred.width += 8;
//        panel.setMinimumSize(preferred);
//        GridBagConstraints constraints = new GridBagConstraints();
//        constraints.weightx = constraints.weighty = 1;
        constraints.insets = new Insets(3, 3, 3, 3);
        for (ColumnSequence sequence : choices)
        {
            if (sequence.getTotalWidth() > 0 || !reduceClutter)
            {
                JPanel sequencePanel = sequence.display(reduceClutter);
                panel.add(sequencePanel, constraints.weightedNextCol());
                constraints.newRow();
            }
        }
        panel.setTitle(title);
        return panel;
    }
}
