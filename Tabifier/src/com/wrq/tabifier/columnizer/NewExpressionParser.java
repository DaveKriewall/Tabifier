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

/**
 * Parses "new" expressions into the column tree.  Handle anonymous inner class syntax as well as common syntax.
 */
public final class NewExpressionParser
        extends NestedParser
{
    private final int          nestingLevel;
    private final TokenColumn  newToken;
    private final ColumnChoice referenceElement;
    private final TokenColumn  arrayType;
    private final TokenColumn  leftBrace;
    private final TokenColumn  rightBrace;
    private final ColumnChoice arrayInitializer;

    /*
     *   expressionChoice
     *         |
     *    newExpressionSeq: --> newToken -- referenceElement
     *                                             |
     *                                       <methodname unique> -- openParend -- params -- closeParend
     *                                             |
     *                                       <class>: -->  <Array type> -- <LBrace> -- <RBrace> -- <array initializer>
     */
    public NewExpressionParser(final ColumnChoice      expressionChoice,
                               final CodeStyleSettings codeStyleSettings,
                               final TabifierSettings  settings,
                               final NestedParser      documentParser,
                               final int               nestingLevel      )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        this.nestingLevel = nestingLevel;
        final ColumnSequence seq = expressionChoice.findOrAppend(ColumnSequenceNodeType.NEW_EXPRESSION);
        if (seq.findTokenColumn(AlignableColumnNodeType.NEW_TOKEN) == null)
        {
            seq.appendTokenColumn (settings.align_terms, AlignableColumnNodeType.NEW_TOKEN               );
            seq.appendChoiceColumn(settings.align_new_object, AlignableColumnNodeType.REFERENCE_ELEMENT);
            ColumnChoice   re   = seq.findColumnChoice(AlignableColumnNodeType.REFERENCE_ELEMENT);
            ColumnSequence cseq = re.findOrAppend(ColumnSequenceNodeType.CLASS_SEQ);
            cseq.appendTokenColumn (settings.align_new_object, AlignableColumnNodeType.ARRAY_TYPE );
            cseq.appendTokenColumn (settings.align_braces, AlignableColumnNodeType.LEFT_BRACE       );
            cseq.appendTokenColumn (settings.align_braces, AlignableColumnNodeType.RIGHT_BRACE      );
            cseq.appendChoiceColumn(settings.align_braces, AlignableColumnNodeType.ARRAY_INITIALIZER);
        }
        newToken         = seq.findTokenColumn(AlignableColumnNodeType.NEW_TOKEN        );
        referenceElement = seq.findColumnChoice(AlignableColumnNodeType.REFERENCE_ELEMENT);
        ColumnSequence cseq = referenceElement.findOrAppend(ColumnSequenceNodeType.CLASS_SEQ);
        arrayType        = cseq.findTokenColumn(AlignableColumnNodeType.ARRAY_TYPE       );
        leftBrace = cseq.findTokenColumn(AlignableColumnNodeType.LEFT_BRACE       );
        rightBrace = cseq.findTokenColumn(AlignableColumnNodeType.RIGHT_BRACE      );
        arrayInitializer = cseq.findColumnChoice(AlignableColumnNodeType.ARRAY_INITIALIZER);
    }

    public final void visitNewExpression(final PsiNewExpression psiNewExpression)
    {
        TokenColumn  openParend  = null;
        ColumnChoice params      = null;
        TokenColumn  closeParend = null;
        boolean arrayInit = psiNewExpression.getArrayInitializer() != null;
        boolean couldBeTypeKeyword = false;

        for (int i = 0; i < psiNewExpression.getChildren().length; i++)
        {
            final PsiElement child = psiNewExpression.getChildren()[i];
            if (child.getText().equals("new"))
            {
                // this is the 'new' keyword.
                addToken(child, newToken);
                couldBeTypeKeyword = true;
                continue;
            }
            if (child instanceof PsiAnonymousClass)
            {
                final ClassParser cp = new ClassParser(getClassColumn(), codeStyleSettings, settings, this);
                child.accept(cp);
                continue;
            }
            if (child == psiNewExpression.getArrayInitializer()) {
                final ExpressionParser expr = new ExpressionParser(arrayInitializer,
                                                                     codeStyleSettings,
                                                                     settings,
                                                                     this,
                                                                     0);
                child.accept(expr);
                continue;
            }
            if (arrayInit &&
                    child instanceof PsiJavaToken) {
                if (child.getText().equals("{")) {
                    addToken(child, leftBrace);
                    continue;
                }
                if (child.getText().equals("}")) {
                    addToken(child, rightBrace);
                    continue;
                }
            }
            boolean itsTheClassReference = false;
            if (psiNewExpression.getClassReference() == null) {
                // the psiNewExpression apparently refers to a primitive type, like int.  In this case, evidently
                // no class reference exists.  This code tricks the parser into believing that the class reference
                // is actually the primitive type, and sets the boolean true when the child refers to that
                // primitive type.
                if (couldBeTypeKeyword &&
                    child.getText().length() > 0 &&
                    !(child instanceof PsiWhiteSpace) &&
                    !(child instanceof PsiComment))
                {
                    couldBeTypeKeyword = false;
                    itsTheClassReference = true;
                }
            }
            else {
                itsTheClassReference = child == psiNewExpression.getClassReference();
            }
            if (itsTheClassReference)
            {
                if (arrayInit) {
                    addToken(child, arrayType);
                    continue;
                }
                final ColumnSequenceNodeType method_calls = ColumnSequenceNodeType.getMethodCallCSNT(
                        child.getText(),
                        settings.method_call_similarity_threshold.get());
                final ColumnSequence         seq          = referenceElement.findOrAppend(method_calls);
                if (seq.findTokenColumn(AlignableColumnNodeType.METHOD_NAME) == null)
                {
                    seq.appendTokenColumn(settings.align_new_object,              AlignableColumnNodeType.METHOD_NAME);
                    seq.appendTokenColumn(settings.align_method_call_open_parend, AlignableColumnNodeType.OPEN_PAREND);
                    /**
                     * Since number of parameters is variable, but close parends must follow all parameters,
                     * make a choiceColumn for all parameters.
                     */
                    seq.appendChoiceColumn(settings.align_initial_params,           AlignableColumnNodeType.PARAMS      );
                    seq.appendTokenColumn (settings.align_method_call_close_parend, AlignableColumnNodeType.CLOSE_PAREND);
                }
                final TokenColumn methodName = seq.findTokenColumn(AlignableColumnNodeType.METHOD_NAME);
                openParend  = seq.findTokenColumn(AlignableColumnNodeType.OPEN_PAREND );
                params      = seq.findColumnChoice(AlignableColumnNodeType.PARAMS      );
                closeParend = seq.findTokenColumn(AlignableColumnNodeType.CLOSE_PAREND);
                addToken(child, methodName);

                continue;
            }
            if (child == psiNewExpression.getArgumentList())
            {
                final ExpressionListParser list = new ExpressionListParser(openParend,
                                                                     params,
                                                                     closeParend,
                                                                     codeStyleSettings,
                                                                     settings,
                                                                     this,
                                                                     nestingLevel + 1, '(', ')');
                child.accept(list);
                continue;
            }

            checkForNewline(child);
            child.accept(this);
        }
        undoIndentBias();
    }
}
