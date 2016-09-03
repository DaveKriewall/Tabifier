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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.awt.*;

/**
 * A TokenColumn is the basic unit of the column tree, and represents a set of tokens to be aligned vertically.
 */
public class TokenColumn
        extends AlignableColumn
{
    private static final Logger logger = Logger.getLogger("com.wrq.tabifier.parse.TokenColumn");
    private final List/*<List<AlignableToken>>*/ tokenLists;

    public TokenColumn(ColumnSetting setting,
                       AlignableColumnNodeType nodeType,
                       int tab_spacing,
                       ColumnSequence sequenceHead,
                       TabifierSettings settings)
    {
        super(setting, nodeType, tab_spacing, sequenceHead, settings);
        tokenLists = new ArrayList/*<List<AlignableToken>>*/();
    }

    private List/*<AlignableToken>*/ getTokens(int indentBias)
    {
        return getTokenList(indentBias);
    }

    private void applyTabstopToTokens(int tabstop, int indentBias)
    {
        ListIterator/*<AlignableToken>*/ list = getTokens(indentBias).listIterator();
        while (list.hasNext())
        {
            AlignableToken token = (AlignableToken) list.next();
            if (token.getLine().isImmutable())
                continue;
            token.setLinePosition(tabstop);
        }
    }

    public final boolean isAllTokensHaveLeadingSpace(int indentBias)
    {
        boolean result = (getTokens(indentBias).size() > 0);
        ListIterator/*<AlignableToken>*/ list = getTokens(indentBias).listIterator();
        while (result && list.hasNext())
        {
            AlignableToken token = (AlignableToken) list.next();
            if (token.getLine().isImmutable())
                continue;
            if (token.getWidth() > 0)
            {
                if (token.getValue().charAt(0) != ' ')
                {
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * Considering all tokens which end at column tabstop, return true if all have a trailing space.
     * // todo - should we only count trailing spaces that are there because of code style settings?
     */
    public final boolean isAllTokensHaveTrailingSpace(int tabstop, int indentBias)
    {
        if (getTabstop() + getMaxWidth() == tabstop)
        {
            ListIterator/*<AlignableToken>*/ list = getTokens(indentBias).listIterator();
            while (list.hasNext())
            {
                AlignableToken token = (AlignableToken) list.next();
                if (token.getLine().isImmutable()) continue;
//                if (token.getWidth() == 0) return false;  // todo - an empty token probably results from appending its value to the token to the left, so should just ignore and continue
                if (token.getLinePosition() + token.getWidth() == tabstop)
                {
                    if (token.getValue().charAt(token.getWidth() - 1) != ' ')
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void addToken(AlignableToken token)
    {
        List/*<AlignableToken>*/ tokens = getTokenList(token);
        tokens.add(token);
        token.setTokenColumn(this);
    }

    private List/*<AlignableToken>*/ getTokenList(AlignableToken token)
    {
        int indentBias = token.getLine().getIndentBias();
        return getTokenList(indentBias);
    }

    final List/*<AlignableToken>*/ getTokenList(int indentBias)
    {
        List/*<AlignableToken>*/ tokens;
        while (tokenLists.size() <= indentBias)
        {
            tokenLists.add(new ArrayList/*<AlignableToken>*/());
        }
        tokens = (List/*<AlignableToken>*/) tokenLists.get(indentBias);
        return tokens;
    }

    public final void removeToken(AlignableToken token)
    {
        List/*<AlignableToken>*/ tokens = getTokenList(token);
        tokens.remove(token);
        token.setTokenColumn(null);
    }

    public final void align(int indentBias)
    {
        boolean isAligned = getColumnSetting().isAligned();
        /**
         * This column may belong (be a descendant of) another column setting such as a parameter whose alignment
         * supersedes this column setting. This situation is indicated by the presence of a marker object of
         * PropagatingAlignableColumnNodeType.
         *
         * This only applies to cases where this column is the first non-empty in the sequence.
         */
        final AlignableColumn parent = getSequenceHead().getParent();
        if (parent != null && parent.getNodeType() instanceof PropagatingAlignableColumnNodeType)
        {
            boolean isFirstInSequence = true;
            for (Object o : getSequenceHead().getSequenceList())
            {
                AlignableColumn alignableColumn = (AlignableColumn) o;
                if (alignableColumn == this) break;
                if (alignableColumn.getMaxWidth() > 0)
                {
                    isFirstInSequence = false;
                    break;
                }
            }
            if (isFirstInSequence)
            {
                isAligned = parent.getColumnSetting().isAligned();
            }
        }
        if (!isAligned)
        {
            handleUnalignedTokens(indentBias);
            return;
        }
        super.align(indentBias);
        applyTabstopToTokens(tabstop, indentBias);
        calculateMaxWidth(true, indentBias);
        if (tabstop > 0 || maxWidth > 0)
        {
            logger.debug("aligned column " + getName() + " of " + sequenceHead.getName() + ", tabstop=" + tabstop +
                    ", maxWidth=" + maxWidth);
        }
        /**
         * Handle right-justified tokens.  Make one pass through all the tokens to calculate the widest right-justified
         * token.  Make a second pass right-justifying all other such tokens to the widest one's final column.  This
         * right-justifies numeric values relative only to each other, not to the widest token in the entire token
         * column.
         */
        int widestRJToken = 0;
        ListIterator/*<AlignableToken>*/ li = getTokens(indentBias).listIterator();
        while (li.hasNext())
        {
            AlignableToken token = (AlignableToken) li.next();
            if (token.getLine().isImmutable())
                continue;
            if (token.isRightJustified())
            {
                if (widestRJToken < token.getWidth())
                    widestRJToken = token.getWidth();
            }
        }
        li = getTokens(indentBias).listIterator();
        while (li.hasNext())
        {
            AlignableToken token = (AlignableToken) li.next();
            if (token.getLine().isImmutable())
                continue;
            if (token.isRightJustified())
            {
                token.setLinePosition(token.getLinePosition() + widestRJToken - token.getWidth());
            }
        }
    }

    /**
     * When we are aligning the tokens in a column, we want to recursively visit all columns in
     * the column sequence belonging to this ColumnChoice.  But when we are updating a TokenColumn's
     * width and pushing that new width up to its ColumnSequence and possibly higher, we don't want to
     * realign other tokens; just add up the other values.  In any case, there's no recursion involved
     * in TokenColumn calculation of max width; token alignment takes place in the align() method.
     * 
     * @param recurse 
     */
    final void calculateMaxWidth(boolean recurse, int indentBias)
    {
        ListIterator/*<AlignableToken>*/ li;
        maxWidth = 0;
        li = getTokens(indentBias).listIterator();
        while (li.hasNext())
        {
            AlignableToken token = (AlignableToken) li.next();
            if (token.getLine().isImmutable())
                continue;
            if (token.getLinePosition() + token.getWidth() > tabstop + maxWidth)
            {
                maxWidth = token.getLinePosition() + token.getWidth() - tabstop;
            }
        }
//        logger.debug("calculateMaxWidth for token column=" + getName() + " of " + getSequenceHead().getName() +
//                ": tabstop=" + tabstop + ", maxWidth=" + maxWidth);
        if (sequenceHead != null)
        {
            sequenceHead.calculateWidth(indentBias);
        }
    }

    /**
     * Align any tokens in an unaligned column immediately following the prior token, or at the previously
     * aligned column (but not less than left indent).
     */
    // TODO - won't be needed after combining unaligned tokens with prior non-blank token
    private void handleUnalignedTokens(int indentBias)
    {
        boolean displayDebug = getTokens(indentBias).size() > 0;
        if (displayDebug)
        {
            logger.debug("handleUnalignedTokens for column " + getName() + " of " + sequenceHead.getName());
        }
        ListIterator/*<AlignableToken>*/ li = getTokens(indentBias).listIterator();
        while (li.hasNext())
        {
            AlignableToken genericToken = (AlignableToken) li.next();
            final Line line = genericToken.getLine();
            if (line.isImmutable())
                continue;

            AlignableToken previousToken = genericToken.findPreviousNonBlankToken();
            int tabstop = 0;
            if (previousToken != null)
            {
                tabstop = previousToken.getLinePosition() + previousToken.getWidth();
                if (genericToken.needSpaceAfterPreviousToken(previousToken))
                {
                    tabstop++;
                }
            }
            genericToken.setLinePosition(tabstop);
        }
        this.tabstop = determineTabstop(this);
        calculateMaxWidth(false, indentBias);
        if (displayDebug)
        {
            logger.debug("setting column " + getName() + " of " + sequenceHead.getName() + " tabstop to " +
                    this.tabstop + ", width=" + this.maxWidth);
        }
    }

    /**
     * Some tokens have modified representations.  For example, some tokens are always preceded by a space;
     * modifiers can be rearranged and spaced to align with modifiers from other lines.
     */
    void calculateAlternateRepresentations(int indentBias)
    {
        // New idea: if the token belongs to an unaligned column and there is a preceding non-blank token on the line,
        // append this token to the preceding one (inserting a space if necessary to avoid combining identifiers) and set this token's
        // alternate representation to empty.  Purpose is to try to avoid needing complicated logic later to handle recalculation of
        // column widths that aren't correct at the time they are processed because unaligned tokens from subsequent columns can slide
        // over into this the left column. -- This approach had the problem of causing subsequent columns to be shifted right one or even
        // two spaces; it seemed to be related to whether or not the combined tokens ended with an extra space (due to code style settings).
        // The whole algorithm is excessively complex; looking for something simpler.
        /**
         * If the token in this column begins with an alphanumeric and the previous non-blank token in this column ends
         * with an alphanumeric, add a space to the previous non-blank token.
         */
        if (setting.get() && setting.getCharacters() != 0)
        {
            return;
        }
        ListIterator/*<AlignableToken>*/ iterator = getTokenList(indentBias).listIterator();
        while (iterator.hasNext())
        {
            final AlignableToken token = (AlignableToken) iterator.next();
            final String tokenValue = token.getValue();
            if (tokenValue.length() == 0)
                continue;
            /** find previous token on this line. */
            AlignableToken previousToken = token.findPreviousNonBlankToken();

            /** check for alphanumeric fields following an alphanumeric field.
             *  Add logic to handle special case of annotation ("@NAME") following an identifier; i.e. treat @ as a valid identifier start.
             */
            if (/*!setting.get() && */ token.needSpaceAfterPreviousToken(previousToken))
            {
                previousToken.setAlternateRepresentation(previousToken.getValue() + ' ');
            }
//                if (setting.get())
//                {
//                    // if the token is in an aligned column, simply add a space to the previousToken to ensure separation of identifiers.
////                    previousToken.setAlternateRepresentation(alt.toString());
//                }
//                else
//                {
//                    // the token is not in an aligned column, so add the token to the previous token's representation and set the token's own
//                    // representation to the empty string.
//                    StringBuilder alt = new StringBuilder();
//                    alt.append(previousToken);
//                    if (token.needSpaceAfterPreviousToken(previousToken))
//                    {
//                        alt.append(" ");
//                    }
//                    alt.append(token);
//                    previousToken.setAlternateRepresentation(alt.toString());
//                    token.setAlternateRepresentation("");
//                    previousToken.setAppendSpace(token.isAppendSpace());
//                    token.setAppendSpace(false); // any extra padding space is transferred from this token to the previous token.
//                }
            else if (previousToken == null && tokenValue.charAt(0) == ' ' &&
                    !(this instanceof ModifierTokenColumn))// todo - probably don't need this second boolean term
            {
                /**
                 * trim leading space from any token which is first on the line.  E.g., " {"
                 * However, leave " :" alone since this operator will be aligned with " ?" on preceding line.
                 */
                if (token.getWidth() < 2 || tokenValue.charAt(1) != ':')
                {
                    token.setAlternateRepresentation(token.getValue().trim());
                }
            }
        }
        boolean sawLeadingSpace = false;
        boolean sawNonLeadingSpace = false;
        iterator = getTokenList(indentBias).listIterator();
        while (iterator.hasNext()) {
            AlignableToken token = (AlignableToken) iterator.next();
            if (token.getValue().equals(" }")) sawLeadingSpace = true;
            if (token.getValue().equals("}")) sawNonLeadingSpace = true;
        }
        if (sawLeadingSpace && sawNonLeadingSpace) {
            iterator = getTokenList(indentBias).listIterator();
            while (iterator.hasNext()) {
                AlignableToken token = (AlignableToken) iterator.next();
                if (token.getValue().equals("}")) {
                    token.setAlternateRepresentation(" }");
                }
            }
        }
    }

    public void clearTokens(Line except, int indentBias)
    {
        super.clearTokens(except, indentBias);
        if (except != null)
        {
            ListIterator/*<AlignableToken>*/ tokenList = getTokens(indentBias).listIterator();
            while (tokenList.hasNext())
            {
                AlignableToken token = (AlignableToken) tokenList.next();
                if (!token.getLine().isImmutable())
                {
                    tokenList.remove();
                }
            }
        }
        else
        {
            getTokens(indentBias).clear();
        }
    }

    public final boolean determineNodesToDump(int indentBias)
    {
        includeInDump = getTokens(indentBias).size() > 0;
        return includeInDump;
    }

    public final void dumpDetails(String prefix, int indentBias)
    {
        String abbr = abbreviated(prefix);
        if (getTokens(indentBias).size() > 0)
        {
            logger.debug(abbr + "    Tokens are: (size " + getTokens(indentBias).size() + ")");
            ListIterator/*<AlignableToken>*/ li = getTokens(indentBias).listIterator();
            int i = 1;
            while (li.hasNext())
            {
                AlignableToken alignableToken = (AlignableToken) li.next();
                logger.debug(abbr + "-" + i + ":" + alignableToken.getValue());
                i++;
            }
        }
    }

    public JPanel display(boolean reduceClutter)
    {
        SizedPanel panel = new SizedPanel();
        Constraints constraints = new Constraints(GridBagConstraints.NORTHWEST);
        final String title = "tokens:" + nodeType.getName() + " at " + tabstop + ", max width=" + maxWidth;
        ListIterator/*<List<AlignableToken>>*/ li = tokenLists.listIterator();
        int indentBias = 0;
        while (li.hasNext())
        {
            List tlist = (List/*<AlignableToken>*/) li.next();
            if (tlist != null && tlist.size() != 0) {
                SizedPanel tokenpanel = new SizedPanel();
                Constraints gbc = new Constraints(GridBagConstraints.NORTHWEST);
                ListIterator tli = tlist.listIterator();
                while (tli.hasNext()) {
                    AlignableToken token = (AlignableToken) tli.next();
                    JLabel tokenLabel = new JLabel("token:'" + token.getValue() + "', pos=" +
                                                   token.getLinePosition() + ", width=" + token.getWidth());
                    tokenLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    tokenpanel.add(tokenLabel, gbc.weightedNextCol());
                    gbc.newRow();
                }
                tokenpanel.setTitle("tokenList #" + indentBias + ", nTokens=" + tlist.size());
                panel.add(tokenpanel);
            }
            indentBias++;
        }
        panel.setTitle(title);
        return panel;
    }

}
