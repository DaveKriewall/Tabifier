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
 * Parses if-statements into the column tree.
 */
final class IfStatementParser
        extends NestedParser
{
    private final TokenColumn  ifElseKeywords;
    private final TokenColumn  secondaryIfKeywords;
    private final TokenColumn  ifStmtOpenParends;
    private final ColumnChoice ifStmtExpressions;
    private final TokenColumn  ifStmtCloseParends;
    private final ColumnChoice ifStmtConditionals;
    private final ColumnChoice unindentedNextBlock;

    private       boolean      expectSecondaryKeyword = false;

    /*
     *  parent
     *    |
     *  IF_STATEMENT: -- IF_STMT_LINES
     *                       |
     *                  IF_STATEMENT_ITSELF: IF_ELSE_KEYWORDS SECONDARY_IF_KEYWORDS IF_STMT_OPEN_PAREND TERMS ...
     *                       |               IF_STMT_CLOSE_PAREND IF_STMT_CONDITIONALS
     *                       |
     *                       |
     *                  NEXT_LINE_STATEMENTS (indented):
     *                       |
     *                  (unindented) CODE_BLOCK:
     */
    public IfStatementParser(final ColumnChoice      currentCodeStatementColumnNode,
                             final CodeStyleSettings codeStyleSettings,
                             final TabifierSettings  settings,
                             final NestedParser      documentParser                 )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        /**
         * Add a ColumnSequence node for statements to the column choice passed to us.
         */
        final ColumnSequence ifstmt                  = currentCodeStatementColumnNode.findOrAppend(ColumnSequenceNodeType.IF_STATEMENT);
              ColumnChoice   ifsOrNextLineStatements = ifstmt.findColumnChoice(AlignableColumnNodeType.IF_STMT_LINES);
              ColumnSequence myNode;
              ColumnSequence unindentedNextBlockSeq;
        if (ifsOrNextLineStatements == null)
        {
            ifsOrNextLineStatements = ifstmt.appendChoiceColumn(settings.align_statements,
                                                                AlignableColumnNodeType.IF_STMT_LINES);
            myNode                  = ifsOrNextLineStatements.findOrAppend(ColumnSequenceNodeType.IF_STATEMENT_ITSELF);
            unindentedNextBlockSeq  = ifsOrNextLineStatements.findOrAppend(ColumnSequenceNodeType.CODE_BLOCK         );
            myNode.appendTokenColumn (settings.align_if_keywords, AlignableColumnNodeType.IF_ELSE_KEYWORDS             );
            myNode.appendTokenColumn (settings.align_if_keywords, AlignableColumnNodeType.SECONDARY_IF_KEYWORDS        );
            myNode.appendTokenColumn (settings.align_if_stmt_open_parend, AlignableColumnNodeType.IF_STMT_OPEN_PAREND  );
            myNode.appendChoiceColumn(settings.align_terms, AlignableColumnNodeType.TERMS                              );
            myNode.appendTokenColumn (settings.align_if_stmt_close_parend, AlignableColumnNodeType.IF_STMT_CLOSE_PAREND);
            myNode.appendChoiceColumn(settings.align_if_stmt_conditionals, AlignableColumnNodeType.IF_STMT_CONDITIONALS);
            unindentedNextBlockSeq.appendChoiceColumn(settings.align_code_block, AlignableColumnNodeType.CODE_BLOCK);
        }
        myNode                 = ifsOrNextLineStatements.findOrAppend(ColumnSequenceNodeType.IF_STATEMENT_ITSELF);
        unindentedNextBlockSeq = ifsOrNextLineStatements.findOrAppend(ColumnSequenceNodeType.CODE_BLOCK         );

        ifElseKeywords         = myNode.findTokenColumn(AlignableColumnNodeType.IF_ELSE_KEYWORDS     );
        secondaryIfKeywords    = myNode.findTokenColumn(AlignableColumnNodeType.SECONDARY_IF_KEYWORDS);
        ifStmtOpenParends      = myNode.findTokenColumn(AlignableColumnNodeType.IF_STMT_OPEN_PAREND  );
        ifStmtExpressions      = myNode.findColumnChoice(AlignableColumnNodeType.TERMS                );
        ifStmtCloseParends     = myNode.findTokenColumn(AlignableColumnNodeType.IF_STMT_CLOSE_PAREND );
        ifStmtConditionals     = myNode.findColumnChoice(AlignableColumnNodeType.IF_STMT_CONDITIONALS );
        unindentedNextBlock    = unindentedNextBlockSeq.findColumnChoice(AlignableColumnNodeType.CODE_BLOCK);
    }

    public final void visitIfStatement(final PsiIfStatement psiIfStatement)
    {
        boolean stmtIsNextLine = false;
        setStatementType(LineGroup.IF_STATEMENT);
        for (int i = 0; i < psiIfStatement.getChildren().length; i++)
        {
            final PsiElement child = psiIfStatement.getChildren()[i];

            if (child instanceof PsiComment && !(child instanceof PsiDocComment))
            {
                handleComment((PsiComment) child);
                continue;

            }
            if (child instanceof PsiJavaToken)
            {
                final PsiJavaToken token = (PsiJavaToken) child;
                if (token.getTokenType() == JavaTokenType.IF_KEYWORD)
                {
                        addToken(token, expectSecondaryKeyword ? secondaryIfKeywords : ifElseKeywords);
                        expectSecondaryKeyword = false;
                }
                else if (token.getTokenType() == JavaTokenType.LPARENTH)
                {
                        addToken(new AlignableToken(token,
                                                    codeStyleSettings.SPACE_BEFORE_IF_PARENTHESES,
                                                    codeStyleSettings.SPACE_WITHIN_IF_PARENTHESES ), ifStmtOpenParends);
                }
                else if (token.getTokenType() == JavaTokenType.RPARENTH)
                {
                        addToken(new AlignableToken(token,
                                                    codeStyleSettings.SPACE_WITHIN_IF_PARENTHESES,
                                                    false                                         ), ifStmtCloseParends);
                        stmtIsNextLine = false;
                }
                else if (token.getTokenType() == JavaTokenType.ELSE_KEYWORD)
                {
                        addToken(token, ifElseKeywords);
                        expectSecondaryKeyword = true;
                        stmtIsNextLine         = false;
                }
                else {
                        child.accept(this);
                }
                continue;
            }

            if (child instanceof PsiIfStatement)
            {
                visitIfStatement((PsiIfStatement) child);
                continue;
            }

            if (child instanceof PsiBlockStatement)
            {
                setStatementType(LineGroup.NONE);

                final CodeBlockParser cbp = new CodeBlockParser(unindentedNextBlock, codeStyleSettings, settings, this,
                                                                codeStyleSettings.SPACE_BEFORE_IF_LBRACE               );
                child.accept(cbp);
                setStatementType(LineGroup.IF_STATEMENT);
                continue;
            }
            if (child instanceof PsiExpression)
            {
                final ExpressionParser ep = new ExpressionParser(ifStmtExpressions, codeStyleSettings, settings, this, 0);
                child.accept(ep);
                continue;
            }

            if (child instanceof PsiStatement)
            {
                if (stmtIsNextLine) handleNewline(1);
                final StatementParser sp = new StatementParser(
                        ifStmtConditionals,
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
