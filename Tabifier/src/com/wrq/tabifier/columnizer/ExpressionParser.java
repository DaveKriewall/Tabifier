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
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Parses Java expressions into the column tree.
 */
public final class ExpressionParser
        extends NestedParser
{
    private static final Logger       logger           = Logger.getLogger("com.wrq.tabifier.columnizer.ExpressionParser");
    private        final ColumnChoice expressionChoice;
    private        final int          nestingLevel;

    public ExpressionParser(
            @NotNull final ColumnChoice      expressionChoice,
            @NotNull final CodeStyleSettings codeStyleSettings,
            @NotNull final TabifierSettings  settings,
            @NotNull final NestedParser      documentParser,
                     final int               nestingLevel      )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        this.expressionChoice = expressionChoice;
        this.nestingLevel     = nestingLevel;
    }

    /**
     * When an expression has been recursively parsed past the nesting level, we start simply dropping the entire
     * expression in as a simple term -- no more subalignment taking place.
     * 
     * @param simpleTerm 
     */
    private AlignableToken visitSimpleTerm(final PsiElement simpleTerm)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            logger.debug("visitSimpleTerm called; nestingLevel="       +
                         nestingLevel                                  +
                         ", configured max nesting level="             +
                         settings.expression_parse_nesting_level.get()  );
        }
        final ColumnSequence seq = expressionChoice.findOrAppend(ColumnSequenceNodeType.TERM );
              TokenColumn    col = seq.findTokenColumn          (AlignableColumnNodeType.TERM);
        if (col == null)
        {
            col = seq.appendTokenColumn(settings.align_terms, AlignableColumnNodeType.TERM);
        }
        /** if the simpleTerm is multiline, we need to parse it down so that the first "token" is assigned to
         * the token column, and the rest are simply added as unaligned tokens.   The multiline processor works
         * on single lines also, so just use it for everything here.
         */
        return addMultilineElement(simpleTerm, col);
    }

    public final void visitExpression(final PsiExpression psiExpression)
    {
        visitSimpleTerm(psiExpression);
    }

    public final void visitReferenceExpression(final PsiReferenceExpression psiReferenceExpression)
    {
//        if (nestingLevel > settings.expression_parse_nesting_level.get())
//        {
//            visitSimpleTerm(psiReferenceExpression);
//            return;
//        }
//        PsiElement e;
//        for (int i = 0; i < psiReferenceExpression.getChildren().length; i++)
//        {
//            psiReferenceExpression.getChildren()[i].accept(this);
//        }
        visitSimpleTerm(psiReferenceExpression); // todo - fix
    }

    public final void visitParenthesizedExpression(final PsiParenthesizedExpression psiParenthesizedExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiParenthesizedExpression);
            return ;
        }
        final ColumnSequence seq = expressionChoice.findOrAppend(ColumnSequenceNodeType.PARENTHESIZED_EXPRESSION);
        if (seq.findTokenColumn(AlignableColumnNodeType.OPEN_PAREND) == null)
        {
            seq.appendTokenColumn (settings.align_other_open_parend, AlignableColumnNodeType.OPEN_PAREND             );
            seq.appendChoiceColumn(settings.align_expression_statements, AlignableColumnNodeType.EXPRESSION_STATEMENT);
            seq.appendTokenColumn (settings.align_other_close_parend, AlignableColumnNodeType.CLOSE_PAREND           );
        }
        final TokenColumn  openParend  = seq.findTokenColumn (AlignableColumnNodeType.OPEN_PAREND         );
        final ColumnChoice expr        = seq.findColumnChoice(AlignableColumnNodeType.EXPRESSION_STATEMENT);
        final TokenColumn  closeParend = seq.findTokenColumn(AlignableColumnNodeType.CLOSE_PAREND);
        for (int i = 0; i < psiParenthesizedExpression.getChildren().length; i++)
        {
            final PsiElement child = psiParenthesizedExpression.getChildren()[i];
            if (child instanceof PsiJavaToken)
            {
                final PsiJavaToken token = (PsiJavaToken) child;
                if (token.getTokenType() == JavaTokenType.LPARENTH)
                {
                    addToken(new AlignableToken(token, false, codeStyleSettings.SPACE_WITHIN_PARENTHESES), openParend);
                    continue;
                }
                if (token.getTokenType() == JavaTokenType.RPARENTH)
                {
                    addToken(new AlignableToken(token, codeStyleSettings.SPACE_WITHIN_PARENTHESES, false), closeParend);
                    continue;
                }
            }
            if (child == psiParenthesizedExpression.getExpression())
            {
                final ExpressionParser ep = new ExpressionParser(expr, codeStyleSettings, settings, this, nestingLevel + 1);
                child.accept(ep);
                continue;
            }
            child.accept(this);
        }
    }

    public final void visitTypeCastExpression(final PsiTypeCastExpression psiTypeCastExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiTypeCastExpression);
            return;
        }
        final ColumnSequence seq = expressionChoice.findOrAppend(ColumnSequenceNodeType.TYPE_CAST_EXPRESSION);
        if (seq.findTokenColumn(AlignableColumnNodeType.OPEN_PAREND) == null)
        {
            seq.appendTokenColumn (settings.align_typecast_open_parend, AlignableColumnNodeType.OPEN_PAREND          );
            seq.appendTokenColumn (settings.align_typecast_type, AlignableColumnNodeType.TERM                        );
            seq.appendTokenColumn (settings.align_typecast_close_parend, AlignableColumnNodeType.CLOSE_PAREND        );
            seq.appendChoiceColumn(settings.align_expression_statements, AlignableColumnNodeType.EXPRESSION_STATEMENT);

        }
        final TokenColumn  openParend   = seq.findTokenColumn(AlignableColumnNodeType.OPEN_PAREND);
        final TokenColumn  typecastType = seq.findTokenColumn (AlignableColumnNodeType.TERM                );
        final TokenColumn  closeParend  = seq.findTokenColumn (AlignableColumnNodeType.CLOSE_PAREND        );
        final ColumnChoice expr         = seq.findColumnChoice(AlignableColumnNodeType.EXPRESSION_STATEMENT);
        for (int i = 0; i < psiTypeCastExpression.getChildren().length; i++)
        {
            final PsiElement child = psiTypeCastExpression.getChildren()[i];
            if (child == psiTypeCastExpression.getCastType())
            {
                addToken(child, typecastType);
                continue;
            }
            if (child == psiTypeCastExpression.getOperand())
            {
                final ExpressionParser ep = new ExpressionParser(expr, codeStyleSettings, settings, this, nestingLevel);
                child.accept(ep);
                continue;
            }
            if (child instanceof PsiJavaToken)
            {
                final PsiJavaToken token = (PsiJavaToken) child;
                if (token.getTokenType() == JavaTokenType.LPARENTH)
                {
                    addToken(token, openParend);
                    continue;
                }
                else if (token.getTokenType() == JavaTokenType.RPARENTH)
                {
                    addToken(new AlignableToken(token,
                                                false,
                                                codeStyleSettings.SPACE_AFTER_TYPE_CAST),
                             closeParend                                                 );
                    continue;
                }
            }
            child.accept(this);
        }
    }

    public final void visitAssignmentExpression(final PsiAssignmentExpression psiAssignmentExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiAssignmentExpression);
            return ;
        }
        final ColumnSequence seq = expressionChoice.findOrAppend(ColumnSequenceNodeType.ASSIGNMENT_STATEMENTS);
        if (seq.findColumnChoice(AlignableColumnNodeType.ASSIGNMENT_EXPRESSIONS) == null)
        {
            seq.appendChoiceColumn(settings.align_statements, AlignableColumnNodeType.ASSIGNMENT_EXPRESSIONS);
        }
        final ColumnChoice               ae  = seq.findColumnChoice(AlignableColumnNodeType.ASSIGNMENT_EXPRESSIONS);
        final AssignmentExpressionParser aep = new AssignmentExpressionParser(ae, codeStyleSettings, settings,
                                                                              this                            );
        psiAssignmentExpression.accept(aep);
    }

    public final void visitPostfixExpression(final PsiPostfixExpression psiPostfixExpression)
    {
        visitSimpleTerm(psiPostfixExpression);
    }

    public final void visitConditionalExpression(final PsiConditionalExpression psiConditionalExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiConditionalExpression);
            return ;
        }
        final ColumnSequence seq = expressionChoice.findOrAppend(ColumnSequenceNodeType.CONDITIONAL_EXPRESSION);
        if (seq.findColumnChoice(AlignableColumnNodeType.CONDITION) == null)
        {
            seq.appendChoiceColumn(settings.align_terms, AlignableColumnNodeType.CONDITION            );
            seq.appendTokenColumn (settings.align_question_mark, AlignableColumnNodeType.QUESTION_MARK);
            seq.appendChoiceColumn(settings.align_terms, AlignableColumnNodeType.THEN_EXPRESSION      );
            seq.appendTokenColumn (settings.align_colon, AlignableColumnNodeType.COLON                );
            seq.appendChoiceColumn(settings.align_terms, AlignableColumnNodeType.ELSE_EXPRESSION      );

        }
        final ColumnChoice condition      = seq.findColumnChoice(AlignableColumnNodeType.CONDITION      );
        final TokenColumn  questionMark   = seq.findTokenColumn (AlignableColumnNodeType.QUESTION_MARK  );
        final ColumnChoice thenExpr       = seq.findColumnChoice(AlignableColumnNodeType.THEN_EXPRESSION);
        final TokenColumn  colon          = seq.findTokenColumn (AlignableColumnNodeType.COLON          );
        final ColumnChoice elseExpr       = seq.findColumnChoice(AlignableColumnNodeType.ELSE_EXPRESSION);

              boolean      wrappedAtColon = false;
        for (int i = 0; i < psiConditionalExpression.getChildren().length; i++)
        {
            final PsiElement child = psiConditionalExpression.getChildren()[i];
            if (child == psiConditionalExpression.getCondition())
            {
                final ExpressionParser ep = new ExpressionParser(condition,
                                                                 codeStyleSettings, settings, this,
                                                                 nestingLevel + 1                  );
                child.accept(ep);
                continue;
            }
            if (child == psiConditionalExpression.getThenExpression())
            {
                final ExpressionParser ep = new ExpressionParser(thenExpr,
                                                                 codeStyleSettings, settings, this,
                                                                 nestingLevel + 1                  );
                child.accept(ep);
                continue;
            }
            if (child == psiConditionalExpression.getElseExpression())
            {
                final ExpressionParser ep = new ExpressionParser(wrappedAtColon ? thenExpr : elseExpr,
                                                                 codeStyleSettings, settings, this,
                                                                 nestingLevel + 1                     );
                child.accept(ep);
                continue;
            }
            if (child instanceof PsiJavaToken)
            {
                final PsiJavaToken token = (PsiJavaToken) child;
                if (token.getTokenType() == JavaTokenType.QUEST)
                {
                    handleNewline(2                                                                      );
                    addToken     (new AlignableToken(token,
                                                     codeStyleSettings.SPACE_BEFORE_QUEST,
                                                     codeStyleSettings.SPACE_AFTER_QUEST  ), questionMark);
                    continue;
                }
                if (token.getTokenType() == JavaTokenType.COLON)
                {
                    wrappedAtColon = isSawNewline();
                    final TokenColumn tc          = wrappedAtColon ? questionMark                         : colon;
                    final boolean     spaceBefore = wrappedAtColon ? codeStyleSettings.SPACE_BEFORE_QUEST
                                                                   : codeStyleSettings.SPACE_BEFORE_COLON;
                    final boolean     spaceAfter  = wrappedAtColon ? codeStyleSettings.SPACE_AFTER_QUEST
                                                                   : codeStyleSettings.SPACE_AFTER_COLON;

                    addToken(new AlignableToken(token, spaceBefore, spaceAfter), tc);
                    continue;
                }
            }
            checkForNewline(child);
            child.accept   (this );
        }
        undoIndentBias();
    }

    public final void visitLiteralExpression(final PsiLiteralExpression psiLiteralExpression)
    {
        final AlignableToken token = visitSimpleTerm(psiLiteralExpression);
        if (settings.right_justify_numeric_literals.get()      &&
            psiLiteralExpression.getValue() instanceof Integer   )
        {
            token.setRightJustified();
        }
    }

    public final void visitPrefixExpression(final PsiPrefixExpression psiPrefixExpression)
    {
        if (psiPrefixExpression.getOperand() instanceof PsiLiteralExpression)
        {
            final AlignableToken token = visitSimpleTerm(psiPrefixExpression);
            if (settings.right_justify_numeric_literals.get())
            {
                if (psiPrefixExpression.getOperand() instanceof PsiLiteralExpression)
                {
                    if (((PsiLiteralExpression) psiPrefixExpression.getOperand()).getValue() instanceof Integer)
                    {
                        token.setRightJustified();
                    }
                }
            }
        }
        else
        {
            final ColumnSequence seq = expressionChoice.findOrAppend(ColumnSequenceNodeType.PREFIX_EXPRESSION);
            if (seq.findTokenColumn(AlignableColumnNodeType.PREFIX_EXPRESSION_OPERATOR) == null)
            {
                seq.appendTokenColumn (settings.align_terms, AlignableColumnNodeType.PREFIX_EXPRESSION_OPERATOR);
                seq.appendChoiceColumn(settings.align_terms, AlignableColumnNodeType.EXPR_LOPERAND             );

            }
            final TokenColumn  operator = seq.findTokenColumn (AlignableColumnNodeType.PREFIX_EXPRESSION_OPERATOR);
            final ColumnChoice expr     = seq.findColumnChoice(AlignableColumnNodeType.EXPR_LOPERAND             );

            for (int i = 0; i < psiPrefixExpression.getChildren().length; i++)
            {
                final PsiElement child = psiPrefixExpression.getChildren()[i];
                if (child == psiPrefixExpression.getOperationSign())
                {
                    handleNewline(2              );
                    addToken     (child, operator);
                    continue;
                }
                if (child == psiPrefixExpression.getOperand())
                {
                    final ExpressionParser ep = new ExpressionParser(expr,
                                                                     codeStyleSettings, settings, this,
                                                                     nestingLevel                      );
                    child.accept(ep);
                    continue;
                }
                checkForNewline(child);
                child.accept   (this );
            }
            undoIndentBias();
        }
    }

    /**
     * At some point between IDEA 6 and IDEA 13, PsiPolyadicExpressions were introduced.  The previous tabifier code handled these as simple (atomic) terms, hence
     * they were never tabified.  Pass them to a customized parser which will align the terms properly.
     */
    @Override
    public void visitPolyadicExpression(PsiPolyadicExpression psiPolyadicExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiPolyadicExpression);
        }
        else
        {
            final PolyadicExpressionParser pep = new PolyadicExpressionParser(expressionChoice, codeStyleSettings, settings, this, nestingLevel);
            psiPolyadicExpression.accept(pep);
        }
    }

    public final void visitBinaryExpression(final PsiBinaryExpression psiBinaryExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiBinaryExpression);
        }
        else
        {
            final BinaryExpressionParser bep = new BinaryExpressionParser(expressionChoice, codeStyleSettings, settings, this, nestingLevel);
            psiBinaryExpression.accept(bep);
        }
    }

    public final void visitNewExpression(final PsiNewExpression psiNewExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiNewExpression);
        }
        else
        {
            final NewExpressionParser nep = new NewExpressionParser(expressionChoice, codeStyleSettings, settings, this, nestingLevel);
            psiNewExpression.accept(nep);
        }
    }

    public final void visitThisExpression(final PsiThisExpression psiThisExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiThisExpression);
            return ;
        }

        super.visitThisExpression(psiThisExpression);
    }

    public final void visitInstanceOfExpression(final PsiInstanceOfExpression psiInstanceOfExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiInstanceOfExpression);
            return ;
        }

        super.visitInstanceOfExpression(psiInstanceOfExpression);
    }

    public final void visitClassObjectAccessExpression(final PsiClassObjectAccessExpression psiClassObjectAccessExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiClassObjectAccessExpression);
            return ;
        }

        super.visitClassObjectAccessExpression(psiClassObjectAccessExpression);
    }

    public final void visitMethodCallExpression(final PsiMethodCallExpression psiMethodCallExpression)
    {
        if (nestingLevel > settings.expression_parse_nesting_level.get())
        {
            visitSimpleTerm(psiMethodCallExpression);
//            super.visitMethodCallExpression(psiMethodCallExpression); // todo
            return ;
        }
        final ColumnSequenceNodeType method_calls = ColumnSequenceNodeType.getMethodCallCSNT(
                psiMethodCallExpression.getMethodExpression().getText(),
                settings.method_call_similarity_threshold.get        () );
        final ColumnSequence         seq          = expressionChoice.findOrAppend           (method_calls);
        if (seq.findTokenColumn(AlignableColumnNodeType.METHOD_NAME) == null)
        {
            seq.appendTokenColumn(settings.align_method_call_names, AlignableColumnNodeType.METHOD_NAME      );
            seq.appendTokenColumn(settings.align_method_call_open_parend, AlignableColumnNodeType.OPEN_PAREND);
            /**
             * Since number of parameters is variable, but close parends must follow all parameters,
             * make a choiceColumn for all parameters.
             */
            seq.appendChoiceColumn(settings.align_initial_params, AlignableColumnNodeType.PARAMS                );
            seq.appendTokenColumn (settings.align_method_call_close_parend, AlignableColumnNodeType.CLOSE_PAREND);
        }
        final TokenColumn  methodName  = seq.findTokenColumn (AlignableColumnNodeType.METHOD_NAME );
        final TokenColumn  openParend  = seq.findTokenColumn (AlignableColumnNodeType.OPEN_PAREND );
        final ColumnChoice params      = seq.findColumnChoice(AlignableColumnNodeType.PARAMS      );
        final TokenColumn  closeParend = seq.findTokenColumn (AlignableColumnNodeType.CLOSE_PAREND);

        for (int i = 0; i < psiMethodCallExpression.getChildren().length; i++)
        {
            final PsiElement child = psiMethodCallExpression.getChildren()[i];
            if (child == psiMethodCallExpression.getMethodExpression())
            {
                addToken(child, methodName); // todo - try recursive approach  - this was original line
//                child.accept(this);   // todo - new attempt - wrong, should pass methodName column to PsiReferenceExpression handler
//                super.visitMethodCallExpression(psiMethodCallExpression);
                continue;
            }
            if (child == psiMethodCallExpression.getArgumentList())
            {
                final ExpressionListParser list = new ExpressionListParser(openParend,
                                                                           params, closeParend,
                                                                           codeStyleSettings,
                                                                           settings, this,
                                                                           nestingLevel, '(', ')');
                child.accept(list);
                continue;
            }
            checkForNewline(child);
            child.accept   (this );
        }
        undoIndentBias(); // reset any contribution we made to indent bias
    }

    public final void visitArrayInitializerExpression(final PsiArrayInitializerExpression psiArrayInitializerExpression)
    {
        if (settings.disable_array_initializer_processing.get()            ||
            (nestingLevel > settings.expression_parse_nesting_level.get())   )
        {
            visitSimpleTerm(psiArrayInitializerExpression);
            return ;
        }
        /** parse a list of expressions inside a pair of braces. */
        final ColumnSequence seq = expressionChoice.findOrAppend(ColumnSequenceNodeType.ARRAY_INITIALIZER);
        if (seq.findTokenColumn(AlignableColumnNodeType.OPEN_PAREND) == null)
        {
            seq.appendTokenColumn(settings.align_method_call_open_parend, AlignableColumnNodeType.OPEN_PAREND);
            /**
             * Since number of parameters is variable, but close parends must follow all parameters,
             * make a choiceColumn for all parameters.
             */
            seq.appendChoiceColumn(settings.align_initial_params, AlignableColumnNodeType.PARAMS                );
            seq.appendTokenColumn (settings.align_method_call_close_parend, AlignableColumnNodeType.CLOSE_PAREND);
        }
        final TokenColumn          openParend  = seq.findTokenColumn (AlignableColumnNodeType.OPEN_PAREND );
        final ColumnChoice         params      = seq.findColumnChoice(AlignableColumnNodeType.PARAMS      );
        final TokenColumn          closeParend = seq.findTokenColumn (AlignableColumnNodeType.CLOSE_PAREND);

        final ExpressionListParser list        = new ExpressionListParser(openParend, params, closeParend, codeStyleSettings, settings, this, nestingLevel + 1, '{', '}');
        psiArrayInitializerExpression.accept(list);
    }

    public final void visitSuperExpression(final PsiSuperExpression psiSuperExpression)
    {
        visitSimpleTerm(psiSuperExpression);
    }

    public final void visitArrayAccessExpression(final PsiArrayAccessExpression psiArrayAccessExpression)
    {
        visitSimpleTerm(psiArrayAccessExpression);
    }
}
