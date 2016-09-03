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
import com.wrq.tabifier.parse.*;
import com.wrq.tabifier.parse.ColumnSequenceNodeType;
import com.wrq.tabifier.settings.TabifierSettings;

/**
 * Parses expression statements into the column tree.
 */
final class ExpressionStatementParser
        extends NestedParser
{
    private final ColumnChoice exprChoice;

    public ExpressionStatementParser(final ColumnChoice      statementChoice,
                                     final CodeStyleSettings codeStyleSettings,
                                     final TabifierSettings  settings,
                                     final NestedParser      statementParser   )
    {
        super(statementParser, codeStyleSettings, settings, statementParser.tab_size);
        final ColumnSequence expr = statementChoice.findOrAppend(ColumnSequenceNodeType.EXPRESSION_STATEMENTS);
              ColumnChoice   cc   = expr.findColumnChoice(AlignableColumnNodeType.EXPRESSION_STATEMENT);
        if (cc == null)
        {
            cc = expr.appendChoiceColumn(settings.align_expression_statements,
                                         AlignableColumnNodeType.EXPRESSION_STATEMENT);
        }
        exprChoice = cc;
    }

    public final void visitExpressionStatement(final PsiExpressionStatement psiExpressionStatement)
    {
        for (int i = 0; i < psiExpressionStatement.getChildren().length; i++)
        {
            final PsiElement child = psiExpressionStatement.getChildren()[i];
            if (child instanceof PsiComment && !(child instanceof PsiDocComment))
            {
                handleComment((PsiComment) child);
                continue;

            }
            if (child instanceof PsiExpression)
            {
                setStatementType(child instanceof PsiMethodCallExpression ?
                                                                           LineGroup.METHOD_CALL :
                                                                                                  (child instanceof PsiAssignmentExpression) ?
                                                                                                                                              LineGroup.ASSIGNMENT :
                                                                                                                                                                    LineGroup.OTHER_EXPRESSION_STATEMENT);
                final ExpressionParser ep = new ExpressionParser(exprChoice, codeStyleSettings, settings, this, 0);
                child.accept(ep);
                continue;
            }
            child.accept(this);
        }

    }
}
