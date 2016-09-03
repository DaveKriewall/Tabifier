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
package com.wrq.tabifier.columnizer;

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.wrq.tabifier.formatter.ColumnNodeTabifier;
import com.wrq.tabifier.parse.*;
import com.wrq.tabifier.parse.ColumnSequenceNodeType;
import com.wrq.tabifier.settings.TabifierSettings;
import com.wrq.tabifier.tabifier;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Parses a PsiFile and assigns tokens to specific columns for subsequent alignment.
 */
public final class DocumentParser
        extends NestedParser
{
    private static final Logger            logger             = Logger.getLogger("com.wrq.tabifier.parse.DocumentParser");
    private final        ColumnSequence    baseSeq;
    private final        ColumnChoice      classChoice;
    private final        int               startOffset;
    private final        int               endOffset;
    private final        CodeStyleSettings codeStyleSettings;

    private              Line              currentLine;
    private              int               indentLevel        = 0;
    private              int               indentBias         = 0;

    private              boolean           scheduleAlignment;
    private              boolean           firstTokenSeen     = false;  // used to reset scheduleAlignment to false when we start seeing tokens.
    private final        boolean           use_tab_char;
    private final        boolean           smart_tabs;
    private final        int               indent;
    private final        LineGroup         lineGroup;

    private              TokenColumn       myTrailingComments;
//    private final ProgressBar progressBar;

    /*
     * <BASE_SEQ>: -- <PROGRAM> -- <TRAILING COMMENTS>
     *                    |
     *                <UNKNOWN_TOKEN_SEQ>: -- <{START_OF_COLUMN} - TokenColumn used for unknown tokens>
     */

    public DocumentParser(final int               startOffset,
                          final int               endOffset,
                          final CodeStyleSettings codeStyleSettings,
                          final TabifierSettings  settings,
                          final int               tab_size,
//            final ProgressBar progressBar,
                                boolean           use_tab_char,
                                boolean           smart_tabs,
                                int               indent            )
    {
        super(null, codeStyleSettings, settings, tab_size);
        this.startOffset       = startOffset;
        this.endOffset         = endOffset;
        this.codeStyleSettings = codeStyleSettings;
        this.baseSeq           = new ColumnSequence(ColumnSequenceNodeType.BASE_SEQ, null, tab_size, settings);
        classChoice            = baseSeq.appendChoiceColumn(settings.start_of_column_sequence, AlignableColumnNodeType.PROGRAM);
        final ColumnSequence unknownTokenSeq = classChoice.findOrAppend(ColumnSequenceNodeType.UNKNOWN_TOKEN_SEQ);
        unknownTokenSeq.appendTokenColumn(settings.start_of_column_sequence, AlignableColumnNodeType.START_OF_COLUMN);
        lineGroup = new LineGroup(this, settings);
        LineGroup.reset();
//        this.progressBar = progressBar;
        this.use_tab_char = use_tab_char;
        this.smart_tabs   = smart_tabs;
        this.indent       = indent;
    }

    public final void visitReferenceExpression(final PsiReferenceExpression psiReferenceExpression)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Routine to determine if the psiElement overlaps the region to be reformatted.
     * 
     * @param psiElement @return true if element is within the selected region to be tabified.
     */
    private boolean elementInRange(final PsiElement psiElement)
    {
        final int elementStart = psiElement.getTextRange().getStartOffset();
        final int elementEnd   = psiElement.getTextRange().getEndOffset();
        return(tabifier.seeingTokensInRange =!(elementEnd <startOffset ||elementStart >endOffset));
    }

    protected void adjustIndentBias(final int adjustment)
    {
        if (adjustment != 0) {
            indentBias += adjustment;
            if (currentLine != null) {
                if (indentBias > currentLine.getIndentBias()) {
                    currentLine.setIndentBias(indentBias);
                }
            }
            logger.debug("adjustIndentBias by " + adjustment + ", indentBias=" + indentBias);
        }
    }

    /**
     * add a token to the current line and given TokenColumn.
     */
    final AlignableToken addToken(final PsiElement element, final TokenColumn tokenColumn)
    {
        return addToken(new AlignableToken(element), tokenColumn);
    }

    final AlignableToken addToken(final AlignableToken token, final TokenColumn tokenColumn)
    {
        return addToken(token, tokenColumn, false);
    }

    private AlignableToken addToken(final AlignableToken token,
                                    final TokenColumn    tokenColumn,
                                    final boolean        noMultilineWarning)
    {
        final PsiElement element = token.getElement();
        if (element.getText().length() != 0 && elementInRange(element)) {
            if (!firstTokenSeen) {
                firstTokenSeen = true;
                logger.debug("saw first token; resetting scheduleAlignment which was " + scheduleAlignment);
                scheduleAlignment = false;
            }
            if (currentLine == null) {
                currentLine = new Line();
                lineGroup.addLine(currentLine);
                currentLine.setIndentLevel(indentLevel);
                currentLine.setIndentBias (indentBias );
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "addToken: create new line, indentLevel=" + indentLevel + ", indentBias=" + indentBias
                                                                                                                  );
                }
            }
            currentLine.newToken(token);
            if (tokenColumn != null) {
                tokenColumn.addToken(token);
            }
            currentLine.updateBlankAndUnalignedFlags(token);

            if (indentBias > token.getLine().getIndentBias()) {
                throw new IllegalStateException(
                        "global indentBias " +
                        indentBias +
                        " doesn't match line's indentBias " + token.getLine().getIndentBias()
                );
            }
            if (!noMultilineWarning                                                   &&
                token.getValue().indexOf('\n') >= 0                                   &&
                !(element instanceof PsiWhiteSpace && token.getValue().length() == 1)   ) {
                logger.warn(
                        "addToken: added a token including a newline which "   +
                        "was not whitespace (i.e. embedded in a larger token)"
                                                                                );
                logger.warn("line was:"  + currentLine.toString());
                logger.warn("token was:" + element.getText()     );
            }

            if (logger.isDebugEnabled()) {
                logger.debug(
                        "addToken  ("                                                                  +
                        token.getValue().replaceAll("\n", "<NEWLINE>")                                 +
                        ")"                                                                            +
                        (indentBias > 0               ? " INDENTED " + indentBias                : "") +
                        ", offset ["                                                                   +
                        element.getTextRange().getStartOffset()                                        +
                        ","                                                                            +
                        element.getTextRange().getEndOffset()                                          +
                        "]"                                                                            +
                        (token.getElementOffset() > 0 ? " (element Offset=" +
                                                        token.getElementOffset() +
                                                        " for " + token.getElementLength() + ")" : "") +
                        ":\""                                                                          +
                        element.toString().replaceAll("\n", "<NEWLINE>")                               +
                        "\""                                                                           +
                        (tokenColumn != null          ? ", column=" + tokenColumn.toString()     : "")
                                                                                                        );
            }
        }
        return token;
    }

    public final void visitClass(final PsiClass psiClass)
    {
        final ClassParser cp = new ClassParser(classChoice, codeStyleSettings, settings, this);
        myTrailingComments = cp.getTrailingComments();
        psiClass.accept(cp);
    }

    /**
     * Determine if this comment is a trailing comment or not.  If so, drop it in the trailing comment column;
     * otherwise, treat it as unknown (call visitComment).
     *
     * @param comment
     */
    void handleComment(PsiComment comment)
    {
        if (currentLine == null) {
            visitComment(comment);
            return ;
        }
        // in order to be considered a trailing comment, the following criteria apply:
        // comment may not be a PsiDocComment.
        // comment must be contained on a single line.
        // comment must not be the first non-blank token on the line, unless the previous line ended with
        //     a trailing comment.
        // comment must be the last token on the line.
        // comment must not follow a right brace.
        //
        if (!(comment instanceof PsiDocComment) &&
            comment.getText().indexOf('\n') < 0   )
        {
            // search for anything other than whitespace before this comment on this line.
            boolean firstTextOnLine = true;
            boolean sawRBrace       = false;
            if (currentLine != null)
            {
                ListIterator li = currentLine.getTokens().listIterator();
                while (li.hasNext()) {
                    AlignableToken token = (AlignableToken) li.next();
                    if (!token.isWhiteSpace()) {
                        if (token.getValue().equals("}")) {
                            sawRBrace = true;
                            break; // treat as first text on line - will remove it from consideration as trailing comment
                        }
                        firstTextOnLine = false;
                        break;
                    }
                }
            }
            boolean    lastTextOnLine = false;
            PsiElement e              = comment;
            while (e != null && e.getNextSibling() == null) {
                e = e.getParent();
            }
            if (e != null) {
                e = e.getNextSibling();
                if (e instanceof PsiWhiteSpace && e.getText().indexOf('\n') == 0) {
                    lastTextOnLine = true;
                }
            }
            if (firstTextOnLine && !sawRBrace) {
                // if line immediately preceding this has final token in trailing comment column, and
                // this comment is indented from the preceding line's first token's position, treat this also
                // as a trailing comment.
                int thisLineIndent = 0;
                {
                    AlignableToken t = (AlignableToken) currentLine.getTokens().get(0);
                    if (t.isWhiteSpace()) {
                        thisLineIndent = t.getWidth();
                    }
                }
                int  lastLineIndent = 0;
                Line last           = lineGroup.getLastLine();
                if (last != null) {
                    {
                        AlignableToken t = (AlignableToken) last.getTokens().get(0);
                        if (t.isWhiteSpace()) {
                            lastLineIndent = t.getWidth();
                        }
                    }
                    ListIterator li = last.getTokens().listIterator(last.getTokens().size());
                    while (li.hasPrevious()) {
                        AlignableToken t = (AlignableToken) li.previous();
                        if (t.isWhiteSpace()) {
                            continue;
                        }
                        if (t.getColumn()  == myTrailingComments &&
                            lastLineIndent != thisLineIndent       ) {
                            firstTextOnLine = false;
                        }
                        break;
                    }
                }
            }
            if (!firstTextOnLine && lastTextOnLine) {
                addToken(comment, myTrailingComments);
                return ;
            }
        }
        visitComment(comment);
    }

    public void visitComment(final PsiComment psiComment)
    {
        if (psiComment.getText().indexOf('\n') < 0) {
            addToken(psiComment, null);
        }
        else {
            String s       = psiComment.getText();
            int    ioffset = 0;
            while (s.length() > 0) {
                int index = s.indexOf('\n');
                if (index == 0) {
                    addToken(new AlignableToken(psiComment, ioffset, 1), null, true);
                    s = s.substring(1);
                    ioffset++;
                    continue;
                }
                if (index < 0) {
                    index = s.length();
                }
                if (index > 0) {
                    // have leading characters.  Separate any leading whitespace and submit as a separate token.
                    if (s.length() > 0) {
                        addToken(new AlignableToken(psiComment, ioffset, index), null);
                        s       =  s.substring(index);
                        ioffset += index;
                    }
                }
            }
        }
    }

    public final void visitElement(final PsiElement psiElement)
    {
        if (elementInRange(psiElement)) {
            super.visitElement(psiElement);
            if (psiElement.getChildren().length == 0) {
                addToken(psiElement, null);
            }
        }
    }

    public final void visitJavaFile(final PsiJavaFile psiJavaFile)
    {
        super.visitJavaFile(psiJavaFile);
        alignColumns();
    }

    public void visitWhiteSpace(final PsiWhiteSpace psiWhiteSpace)
    {
        if (elementInRange(psiWhiteSpace)) {
            /**
             * utility function to break strings apart when newline characters are encountered. Each string is added
             * as a separate token to the token list. To the extent that the element is not included in the selection to
             * be tabified, it will be trimmed.
             *
             * @param element             PsiWhiteSpace element whose text is to be added as a whitespace
             *                            token.
             */
            String s       = psiWhiteSpace.getText();
            int    ioffset = 0;
            int    length  = s.length();
            if (psiWhiteSpace.getTextRange().getStartOffset() < startOffset) {
                /**
                 * whitespace begins before the selection to be tabified. Drop the appropriate
                 * number of characters.
                 */
                final int drop = startOffset - psiWhiteSpace.getTextRange().getStartOffset();
                logger.debug(
                        "trimToSelection: dropping "      +
                        drop                              +
                        " leading characters from "       +
                        PsiTreeUtil.elname(psiWhiteSpace) +
                        " because startOffset is < "      +
                        startOffset
                                                           );
                ioffset += drop;
                length  -= drop;
                s       =  s.substring(drop);
            }
            if (psiWhiteSpace.getTextRange().getEndOffset() > endOffset) {
                final int endIndex = endOffset - psiWhiteSpace.getTextRange().getStartOffset();
                if (endIndex <= 0) {
                    logger.debug(
                            "trimToSelection: discard unselected token for " +
                            PsiTreeUtil.elname(psiWhiteSpace)
                                                                              );
                    s = "";
                }
                else {
                    logger.debug(
                            "trimToSelection: truncate token to " +
                            endIndex                              +
                            " chars for element "                 +
                            PsiTreeUtil.elname(psiWhiteSpace)     +
                            " because endOffset > "               + endOffset
                                                                             );
                    s = s.substring(0, endIndex);
                }
            }
            while (length > 0) {
                final int index = s.indexOf('\n');
                if (index == 0) {
                    endOfLineProcessing(psiWhiteSpace, ioffset);

                    ioffset++;
                    length--;
                    s = s.substring(1);
                }
                else if (index == -1) {
                    // no newline seen.  add the rest of the whitespace.
                    if (ioffset == 0) {
                        // we haven't dropped any of the preceding characters from the token, so just take
                        // the whole thing.
                        addToken(psiWhiteSpace, null);
                    }
                    else {
                        addToken(new AlignableToken(psiWhiteSpace, ioffset, length), null);
                    }
                    break;
                }
                else {
                    addToken(new AlignableToken(psiWhiteSpace, ioffset, index), null);
                    ioffset += index;
                    length  -= index;
                    s       =  s.substring(index);
                }
            }
        }
    }

    private void endOfLineProcessing(final PsiElement psiWhiteSpace, final int ioffset)
    {
        /**
         * determine if empty line or untreated line should cause the currently accumulated group of lines to be
         * aligned, and a new group begun. Check to see if whitespace contains multiple newline characters -- if so,
         * there will be a blank line.
         */
        if (currentLine != null                       &&
            currentLine.isEntirelyUnaligned()         &&
            settings.delimit_by_non_blank_lines.get()   ) {
            scheduleAlignment("unaligned statement delimiter");
        }
        // if currentLine is null, it means we just started a new line and the only thing on the current line is going
        // to be a newline character -- hence, it is a blank line.
        if (settings.delimit_by_blank_lines.get()              &&
            (currentLine == null || currentLine.isBlankLine())   ) {
            scheduleAlignment("blank line delimiter");
        }

        addToken(new AlignableToken(psiWhiteSpace, ioffset, 1), null, true);
        currentLine = null;
        if (scheduleAlignment && typeCheckDepth == 0) {
            alignColumns();
        }
    }

    /**
     * Sometimes we want to add a multiline Psi element such as an expression, retaining spacing as much as possible
     * but handling the newline character as it normally is in visitWhiteSpace() above.
     * 
     * @param element 
     */
    protected AlignableToken addMultilineElement(final PsiElement element, final TokenColumn column)
    {
        AlignableToken firstToken = null;
        String         s          = element.getText();
        int            ioffset    = 0;
        while (s.length() > 0) {
            int            index = s.indexOf('\n');
            AlignableToken t;
            if (index == 0) {
                endOfLineProcessing(element, ioffset);
                s = s.substring(1);
                ioffset++;
                continue;
            }
            if (index < 0) {
                index = s.length();
            }
            if (index > 0) {
                // have leading characters.  Separate any leading whitespace and submit as a separate token.
                final int originalOffset = ioffset;
                while (s.length() > 0 && s.charAt(0) == ' ') {
                    ioffset++;
                    index--;
                    s = s.substring(1);
                }
                if (originalOffset != ioffset) {
                    t = new AlignableToken(element, originalOffset, ioffset - originalOffset);
                    if (firstToken == null) {
                        firstToken = t;
                    }
                    addToken(t, null);
                }
                if (s.length() > 0) {
                    t = new AlignableToken(element, ioffset, index);
                    if (firstToken == null) {
                        firstToken = t;
                    }
                    s       =  s.substring(index);
                    ioffset += index;
                    addToken(t, column);
                }
            }
        }
        return firstToken;
    }

    public final void visitJavaToken(final PsiJavaToken psiJavaToken)
    {
        if (psiJavaToken.getTokenType() == JavaTokenType.LBRACE)
        {
            /**
             * Keep track of indentation caused by braces. Ignore braces if inside an array initializer since this
             * doesn't affect indentation.
             */
                if (!hasArrayInitializerAncestor(psiJavaToken)) {
                    bumpIndentLevel(psiJavaToken);
                    scheduleAlignment("LBRACE encountered");
                    setStatementType(LineGroup.NONE);
                }
        }
        else if (psiJavaToken.getTokenType() == JavaTokenType.RBRACE)
        {
        if (!hasArrayInitializerAncestor(psiJavaToken)) {
                    reduceIndentLevel(psiJavaToken);
                    scheduleAlignment("RBRACE encountered");
                    setStatementType(LineGroup.NONE);
                }
        }
        super.visitJavaToken(psiJavaToken);
    }

    void bumpIndentLevel(final PsiJavaToken psiJavaToken)
    {
        indentLevel += getBlockIndentationLevelDelta(psiJavaToken);
    }

    void reduceIndentLevel(final PsiJavaToken psiJavaToken)
    {
        indentLevel -= getBlockIndentationLevelDelta(psiJavaToken);
        if (indentLevel < 0) {
            indentLevel = 0;
        }
        if (currentLine != null) {
            currentLine.setIndentLevel(indentLevel);
        }
    }

    private static boolean hasArrayInitializerAncestor(PsiElement e)
    {
        boolean ignore = false;
        do {
            if (e instanceof PsiArrayInitializerExpression) {
                ignore = true;
                break;
            }
            if (e instanceof PsiFile) {
                break;
            }
            e = e.getParent();
        }
        while (e != null);
        return ignore;
    }

    /**
     * switch statements cause a special indentation problem. If indent_case_from_switch is true, then essentially the
     * whole block is indented two levels (except for case labels). If false, the block is indented one level, and case
     * labels have the same indent level as the switch statement. So calculate the indent level increment for this block
     * (special casing for switch statements with indent_case_from_switch) and decrement by the same amount when
     * leaving.
     * 
     * @param psiJavaToken left/right brace just encountered which causes change in indentation level.
     */
    private int getBlockIndentationLevelDelta(final PsiJavaToken psiJavaToken)
    {
              int        indentLevelBump = 1;
        final PsiElement parent1;
        final PsiElement parent2;
        parent1 = psiJavaToken.getParent();
        if (parent1 instanceof PsiCodeBlock) {
            parent2 = parent1.getParent();
            if (parent2 instanceof PsiSwitchStatement && codeStyleSettings.INDENT_CASE_FROM_SWITCH) {
                indentLevelBump = 2;
            }
        }
        return indentLevelBump;
    }

    public final void visitSwitchLabelStatement(final PsiSwitchLabelStatement psiSwitchLabelStatement)
    {
        if (currentLine != null) {
            // decrement indentation for this line only, since it contains a switch label.
            currentLine.setIndentLevel(indentLevel - 1);
        }
        super.visitSwitchLabelStatement(psiSwitchLabelStatement);
    }

    public void scheduleAlignment(final String reason)
    {
        if (tabifier.seeingTokensInRange) {
            logger.debug("schedule column alignment: " + reason);
        }
        scheduleAlignment = true;
    }

    protected boolean isCurrentLineBlank()
    {
        return (currentLine == null ? true : currentLine.isBlankLine());
    }

    private void alignColumns()
    {
//        if (progressBar.isCancelled()) {
//            throw new CancelOperationException(); // exit back to action handler
//        }
        if (tabifier.seeingTokensInRange) {
            logger.debug("alignColumns");
        }
        final ArrayList<Line> linesToAlign = lineGroup.getLinesToAlign();
        final ColumnNodeAligner   cna          = new ColumnNodeAligner(baseSeq, linesToAlign);
        final ColumnNodeTabifier columnNodeTabifier = new ColumnNodeTabifier(baseSeq, linesToAlign);
        /**
         * indicate progress.  Since we've parsed the lines but have not yet tabified them, say that we're
         * halfway through the lines.
         */
//        if (linesToAlign.size() > 0)
//        {
//            int index = linesToAlign.size() >> 1;
//            final Line middleLine = linesToAlign.get(index);
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run()
//                {
//                    progressBar.setCurrent(middleLine.getEndOffset());
//                }
//            });
//        }
        if (linesToAlign.size() > 0) {
            final Line untouchable = lineGroup.getUngroupedCurrentLine();
            if (untouchable != null) {
                untouchable.setImmutable(true);
            }
            cna.align();
            for (int currentIndentBias = 0; currentIndentBias <= cna.getMaxIndentBias(); currentIndentBias++) {
                baseSeq.determineNodesToDump(currentIndentBias);
                baseSeq.dump(currentIndentBias);
            }
            for (Line line : linesToAlign)
            {
                LineGroup.reformatableLines.add(line);
                line.formatLine(use_tab_char, smart_tabs, tab_size, indent);
            }
            if (untouchable != null) {
                untouchable.setImmutable(false);
            }
        }
        /** now traverse the baseSeq, removing all tokens from tokenColumns,
         * except the current line if it was not included in the formatting.
         */
        for (int currentIndentBias = 0; currentIndentBias <= cna.getMaxIndentBias(); currentIndentBias++) {
            baseSeq.clearTokens(lineGroup.getUngroupedCurrentLine(), currentIndentBias);
        }
//        if (linesToAlign.size() > 0)
//        {
//            final Line lastLine = linesToAlign.get(linesToAlign.size() - 1);
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run()
//                {
//                    progressBar.setCurrent(lastLine.getEndOffset());
//                }
//            });
//        }
        lineGroup.resetLines();
        scheduleAlignment = false;
    }

    private int typeCheckDepth = 0;

    protected void suspendStatementTypeChecking()
    {
        typeCheckDepth++;
    }

    protected void resumeStatementTypeChecking()
    {
        typeCheckDepth--;
    }

    protected void setStatementType(final LineGroup.LineType type)
    {
        if (typeCheckDepth == 0) {
            lineGroup.setStatementType(type);
            if (lineGroup.immediateAlignmentIndicated()) {
                alignColumns();
            }
        }
    }
}