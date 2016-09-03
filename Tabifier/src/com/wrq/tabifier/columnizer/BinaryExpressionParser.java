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
import org.jetbrains.annotations.NotNull;

/**
 * Parses binary expressions into the column tree.  Although binary expressions are represented in the Psi tree
 * recursively, flatten these out when a sequence of terms such as "a + b + c + d" occurs so that all the terms and
 * operators align.
 */
public final class BinaryExpressionParser
        extends NestedParser
{
    private final ColumnChoice expressionChoice;
    private final int          nestingLevel;
    private       boolean      expectingLOperand;

    public BinaryExpressionParser(final ColumnChoice      expressionChoice ,
                                  final CodeStyleSettings codeStyleSettings,
                                  final TabifierSettings  settings         ,
                                  final NestedParser      documentParser   ,
                                  final int               nestingLevel      )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        this.expressionChoice  = expressionChoice;
        this.nestingLevel      = nestingLevel;
        this.expectingLOperand = true;
    }

    public final void visitBinaryExpression(final PsiBinaryExpression psiBinaryExpression)
    {
        final OperatorType   ot  = new OperatorType(psiBinaryExpression.getOperationSign().getTokenType(), settings);

        final ColumnSequence seq = expressionChoice.findOrAppend(ot.expressionType);
        // share columns with polyadic expressions; use two L_OPERAND columns instead of one L_OPERAND and one R_OPERAND.
        if (seq.findNth(AlignableColumnNodeType.EXPR_LOPERAND, 1) == null)
        {
            seq.appendChoiceColumn(settings.align_terms, AlignableColumnNodeType.EXPR_LOPERAND);
        }
        if (seq.findNth(AlignableColumnNodeType.OPERATOR, 1) == null)
        {
            seq.appendTokenColumn (ot.setting          , AlignableColumnNodeType.OPERATOR     );
        }
        if (seq.findNth(AlignableColumnNodeType.EXPR_LOPERAND, 2) == null)
        {
            seq.appendChoiceColumn(settings.align_terms, AlignableColumnNodeType.EXPR_LOPERAND);
        }

        @NotNull final ColumnChoice LOperand = (ColumnChoice) seq.findNth(AlignableColumnNodeType.EXPR_LOPERAND, 1);
        @NotNull final TokenColumn  operator = seq.findTokenColumn(AlignableColumnNodeType.OPERATOR);
        @NotNull final ColumnChoice ROperand = (ColumnChoice) seq.findNth(AlignableColumnNodeType.EXPR_LOPERAND, 2);
        for (int i = 0; i < psiBinaryExpression.getChildren().length; i++)
        {
            final PsiElement child = psiBinaryExpression.getChildren()[i];
            if (child == psiBinaryExpression.getLOperand())
            {
                if (child instanceof PsiBinaryExpression)
                {
                    /** Keep nested left-hand binary expressions together; they will share operators of the same
                     * precedence.  Do this only for expressions with the same type (logical or arithmetic).
                     */
                    final PsiBinaryExpression newExp  = (PsiBinaryExpression) child;
                    final OperatorType        newType = new OperatorType(newExp.getOperationSign().getTokenType(), settings);
                    if (newType.expressionType == ot.expressionType)
                    {
                        // do this only if there's a newline after the operator.  This handles expressions in
                        // the format:
                        //   a +
                        //   b +
                        //   c;
                        // if no newline exists, we have
                        //   a + b + c    or
                        //   a + b +
                        //   c
                        //
                        // and in either case, 'a + b' is the entire left operand and 'c' is the right operand.
                        PsiElement e                 = newExp.getOperationSign();
                        boolean    visitedExpression = false;
                        while ((e = e.getNextSibling()) != null)
                        {
                            if (e == newExp.getROperand()                                   ) break;
                            if (e instanceof PsiWhiteSpace && e.getText().indexOf('\n') >= 0)
                            {
                                visitBinaryExpression((PsiBinaryExpression) child);
                                visitedExpression = true;
                                break;
                            }
                        }
                        if (visitedExpression)
                        {
                            continue;
                        }
                    }
                }
                final ExpressionParser ep = new ExpressionParser(LOperand, codeStyleSettings, settings, this, nestingLevel + 1);
                child.accept(ep);
                continue;
            }
            if (child == psiBinaryExpression.getOperationSign())
            {
                final AlignableToken token = AlignableToken.createToken((PsiJavaToken) child, codeStyleSettings, settings);
                addToken(token, operator);
                expectingLOperand = false;
                continue;
            }
            if (child == psiBinaryExpression.getROperand())
            {
                final ColumnChoice     myOperand = expectingLOperand ? LOperand : ROperand;
                final ExpressionParser ep        = new ExpressionParser(myOperand       , codeStyleSettings, settings, this,
                                                                        nestingLevel + 1                                    );
                child.accept(ep);
                continue;
            }
            if (child instanceof PsiWhiteSpace)
            {
                if (child.getText().indexOf('\n') >= 0)
                {
                    expectingLOperand = true;
                }
            }
            child.accept(this);
        }
    }
}
