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

import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.wrq.tabifier.parse.*;
import com.wrq.tabifier.settings.TabifierSettings;

/**
 * Parses certain Java statements into the column tree.
 */
final class StatementParser
        extends NestedParser
{
    private final ColumnChoice stmts;
    private final TokenColumn  semicolons;

    /**
     *
     * @param currentCodeStatementColumnNode handles the situation where multiple statements appear on one line.
     *                                       This parameter points to the appropriate column in which the statement
     *                                       should appear.
     * @param settings
     * @param documentParser
     */
    public StatementParser(final ColumnChoice      currentCodeStatementColumnNode,
                           final CodeStyleSettings codeStyleSettings,
                           final TabifierSettings  settings,
                           final NestedParser      documentParser                 )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        /**
         * Add a ColumnSequence node for statements to the column choice passed to us.
         */
        final ColumnSequence myNode = currentCodeStatementColumnNode.findOrAppend(ColumnSequenceNodeType.STATEMENT);
        if (myNode.findColumnChoice(AlignableColumnNodeType.STATEMENT) == null)
        {
            myNode.appendChoiceColumn(settings.align_statements, AlignableColumnNodeType.STATEMENT           );
            myNode.appendTokenColumn (settings.align_semicolons, AlignableColumnNodeType.STATEMENT_SEMICOLONS);
        }
        stmts      = myNode.findColumnChoice(AlignableColumnNodeType.STATEMENT           );
        semicolons = myNode.findTokenColumn(AlignableColumnNodeType.STATEMENT_SEMICOLONS);
    }

    /**
     * There's no actual PsiStatement element in the Psi tree; this routine is called for PsiDeclarationStatements,
     * PsiExpressionStatements, etc., all of which are instances of PsiStatement.  So handle the psiStatement
     * by type directly -- don't handle its children here.  That needs to be delegated to the individual statement
     * type handler (visitDeclarationStatement, visitExpressionStatement, etc.)  This method is here so that the
     * common semicolons can be aligned.
     *
     * @param psiStatement
     */
    public final void visitStatement(final PsiStatement psiStatement)
    {
        /**
         * Use the indicated CODE_STATEMENT ColumnSequence.  Parent object (CodeBlock) resets to the first CODE_STATEMENT
         * in a line when a newline character is seen.  Otherwise, each call to visitStatement uses the next ColumnSequence.
         */
        if (psiStatement instanceof PsiExpressionStatement)
        {
            final ExpressionStatementParser esp = new ExpressionStatementParser(stmts, codeStyleSettings, settings, this);
            psiStatement.accept(esp);
        }
        else
            super.visitStatement(psiStatement);
    }

    public final void visitJavaToken(final PsiJavaToken psiJavaToken)
    {
        {
            if (psiJavaToken.getTokenType() == JavaTokenType.SEMICOLON)
            {
                addToken(new AlignableToken(psiJavaToken,
                                            codeStyleSettings.SPACE_BEFORE_SEMICOLON,
                                            codeStyleSettings.SPACE_AFTER_SEMICOLON  ), semicolons);
                return ;
            }
        }
        super.visitJavaToken(psiJavaToken);
    }
}
