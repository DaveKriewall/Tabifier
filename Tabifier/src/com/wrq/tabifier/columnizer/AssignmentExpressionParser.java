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

import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.wrq.tabifier.parse.*;
import com.wrq.tabifier.parse.ColumnSequenceNodeType;
import com.wrq.tabifier.settings.TabifierSettings;

/**
 * Parses assignment expressions into column tree.
 */
public final class AssignmentExpressionParser extends NestedParser
{
    private final ColumnSequence assignmentExprSequence;
    private final TokenColumn    varnames;
    private final TokenColumn    operators;
    private final ColumnChoice   expr;

    public AssignmentExpressionParser(final ColumnChoice      statementChoice  ,
                                      final CodeStyleSettings codeStyleSettings,
                                      final TabifierSettings  settings         ,
                                      final NestedParser      documentParser    )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        assignmentExprSequence = statementChoice.findOrAppend(ColumnSequenceNodeType.ASSIGNMENT_STATEMENTS);
        if (assignmentExprSequence.findTokenColumn(AlignableColumnNodeType.ASSIGNMENT_VARNAMES) == null)
        {
            assignmentExprSequence.appendTokenColumn (settings.align_variable_names                 ,
                                                      AlignableColumnNodeType.ASSIGNMENT_VARNAMES    );
            assignmentExprSequence.appendTokenColumn (settings.align_assignment_operators           ,
                                                      AlignableColumnNodeType.ASSIGNMENT_OPERATORS   );
            assignmentExprSequence.appendChoiceColumn(settings.align_terms                          ,
                                                      AlignableColumnNodeType.ASSIGNMENT_EXPRESSIONS );
        }
        varnames  = assignmentExprSequence.findTokenColumn(AlignableColumnNodeType.ASSIGNMENT_VARNAMES   );
        operators = assignmentExprSequence.findTokenColumn(AlignableColumnNodeType.ASSIGNMENT_OPERATORS  );
        expr      = assignmentExprSequence.findColumnChoice(AlignableColumnNodeType.ASSIGNMENT_EXPRESSIONS);
    }


    public final void visitAssignmentExpression(final PsiAssignmentExpression psiAssignmentExpression)
    {
        PsiElement child;
        setStatementType(LineGroup.ASSIGNMENT);
        for (int i = 0; i < psiAssignmentExpression.getChildren().length; i++)
        {
            child = psiAssignmentExpression.getChildren()[i];
            if (child == psiAssignmentExpression.getLExpression())
            {
                addToken(child, varnames);
                continue;
            }
            if (child == psiAssignmentExpression.getOperationSign())
            {
                handleNewline(2);
                addToken(new AlignableToken(child                                              ,
                                            codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS &&
                        (!settings.no_space_before_assignment_operators.get()),
                                            codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS ),
                         operators                                                                );
                continue;
            }
            if (child == psiAssignmentExpression.getRExpression())
            {
                handleNewline(2);
                final ExpressionParser ep = new ExpressionParser(expr, codeStyleSettings, settings, this, 0);
                child.accept(ep);
                continue;
            }
            checkForNewline(child);
            child.accept(this);
        }
        undoIndentBias();
    }

    public final void visitReferenceExpression(final PsiReferenceExpression psiReferenceExpression)
    {
        throw new UnsupportedOperationException("StatementParser doesn't handle visitReferenceExpression");
    }

}
