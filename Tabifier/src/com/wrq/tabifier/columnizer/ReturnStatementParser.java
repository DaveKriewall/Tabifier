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
import com.intellij.psi.javadoc.PsiDocComment;
import com.wrq.tabifier.parse.*;
import com.wrq.tabifier.settings.TabifierSettings;

/**
 * Parses return statements into the column tree.
 */
final class ReturnStatementParser
        extends NestedParser
{
    private final TokenColumn  returnKeyword;
    private final ColumnChoice returnExpression;
    private final TokenColumn  semicolons;

    /*
     *  parent
     *    |
     *  RETURN_STATEMENT: -- RETURN_KEYWORD TERM(return expression) SEMICOLON
     *
     */
    public ReturnStatementParser(final ColumnChoice      currentCodeStatementColumnNode,
                                 final CodeStyleSettings codeStyleSettings,
                                 final TabifierSettings  settings,
                                 final NestedParser      documentParser                 )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        final ColumnSequence seq = currentCodeStatementColumnNode.findOrAppend(ColumnSequenceNodeType.RETURN_STATEMENT);
        if (seq.findTokenColumn(AlignableColumnNodeType.RETURN_KEYWORD) == null)
        {
            seq.appendTokenColumn (settings.align_if_keywords, AlignableColumnNodeType.RETURN_KEYWORD     );
            seq.appendChoiceColumn(settings.align_terms, AlignableColumnNodeType.TERM                     );
            seq.appendTokenColumn (settings.align_semicolons, AlignableColumnNodeType.STATEMENT_SEMICOLONS);
        }
        returnKeyword    = seq.findTokenColumn(AlignableColumnNodeType.RETURN_KEYWORD      );
        returnExpression = seq.findColumnChoice(AlignableColumnNodeType.TERM                );
        semicolons       = seq.findTokenColumn(AlignableColumnNodeType.STATEMENT_SEMICOLONS);
    }

    public final void visitReturnStatement(final PsiReturnStatement psiReturnStatement)
    {
        for (int i = 0; i < psiReturnStatement.getChildren().length; i++)
        {
            final PsiElement child = psiReturnStatement.getChildren()[i];
            if (i == 0)
            {
                // this is the 'return' keyword.  Force a space afterward since there's no
                // punctuation between it and its expression.
                addToken(new AlignableToken(child, false, true), returnKeyword);
                continue;
            }
            if (child instanceof PsiComment && !(child instanceof PsiDocComment))
            {
                handleComment((PsiComment) child);
                continue;

            }
            if (child == psiReturnStatement.getReturnValue())
            {
                final ExpressionParser ep = new ExpressionParser(returnExpression, codeStyleSettings, settings, this, 0);
                child.accept(ep);
                continue;
            }
            if (child instanceof PsiJavaToken) {
                if (((PsiJavaToken)child).getTokenType() == JavaTokenType.SEMICOLON) {
                    addToken(child, semicolons);
                    continue;
                }
            }
            checkForNewline(child);
            child.accept(this);
        }
        undoIndentBias();
    }
}
