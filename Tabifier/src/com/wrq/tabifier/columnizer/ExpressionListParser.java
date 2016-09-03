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
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.wrq.tabifier.parse.*;
import com.wrq.tabifier.settings.TabifierSettings;

/**
 * Parses expression lists into the column tree.
 */
final class ExpressionListParser
        extends NestedParser
{
    private final TokenColumn openParend;
    private final ColumnChoice params;
    private final TokenColumn closeParend;
    private int paramNumber;
    private final int nestingLevel;
    private final char openChar;
    private final char closeChar;

    public ExpressionListParser(final TokenColumn openParend,
                                final ColumnChoice params,
                                final TokenColumn closeParend,
                                final CodeStyleSettings codeStyleSettings,
                                final TabifierSettings settings,
                                final NestedParser parser,
                                final int nestingLevel,
                                final char openChar,
                                final char closeChar)
    {
        super(parser, codeStyleSettings, settings, parser.tab_size);
        this.openParend = openParend;
        this.params = params;
        this.closeParend = closeParend;
        this.nestingLevel = nestingLevel;
        this.openChar = openChar;
        this.closeChar = closeChar;
    }

    public final void visitExpressionList(final PsiExpressionList psiExpressionList)
    {
        paramNumber = 1;
        int expressionListIndex = 0;
        boolean sawNoParameters = true;
        boolean haveDoneContinuationLineIndent = false;
        final ColumnSequence paramsColSeq = params.findOrAppend(ColumnSequenceNodeType.PARAMLIST);

        for (int i = 0; i < psiExpressionList.getChildren().length; i++)
        {
            final PsiElement child = psiExpressionList.getChildren()[i];
            if (sawNoParameters && isSawNewline()) {
                // handleNewline() call below will set continuation indent.
                haveDoneContinuationLineIndent = true;
            }
            if (sawNoParameters) handleNewline(2);
            else if (!haveDoneContinuationLineIndent && !settings.align_initial_params.get())
            {
                // if parameters are not being aligned, then all parameters after the first newline are indented
                // on a continuation line -- unless the first parameter was already done on a continuation line.
                if (isSawNewline()) {
                    haveDoneContinuationLineIndent = true;
                    handleNewline(2);
                }
            }
            if (psiExpressionList.getExpressions().length > expressionListIndex &&
                    psiExpressionList.getExpressions()[expressionListIndex] == child)
            {
                ColumnChoice param = getParamColumn(paramsColSeq);
                sawNoParameters = false;
                final ExpressionParser ep = new ExpressionParser(param, codeStyleSettings, settings, this, nestingLevel);
                child.accept(ep);
                expressionListIndex++;
                continue;
            }
            if (child instanceof PsiJavaToken)
            {
                final PsiJavaToken token = (PsiJavaToken) child;
                final boolean has_params = psiExpressionList.getExpressions().length > 0;
                if (token.getText().charAt(0) == openChar)
                {
                    final AlignableToken t = new AlignableToken(
                            child,
                            codeStyleSettings.SPACE_BEFORE_METHOD_CALL_PARENTHESES,
                            codeStyleSettings.SPACE_WITHIN_METHOD_CALL_PARENTHESES && has_params);
                    addToken(t, openParend);
                    continue;
                }
                if (token.getText().charAt(0) == closeChar)
                {
                    final AlignableToken t = new AlignableToken(
                            child,
                            has_params ? codeStyleSettings.SPACE_WITHIN_METHOD_CALL_PARENTHESES
                    : settings.spaceBetweenEmptyParentheses.get(),
                            false);
                    addToken(t, closeParend);
                    continue;
                }
                if (token.getTokenType() == JavaTokenType.COMMA)
                {
                    final TokenColumn tc = (TokenColumn) paramsColSeq.findNth(AlignableColumnNodeType.COMMAS, paramNumber);
                    if (tc != null)
                    {
                        addComma(child, tc);
                        paramNumber++;
                        continue;
                    }
                }
            }
            checkForNewline(child);
            if (child instanceof PsiWhiteSpace)
            {
                if (child.getText().indexOf('\n') >= 0)
                {
                    paramNumber = 1;
                }
            }
            child.accept(this);
        }
        undoIndentBias();
    }

    /**
     * Utility class to produce the appropriate number of parameter columns in the column sequence.
     * The first parameter column always has a node type of PropagatingAlignableColumnNodeType, because the
     * settings.align_param alignment must propagate to whatever the parameter may be.  (Normally, the only alignment
     * that matters is the alignment of TokenColumns indicated by their respective settings.  In this case, however,
     * the alignment is set on a parent ColumnChoice and is propagated to TokenColumns.)
     * @param paramsColSeq the column sequence which should contain the param ColumnChoice objects
     * @return ColumnChoice object corresponding to Nth parameter, with correct node type for propagating parameter.
     */
    private ColumnChoice getParamColumn(final ColumnSequence paramsColSeq)
    {
        ColumnChoice param = null;

        while (param == null)
        {
            param = (ColumnChoice) paramsColSeq.findNth(PropagatingAlignableColumnNodeType.PROPAGATING_PARAM, paramNumber);
            if (param == null)
            {
                paramsColSeq.appendChoiceColumn(
                        paramNumber == 1 ? settings.align_initial_params
                                         : settings.align_subsequent_params      ,
                        PropagatingAlignableColumnNodeType.PROPAGATING_PARAM       );
                paramsColSeq.appendTokenColumn (
                        paramNumber == 1 ? settings.align_initial_param_commas
                                         : settings.align_subsequent_param_commas,
                        AlignableColumnNodeType.COMMAS                             );
            }
        }
        return param;
    }

    public final void visitArrayInitializerExpression(final PsiArrayInitializerExpression psiArrayInitializerExpression)
    {
        paramNumber = 1;
        int expressionListIndex = 0;
        boolean sawNoParameters = true;

        for (int i = 0; i < psiArrayInitializerExpression.getChildren().length; i++)
        {
            final PsiElement child = psiArrayInitializerExpression.getChildren()[i];
            final ColumnSequence paramsColSeq = params.findOrAppend(ColumnSequenceNodeType.PARAMLIST);
            if (psiArrayInitializerExpression.getInitializers().length > expressionListIndex &&
                    psiArrayInitializerExpression.getInitializers()[expressionListIndex] == child)
            {
                ColumnChoice param = getParamColumn(paramsColSeq);
                if (sawNoParameters) handleNewline(2);
                sawNoParameters = false;
                final ExpressionParser ep = new ExpressionParser(param, codeStyleSettings, settings, this, nestingLevel);
                child.accept(ep);
                expressionListIndex++;
                continue;
            }
            if (child instanceof PsiJavaToken)
            {
                final PsiJavaToken token = (PsiJavaToken) child;
                final boolean has_params = psiArrayInitializerExpression.getInitializers().length > 0;
                if (token.getText().charAt(0) == openChar)
                {
                    final AlignableToken t = new AlignableToken(
                            child,
                            settings.force_space_before_array_initializer.get(),
                            settings.force_space_within_array_initializer.get() && has_params);
                    addToken(t, openParend);
                    continue;
                }
                if (token.getText().charAt(0) == closeChar)
                {
                    final AlignableToken t = new AlignableToken(
                            child,
                            has_params ? settings.force_space_within_array_initializer.get()
                                               : settings.spaceBetweenEmptyParentheses.get(),
                            false);
                    addToken(t, closeParend);
                    continue;
                }
                if (token.getTokenType() == JavaTokenType.COMMA)
                {
                    final TokenColumn tc = (TokenColumn) paramsColSeq.findNth(AlignableColumnNodeType.COMMAS, paramNumber);
                    if (tc != null)
                    {
                        addComma(child, tc);
                        paramNumber++;
                        continue;
                    }
                }
            }
            checkForNewline(child);
            if (child instanceof PsiWhiteSpace)
            {
                if (child.getText().indexOf('\n') >= 0)
                {
                    paramNumber = 1;
                }
            }
            child.accept(this);
        }
        undoIndentBias();
    }
}