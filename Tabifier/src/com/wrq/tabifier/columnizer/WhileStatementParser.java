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
 * Parses while-statements into the column tree.
 */
final class WhileStatementParser
        extends NestedParser
{
    private final TokenColumn  whileKeywords;
    private final TokenColumn  whileStmtOpenParends;
    private final ColumnChoice whileStmtExpressions;
    private final TokenColumn  whileStmtCloseParends;
    private final ColumnChoice whileStmtConditionals;
    private final ColumnChoice unindentedNextBlock;

    /*
     *  parent
     *    |
     *  WHILE_STATEMENT: -- WHILE_STMT_LINES
     *                       |
     *                  WHILE_STATEMENT_ITSELF: WHILE_KEYWORDS WHILE_STMT_OPEN_PAREND TERMS ...
     *                       |                      WHILE_STMT_CLOSE_PAREND WHILE_STMT_CONDITIONALS
     *                       |
     *                  NEXT_LINE_STATEMENTS (indented):
     *                       |
     *                  (unindented) CODE_BLOCK:
     */
    public WhileStatementParser(final ColumnChoice      currentCodeStatementColumnNode,
                                final CodeStyleSettings codeStyleSettings,
                                final TabifierSettings  settings,
                                final NestedParser      documentParser                 )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        /**
         * Add a ColumnSequence node for statements to the column choice passed to us.
         */
        final ColumnSequence whileStmt                  = currentCodeStatementColumnNode.findOrAppend(ColumnSequenceNodeType.WHILE_STATEMENT);
              ColumnChoice   whilesOrNextLineStatements = whileStmt.findColumnChoice(AlignableColumnNodeType.WHILE_STMT_LINES);
              ColumnSequence myNode;
              ColumnSequence unindentedNextBlockSeq;
        if (whilesOrNextLineStatements == null)
        {
            whilesOrNextLineStatements = whileStmt.appendChoiceColumn(settings.align_statements,
                                                                      AlignableColumnNodeType.WHILE_STMT_LINES);
            myNode                     = whilesOrNextLineStatements.findOrAppend(ColumnSequenceNodeType.WHILE_STATEMENT_ITSELF);
            unindentedNextBlockSeq     = whilesOrNextLineStatements.findOrAppend(ColumnSequenceNodeType.CODE_BLOCK            );
            myNode.appendTokenColumn (settings.align_if_keywords, AlignableColumnNodeType.WHILE_KEYWORDS                  );
            myNode.appendTokenColumn (settings.align_if_stmt_open_parend, AlignableColumnNodeType.WHILE_STMT_OPEN_PAREND  );
            myNode.appendChoiceColumn(settings.align_terms, AlignableColumnNodeType.TERMS                                 );
            myNode.appendTokenColumn (settings.align_if_stmt_close_parend, AlignableColumnNodeType.WHILE_STMT_CLOSE_PAREND);
            myNode.appendChoiceColumn(settings.align_if_stmt_conditionals, AlignableColumnNodeType.IF_STMT_CONDITIONALS   );
            unindentedNextBlockSeq.appendChoiceColumn(settings.align_code_block, AlignableColumnNodeType.CODE_BLOCK);
        }
        myNode                 = whilesOrNextLineStatements.findOrAppend(ColumnSequenceNodeType.WHILE_STATEMENT_ITSELF);
        unindentedNextBlockSeq = whilesOrNextLineStatements.findOrAppend(ColumnSequenceNodeType.CODE_BLOCK            );

        whileKeywords          = myNode.findTokenColumn(AlignableColumnNodeType.WHILE_KEYWORDS         );
        whileStmtOpenParends   = myNode.findTokenColumn(AlignableColumnNodeType.WHILE_STMT_OPEN_PAREND );
        whileStmtExpressions   = myNode.findColumnChoice(AlignableColumnNodeType.TERMS                  );
        whileStmtCloseParends  = myNode.findTokenColumn(AlignableColumnNodeType.WHILE_STMT_CLOSE_PAREND);
        whileStmtConditionals  = myNode.findColumnChoice(AlignableColumnNodeType.IF_STMT_CONDITIONALS   );
        unindentedNextBlock    = unindentedNextBlockSeq.findColumnChoice(AlignableColumnNodeType.CODE_BLOCK);
    }

    public final void visitWhileStatement(final PsiWhileStatement psiWhileStatement)
    {
        boolean stmtIsNextLine = false;
        setStatementType(LineGroup.IF_STATEMENT);
        for (int i = 0; i < psiWhileStatement.getChildren().length; i++)
        {
            final PsiElement child = psiWhileStatement.getChildren()[i];

            if (child instanceof PsiComment && !(child instanceof PsiDocComment))
            {
                handleComment((PsiComment) child);
                continue;

            }
            if (child instanceof PsiJavaToken)
            {
                final PsiJavaToken token = (PsiJavaToken) child;
                if (token.getTokenType() == JavaTokenType.WHILE_KEYWORD)
                {
                    addToken(token, whileKeywords);
                }
                else if (token.getTokenType() == JavaTokenType.LPARENTH)
                {
                    addToken(
                            new AlignableToken(
                                    token,
                                    codeStyleSettings.SPACE_BEFORE_WHILE_PARENTHESES,
                                    codeStyleSettings.SPACE_WITHIN_WHILE_PARENTHESES
                            ), whileStmtOpenParends
                    );
                }
                else if (token.getTokenType() == JavaTokenType.RPARENTH)
                {
                    addToken(
                            new AlignableToken(
                                    token,
                                    codeStyleSettings.SPACE_WITHIN_WHILE_PARENTHESES,
                                    false
                            ), whileStmtCloseParends
                    );
                    stmtIsNextLine = false;
                }
                else
                {
                    child.accept(this);
                }
                continue;
            }

            if (child instanceof PsiWhileStatement)
            {
                visitWhileStatement((PsiWhileStatement) child);
                continue;
            }

            if (child instanceof PsiBlockStatement)
            {
                setStatementType(LineGroup.NONE);

                final CodeBlockParser cbp = new CodeBlockParser(unindentedNextBlock, codeStyleSettings, settings, this,
                                                                codeStyleSettings.SPACE_BEFORE_WHILE_LBRACE               );
                child.accept(cbp);
                setStatementType(LineGroup.IF_STATEMENT);
                continue;
            }
            if (child instanceof PsiExpression)
            {
                final ExpressionParser ep = new ExpressionParser(whileStmtExpressions, codeStyleSettings, settings, this, 0);
                child.accept(ep);
                continue;
            }

            if (child instanceof PsiStatement)
            {
                if (stmtIsNextLine)
                {
                    handleNewline(1);
                }
                final StatementParser sp = new StatementParser(
                        whileStmtConditionals,
                        codeStyleSettings, settings, this);
                child.accept(sp);
                undoIndentBias();
                continue;
            }
            checkForNewline(child);
            if (child instanceof PsiWhiteSpace)
            {
                if (child.getText().indexOf('\n') >= 0)
                {
                    stmtIsNextLine = true;
                }
            }
            child.accept(this);
        }
        setStatementType(LineGroup.NONE);
    }
}
