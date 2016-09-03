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

import com.wrq.tabifier.util.Constraints;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Recursively visits all nodes in the tree, calculating tabstops for each token.
 */
public final class ColumnNodeAligner
{
    private static final Logger          logger        = Logger.getLogger("com.wrq.tabifier.parse.ColumnNodeAligner");
    private        final ColumnSequence  parentNode;
    private        final ArrayList/*<Line>*/ lineGroup;
    private        final TokenColumn     unknownTokens;
    private int maxIndentBias;
    private boolean reduceClutter = true;

    public ColumnNodeAligner(ColumnSequence  parentNode,
                             ArrayList/*<Line>*/ lineGroup
                             )
    {
        this.parentNode    = parentNode;
        this.lineGroup     = lineGroup;
        /**
         * find unknownTokens column from the parentNode.
         */
        ColumnChoice   program         = parentNode.findColumnChoice(AlignableColumnNodeType.PROGRAM);
        ColumnSequence unknownTokenSeq = program.findOrAppend(ColumnSequenceNodeType.UNKNOWN_TOKEN_SEQ);
        unknownTokens = unknownTokenSeq.findTokenColumn(AlignableColumnNodeType.START_OF_COLUMN);
        maxIndentBias = -1;
    }

    public int getMaxIndentBias()
    {
        return maxIndentBias;
    }

    public final void align()
    {
        int totalLines, remainingLines;
        totalLines = lineGroup.size();
        /**
         * first, discard any lines that are completely unaligned or blank.
         */
        ListIterator/*<Line>*/ li = lineGroup.listIterator();
        while (li.hasNext())
        {
            Line line = (Line) li.next();
            line.calculateOriginalWidth();
            if (line.isEntirelyUnaligned() || line.isBlankLine)
            {
                li.remove();
            }
        }
        remainingLines = lineGroup.size();
        if (totalLines != remainingLines)
        {
            logger.debug("discarded "   + (totalLines - remainingLines) + " from accumulated list; " +
                         remainingLines + " remain"                    );
        }
        else
        {
            logger.debug("aligning " + remainingLines + " lines");
        }
        if (remainingLines == 0)
        {
            // nothing left to do.  Exit early to avoid dumping a devoid-of-tokens tree to logger.
            return;
        }
        maxIndentBias = -1;
        li = lineGroup.listIterator();
        while (li.hasNext())
        {
            Line line = (Line) li.next();
            if (line.getIndentBias() > maxIndentBias) {
                maxIndentBias = line.getIndentBias();
            }
            logger.debug(line.toString());
        }
        logger.debug("Max continuation indent bias=" + maxIndentBias);
        for (int currentIndentBias = 0; currentIndentBias <= maxIndentBias; currentIndentBias++)
        {
            alignIndentLevel(currentIndentBias);
        }
    }

    private void alignIndentLevel(int currentIndentBias)
    {
        logger.debug("alignIndentLevel " + currentIndentBias);
        ListIterator/*<Line>*/ li;
        parentNode.resetValues();
        /**
         * calculate alternate representations.
         */
        parentNode.calculateAlternateRepresentations(currentIndentBias);
        /**
         * remove any trailing spaces from the last token of each line.  This can happen if a trailing operator such
         * as " + " is at the end of line, and it can affect alignment of other columns like semicolon and trailing
         * comments.
         */
        li = lineGroup.listIterator();
        while (li.hasNext())
        {
            Line line = (Line) li.next();
            if (line.isImmutable()) {
                continue;
            }
            if (line.getTokens().size() > 1)
            {
                AlignableToken t = (AlignableToken) line.getTokens().get(line.getTokens().size() - 2);
                if (t.getValue().length() > 0)
                {
                    if (t.getValue().charAt(t.getValue().length() - 1) == ' ')
                    {
                        t.setAlternateRepresentation(t.getValue().substring(0, t.getValue().length() - 1));
                    }
                }
            }
        }
        /**
         * Now visit each line and move or merge any unknown or unaligned tokens to previous aligned column.
         */
        li = lineGroup.listIterator();
        while (li.hasNext())
        {
            Line line = (Line) li.next();
            if (line.isImmutable()) {
                continue;
            }
            ListIterator/*<AlignableToken>*/ tokenList        = line.getTokens().listIterator();
            AlignableToken               lastAlignedToken = null;
            AlignableToken               firstToken       = null;
            AlignableToken               lastWhiteSpace   = null;
            while (tokenList.hasNext())
            {
                AlignableToken genericToken = (AlignableToken) tokenList.next();
                if (genericToken.isWhiteSpace())
                {
                    lastWhiteSpace = genericToken;
                    continue;
                }
                if (firstToken == null)
                {
                    firstToken = genericToken;
                }
                if (genericToken.getColumn() == null)
                {
                    // this is an unknown token; it was not parsed as anything but a blind token (PsiElement).
                    // it does not belong to a column at all.  Task here is to find a previous token on this line
                    // that does belong to an alignable column.  If none, put this token into the unknown token
                    // column.  Once this is done, all subsequent unknown tokens will be appended to this
                    // token.
                    if (lastAlignedToken == null)
                    {
                        // this is the first token.  Put it in the unknown token column.  If whitespace preceded it,
                        // keep that.
                        unknownTokens.addToken(genericToken);
                        genericToken.setTokenColumn(unknownTokens);
                        lastAlignedToken = genericToken;
                    }
                    else
                    {
                        // append this token to the last aligned token.
                        lastAlignedToken.setAlternateRepresentation(
                                lastWhiteSpace != null ? lastAlignedToken.getValue().trim() +
                                                         lastWhiteSpace.getValue() +
                                                         genericToken.getValue()
                                                       : safeAppend(lastAlignedToken, genericToken));
                        genericToken.setAlternateRepresentation("");
                        if (lastWhiteSpace != null)
                        {
                            lastWhiteSpace.setAlternateRepresentation("");
                        }
                        tokenList.remove(); // drop genericToken from the list.
                    }
                }
                else
                {
                    /**
                     * if we have assembled an unknown token, move it from the unknown column to the unknown token column
                     * of the first known token's sequenceHead's parent.  This fixes alignment problems where, unbeknownst to
                     * the parent ColumnChoice, a child TokenColumn suddenly has an unexpected tabstop greater than
                     * the parent's.
                     */
                    if (lastAlignedToken != null &&
                            lastAlignedToken.getColumn() == unknownTokens &&
                            lastAlignedToken.getWidth() > 0)
                    {
                        genericToken.setAlternateRepresentation(safeAppend(lastAlignedToken, genericToken));
                        lastAlignedToken.getColumn().removeToken(lastAlignedToken);
                        lastAlignedToken.setAlternateRepresentation("");
                    }
                    lastAlignedToken = genericToken;
                }
                lastWhiteSpace = null;
            }
        }
        /** Now visit each column that is alignable and apply spacing calculations.  Set tabstops on all tokens
         * in these columns.
         */
        parentNode.align(0, currentIndentBias);
        if (logger.isDebugEnabled()) {
            JPanel outerPanel = new JPanel(new GridBagLayout());
            // outer panel consists of scrollable pane and radio buttons for options.
            final Constraints constraints = new Constraints(GridBagConstraints.NORTHWEST);
            constraints.weightedNewRow();
            final JScrollPane pane = new JScrollPane();
            pane.setViewportView(parentNode.display(reduceClutter));
            final JDialog frame = new JDialog((Frame) null, "Tabifier Layout");
            final JCheckBox reduceClutterBox = new JCheckBox("Reduce Clutter");
            reduceClutterBox.setSelected(reduceClutter);
            reduceClutterBox.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    reduceClutter = reduceClutterBox.isSelected();
                    pane.setViewportView(parentNode.display(reduceClutter));
                }
            });
            constraints.fill = GridBagConstraints.BOTH;
            pane.setPreferredSize(new Dimension(700, 500));
            outerPanel.add(pane, constraints.weightedLastCol());
            constraints.lastRow();
            outerPanel.add(reduceClutterBox, constraints.lastCol());
            //Finish setting up the frame, and show it.
            ((java.awt.Container)frame.getContentPane()).add(outerPanel);
            frame.pack();
            frame.setResizable(true);
            frame.setModal(true);
            frame.setVisible(true);

        }
        /**
         * Now calculate indentation for wrapped lines, grouped by their indentBias.
         */
        logger.debug("exit alignIndentLevel " + currentIndentBias);
    }

    private static String safeAppend(AlignableToken lastAlignedToken, AlignableToken genericToken)
    {
        String value = lastAlignedToken.getValue();
        String value1 = genericToken.getValue();
        if (value.length() == 0)
        {
            return value1.trim();
        }
        if (value1.length() == 0)
        {
            return value;
        }
        if (genericToken.needSpaceAfterPreviousToken(lastAlignedToken))
        {
            return value + " " + value1;
        }

        return value + value1;
    }
}
