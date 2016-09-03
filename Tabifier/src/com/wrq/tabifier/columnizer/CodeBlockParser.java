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
import com.wrq.tabifier.parse.ColumnSequenceNodeType;
import com.wrq.tabifier.settings.TabifierSettings;

/**
 * Parses code blocks into the column tree.
 */
final class CodeBlockParser
        extends NestedParser
{
    private final ColumnSequence codeBlockSequence;
    private       int            codeStatementIndex;
    private final boolean        spaceBeforeBrace;
    private final TokenColumn    braces;
    private final ColumnSequence horizontalCodeBlockSequence;
    private final TokenColumn    leftBrace;
    private final TokenColumn    rightBrace;
    private final ColumnChoice   statements;
    private final ColumnSequence statementSequence;

    /*
     * A code block may be aligned in one of two ways: if the braces are on the same line,
     * a horizontal layout such as:
     *   parent
     *     |
     *   horizontalCodeBlock: -- LBRACE --- statements --- RBRACE
     *
     * can be used.  If braces are on separate lines, the braces are aligned vertically
     * and the code block is indented.
     *
     *   parent
     *     |
     *   BRACES
     *     |
     *   CODE_BLOCK
     *
     */
    public CodeBlockParser(final ColumnChoice      parent,
                           final CodeStyleSettings codeStyleSettings,
                           final TabifierSettings  settings,
                           final NestedParser      documentParser,
                           final boolean           spaceBeforeBrace  )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        final ColumnSequence braceSeq = parent.findOrAppend(ColumnSequenceNodeType.BRACES);
        codeBlockSequence = parent.findOrAppend(ColumnSequenceNodeType.CODE_BLOCK);
        if (braceSeq.findTokenColumn(AlignableColumnNodeType.BRACES) == null)
        {
            braceSeq.appendTokenColumn(settings.align_braces, AlignableColumnNodeType.BRACES);
        }
        braces                      = braceSeq.findTokenColumn(AlignableColumnNodeType.BRACES);
        this.spaceBeforeBrace       = spaceBeforeBrace;
        horizontalCodeBlockSequence = parent.findOrAppend(ColumnSequenceNodeType.HORIZONTAL_CODE_BLOCK);
        if (horizontalCodeBlockSequence.findTokenColumn(AlignableColumnNodeType.LEFT_BRACE) == null)
        {
            horizontalCodeBlockSequence.appendTokenColumn (settings.align_braces, AlignableColumnNodeType.LEFT_BRACE   );
            horizontalCodeBlockSequence.appendChoiceColumn(settings.align_statements, AlignableColumnNodeType.STATEMENT);
            horizontalCodeBlockSequence.appendTokenColumn (settings.align_braces, AlignableColumnNodeType.RIGHT_BRACE  );
        }
        leftBrace         = horizontalCodeBlockSequence.findTokenColumn(AlignableColumnNodeType.LEFT_BRACE );
        statements        = horizontalCodeBlockSequence.findColumnChoice(AlignableColumnNodeType.STATEMENT  );
        rightBrace        = horizontalCodeBlockSequence.findTokenColumn(AlignableColumnNodeType.RIGHT_BRACE);
        statementSequence = statements.findOrAppend(ColumnSequenceNodeType.CODE_BLOCK);
    }

    /**
     * A code block consists of an open brace, a choice of statement types, followed by a close brace.
     * The braces are placed in a separate ColumnSequence so that the right brace is not indented by the width
     * of the block or vice versa.
     * 
     * @param psiCodeBlock PsiElement corresponding to the code block.
     */
    public final void visitCodeBlock(final PsiCodeBlock psiCodeBlock)
    {
        /**
         * When braces are on the same line, preserve spacing.
         */
        boolean spaceAfterLBrace  = false;
        boolean spaceBeforeRBrace = false;
        codeStatementIndex = 1;
              PsiElement child;
        final boolean    oneLineCodeBlock = psiCodeBlock.getText().indexOf('\n') == -1;
        if (oneLineCodeBlock) {
            suspendStatementTypeChecking();
        }
        ColumnSequence sequence = oneLineCodeBlock ? statementSequence : codeBlockSequence;
        for (int i = 0; i < psiCodeBlock.getChildren().length; i++)
        {
            ColumnChoice currentCodeStatementColumnNode = null;
            while (currentCodeStatementColumnNode == null)
            {
                currentCodeStatementColumnNode = (ColumnChoice) sequence.findNth(AlignableColumnNodeType.FIELD, codeStatementIndex);
                if (currentCodeStatementColumnNode == null)
                {
                    sequence.appendChoiceColumn(settings.align_multiple_statements, AlignableColumnNodeType.FIELD);
                }
            }
            child = psiCodeBlock.getChildren()[i];
            if (child instanceof PsiDeclarationStatement)
            {
                final DeclarationParser dp = new DeclarationParser(currentCodeStatementColumnNode,
                                                                   codeStyleSettings,
                                                                   settings,
                                                                   this                           );
                child.accept(dp);
                codeStatementIndex++;
                continue;
            }
            if (child instanceof PsiExpressionStatement)
            {
                final StatementParser sp = new StatementParser(currentCodeStatementColumnNode,
                                                               codeStyleSettings,
                                                               settings,
                                                               this                           );
                child.accept(sp);
                codeStatementIndex++;
                continue;
            }
            if (child instanceof PsiIfStatement)
            {
                final IfStatementParser ip = new IfStatementParser(currentCodeStatementColumnNode,
                                                                   codeStyleSettings,
                                                                   settings,
                                                                   this                           );
                child.accept(ip);
                codeStatementIndex++;
                continue;
            }
            if (child instanceof PsiWhileStatement)
            {
                final WhileStatementParser wp = new WhileStatementParser(currentCodeStatementColumnNode,
                                                                         codeStyleSettings,
                                                                         settings,
                                                                         this                           );
                child.accept(wp);
                codeStatementIndex++;
                continue;
            }
            if (child instanceof PsiReturnStatement)
            {
                final ReturnStatementParser rp = new ReturnStatementParser(currentCodeStatementColumnNode,
                                                                           codeStyleSettings,
                                                                           settings,
                                                                           this                           );
                child.accept(rp);
                codeStatementIndex++;
                continue;
            }
            if (child == psiCodeBlock.getLBrace())
            {
                if (child.getNextSibling() instanceof PsiWhiteSpace && oneLineCodeBlock) {
                    if (child.getNextSibling().getText().charAt(0) != '\n') {
                        spaceAfterLBrace = true;
                    }
                }
                addToken(new AlignableToken(child, this.spaceBeforeBrace, spaceAfterLBrace),
                         oneLineCodeBlock ? leftBrace : braces                              );
                if (!oneLineCodeBlock)
                {
                    bumpIndentLevel((PsiJavaToken) child);
                    scheduleAlignment("saw CodeBlock LBRACE");
                }
                continue;
            }
            if (child == psiCodeBlock.getRBrace())
            {
                if (!oneLineCodeBlock)
                {
                    reduceIndentLevel((PsiJavaToken) child);
                }
                if (oneLineCodeBlock                                &&
                    child.getPrevSibling() instanceof PsiWhiteSpace   )
                {
                    PsiWhiteSpace ws      = (PsiWhiteSpace) child.getPrevSibling();
                    PsiElement    backTwo = child.getPrevSibling().getPrevSibling();
                    if (ws.getText().charAt(0) != '\n'                     &&
                        backTwo                != psiCodeBlock.getLBrace()   )
                    {
                        spaceBeforeRBrace = true;
                    }
                }
                addToken(new AlignableToken(child, spaceBeforeRBrace, false),
                         oneLineCodeBlock ? rightBrace : braces              );
                if (!oneLineCodeBlock)
                {
                    scheduleAlignment("saw CodeBlock RBRACE");
                }
                continue;
            }
            if (child instanceof PsiComment) {
                handleComment((PsiComment) child);
                continue;
            }
            if (child instanceof PsiWhiteSpace)
            {
                if (child.getText().indexOf('\n') >= 0)
                {
                    codeStatementIndex = 1;
                }
            }
            child.accept(this); // handle anything else we don't recognize
        }
        if (oneLineCodeBlock)
        {
            resumeStatementTypeChecking();
        }
    }
}
