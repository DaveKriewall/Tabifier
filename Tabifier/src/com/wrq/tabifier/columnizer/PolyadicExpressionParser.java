/*
 * Copyright (c) 2003, 2010, 2015 Dave Kriewall
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
 * Parses polyadic expressions into the column tree.
 */
public final class PolyadicExpressionParser
        extends NestedParser
{
    private final ColumnChoice expressionChoice;
    private final int          nestingLevel;

    public PolyadicExpressionParser(final ColumnChoice expressionChoice,
                                    final CodeStyleSettings codeStyleSettings,
                                    final TabifierSettings settings,
                                    final NestedParser documentParser,
                                    final int nestingLevel)
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        this.expressionChoice  = expressionChoice;
        this.nestingLevel      = nestingLevel;
    }

    @Override
    public void visitPolyadicExpression(PsiPolyadicExpression psiPolyadicExpression)
    {
        final OperatorType   ot  = new OperatorType(psiPolyadicExpression.getOperationTokenType(), settings);

        final ColumnSequence seq = expressionChoice.findOrAppend(ot.expressionType);
        // Use columnIndex to locate the appropriate ColumnChoice or TokenColumn.
        // 1 indicates we're handling the first column (assigned to the first term of the expression);
        // 2 indicates we're handling the first operator or the subsequent term; etc.
        // Dynamically add more ColumnChoice or TokenColumn objects to the column sequence as needed to support as many terms as exist on a given line.
        int columnIndex = 1;
        // Use operandIndex to track the operand number, where 0 is the first operand, 1 is the operand following the first operator, etc.
        int operandIndex = 0;
        boolean expectingOperator = false;
        for (PsiElement child : psiPolyadicExpression.getChildren())
        {
            if (child == psiPolyadicExpression.getOperands()[operandIndex])
            {
                ColumnChoice column = (ColumnChoice) seq.findNth(AlignableColumnNodeType.EXPR_LOPERAND, columnIndex);
                if (column == null)
                {
                    column = seq.appendChoiceColumn(settings.align_terms, AlignableColumnNodeType.EXPR_LOPERAND);
                }
                final ExpressionParser ep = new ExpressionParser(column, codeStyleSettings, settings, this, nestingLevel + 1);
                child.accept(ep);
                columnIndex++;
                operandIndex++;
                expectingOperator = true;
                continue;
            }
            if (child == psiPolyadicExpression.getTokenBeforeOperand(psiPolyadicExpression.getOperands()[operandIndex]))
            {
                TokenColumn  operator = (TokenColumn) seq.findNth(AlignableColumnNodeType.OPERATOR, columnIndex - 1);
                if (operator == null)
                {
                    operator = seq.appendTokenColumn(ot.setting, AlignableColumnNodeType.OPERATOR);
                }
                final AlignableToken token = AlignableToken.createToken((PsiJavaToken) child, codeStyleSettings, settings);
                addToken(token, operator);
                expectingOperator = false;
                continue;
            }
            if (child instanceof PsiWhiteSpace)
            {
                if (child.getText().indexOf('\n') >= 0)
                {
                    // if not expecting an operator, set the column index to one so that the term aligns with the first term of the expression;
                    // otherwise, set the column index to two so that the operator aligns with the first operator of the polyadic expression.
                    columnIndex = expectingOperator ? 2 : 1;
                }
            }
            child.accept(this);
        }
    }
}
