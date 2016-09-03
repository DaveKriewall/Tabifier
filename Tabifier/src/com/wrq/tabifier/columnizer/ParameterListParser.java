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
 * Parses method definition parameter lists into the column tree.
 */
final class ParameterListParser
        extends NestedParser
{
    private final TokenColumn    openParend;
    private final ColumnSequence params;
    private final TokenColumn    closeParend;
    private       int            paramNumber;

    public ParameterListParser(final ColumnSequence    parameterListSequence,
                               final CodeStyleSettings codeStyleSettings,
                               final TabifierSettings  settings,
                               final NestedParser      parser                )
    {
        super(parser, codeStyleSettings, settings, parser.tab_size);
        if (parameterListSequence.findTokenColumn(AlignableColumnNodeType.OPEN_PAREND) == null)
        {
            parameterListSequence.appendTokenColumn (settings.align_method_decl_open_parend,
                                                     AlignableColumnNodeType.OPEN_PAREND               );
            parameterListSequence.appendChoiceColumn(settings.align_method_declaration_initial_params,
                                                     AlignableColumnNodeType.PARAMS                    );
            parameterListSequence.appendTokenColumn (settings.align_method_decl_close_parend,
                                                     AlignableColumnNodeType.CLOSE_PAREND              );
        }
        this.openParend  = parameterListSequence.findTokenColumn(AlignableColumnNodeType.OPEN_PAREND );
        this.params      = (parameterListSequence.findColumnChoice(AlignableColumnNodeType.PARAMS)).
                findOrAppend(ColumnSequenceNodeType.PARAMLIST);
        this.closeParend = parameterListSequence.findTokenColumn(AlignableColumnNodeType.CLOSE_PAREND);
    }

    /**
     * Utility class to produce the appropriate number of parameter columns in the column sequence.
     * The first parameter column always has a node type of PropagatingAlignableColumnNodeType, because the
     * settings.align_param alignment must propagate to whatever the parameter may be.  (Normally, the only alignment
     * that matters is the alignment of TokenColumns indicated by their respective settings.  In this case, however,
     * the alignment is set on a parent ColumnChoice and is propagated to TokenColumns.)
     * @param paramsColSeq the column sequence which should contain the param ColumnChoice objects
     * @return ColumnChoice object corresponding to Nth parameter, with correct node type for parameter declaration
     */
    private ColumnChoice getParamColumn(final ColumnSequence paramsColSeq)
    {
        ColumnChoice param = null;

        while (param == null)
        {
            param = (ColumnChoice) paramsColSeq.findNth(PropagatingAlignableColumnNodeType.PROPAGATING_PARAM_DECL,
                                                        paramNumber                                               );
            if (param == null)
            {
                paramsColSeq.appendChoiceColumn(
                        paramNumber == 1 ? settings.align_method_declaration_initial_params
                                         : settings.align_method_declaration_subsequent_params,
                        PropagatingAlignableColumnNodeType.PROPAGATING_PARAM_DECL                    );
                paramsColSeq.appendTokenColumn (
                        paramNumber == 1 ? settings.align_method_declaration_initial_param_commas
                                         : settings.align_method_declaration_subsequent_param_commas,
                        AlignableColumnNodeType.COMMAS                                               );
            }
        }
        return param;
    }

    public final void visitParameterList(final PsiParameterList psiParameterList)
    {
        paramNumber = 1;
        int     parameterListIndex             = 0;
        boolean sawNoParameters                = true;
        boolean haveDoneContinuationLineIndent = false;
        for (int i = 0; i < psiParameterList.getChildren().length; i++)
        {
            final PsiElement child = psiParameterList.getChildren()[i];
            if (sawNoParameters && isSawNewline()) {
                // handleNewline() call below will set continuation indent.
                haveDoneContinuationLineIndent = true;
            }
            if      (sawNoParameters) handleNewline(2);
            else if (!haveDoneContinuationLineIndent && !settings.align_method_declaration_initial_params.get())
            {
                // if parameters are not being aligned, then all parameters after the first newline are indented
                // on a continuation line -- unless the first parameter was already done on a continuation line.
                if (isSawNewline()) {
                    haveDoneContinuationLineIndent = true;
                    handleNewline(2);
                }
            }
            if (psiParameterList.getParameters().length >  parameterListIndex                                   &&
                child                                   == psiParameterList.getParameters()[parameterListIndex]   )
            {
                ColumnChoice paramCol = getParamColumn(params);
                sawNoParameters = false;
                final VariableParser vp = new VariableParser(paramCol, codeStyleSettings, settings, this);
                child.accept(vp);
                parameterListIndex++;
                continue;
            }
            if (child instanceof PsiJavaToken)
            {
                final PsiJavaToken token      = (PsiJavaToken) child;
                final boolean      has_params = psiParameterList.getParameters().length > 0;
                if (token.getTokenType() == JavaTokenType.LPARENTH)
                {
                    final AlignableToken t = new AlignableToken(child,
                                                                codeStyleSettings.SPACE_BEFORE_METHOD_PARENTHESES,
                                                                codeStyleSettings.SPACE_WITHIN_METHOD_PARENTHESES && has_params);
                    addToken(t, openParend);
                    continue;
                }
                if (token.getTokenType() == JavaTokenType.RPARENTH)
                {
                    final AlignableToken t = new AlignableToken(child,
                                                                has_params ? codeStyleSettings.SPACE_WITHIN_METHOD_PARENTHESES
                                                                           : settings.spaceBetweenEmptyParentheses.get(),
                                                                false                                                         );
                    addToken(t, closeParend);
                    continue;
                }
                if (token.getTokenType() == JavaTokenType.COMMA)
                {
                    final TokenColumn tc = (TokenColumn) params.findNth(AlignableColumnNodeType.COMMAS, paramNumber);
                    if (tc != null)
                    {
                        addComma(child, tc);
                        paramNumber++;
                        continue;
                    }
                }
            }
            if (child instanceof PsiComment) {
                handleComment((PsiComment) child);
                continue;
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

