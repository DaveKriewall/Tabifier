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
 * Parses variable and field declarations into the column tree.
 */
public class VariableParser
        extends NestedParser
{
    private final ColumnChoice        annotations;
    private final ModifierTokenColumn modifiers;
    private final TokenColumn         types;
    private final TokenColumn         names;
    private final TokenColumn         assignmentOperators;
    private final ColumnChoice        expressions;
    private final TokenColumn         commas;
    private final TokenColumn         semicolons;

    /**
     * @param currentCodeStatementColumnNode handles the situation where multiple statements appear on one line.
     *                                       This parameter points to the appropriate column in which the statement
     *                                       should appear.
     * @param settings
     * @param documentParser
     */
    public VariableParser(final ColumnChoice      currentCodeStatementColumnNode,
                          final CodeStyleSettings codeStyleSettings,
                          final TabifierSettings  settings,
                          final NestedParser      documentParser                 )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        /**
         * Add a ColumnSequence node for statements to the column choice passed to us.
         */
        final ColumnSequence myNode = currentCodeStatementColumnNode.findOrAppend(ColumnSequenceNodeType.DECLARATION);
        if (myNode.findModifierTokenColumn(AlignableColumnNodeType.ASSIGNMENT_MODIFIERS) == null)
        {
            myNode.appendChoiceColumn       (settings.align_annotations, AlignableColumnNodeType.ANNOTATIONS                  );
            myNode.appendModifierTokenColumn(settings.align_modifiers                                                         );
            myNode.appendTokenColumn        (settings.align_variable_types, AlignableColumnNodeType.VARTYPES                  );
            myNode.appendTokenColumn        (settings.align_variable_names, AlignableColumnNodeType.VARNAMES                  );
            myNode.appendTokenColumn        (settings.align_assignment_operators, AlignableColumnNodeType.ASSIGNMENT_OPERATORS);
            myNode.appendChoiceColumn       (settings.align_terms, AlignableColumnNodeType.TERMS                              );
            myNode.appendTokenColumn        (settings.align_commas, AlignableColumnNodeType.COMMAS                            );
            myNode.appendTokenColumn        (settings.align_semicolons, AlignableColumnNodeType.STATEMENT_SEMICOLONS          );
        }
        annotations         = myNode.findColumnChoice       (AlignableColumnNodeType.ANNOTATIONS         );
        modifiers           = myNode.findModifierTokenColumn(AlignableColumnNodeType.ASSIGNMENT_MODIFIERS);
        types               = myNode.findTokenColumn        (AlignableColumnNodeType.VARTYPES            );
        names               = myNode.findTokenColumn        (AlignableColumnNodeType.VARNAMES            );
        assignmentOperators = myNode.findTokenColumn        (AlignableColumnNodeType.ASSIGNMENT_OPERATORS);
        expressions         = myNode.findColumnChoice       (AlignableColumnNodeType.TERMS               );
        commas              = myNode.findTokenColumn        (AlignableColumnNodeType.COMMAS              );
        semicolons          = myNode.findTokenColumn        (AlignableColumnNodeType.STATEMENT_SEMICOLONS);
    }

    boolean handleDocComment(final PsiVariable psiVariable)
    {
        return false;
    }

    public final void visitVariable(final PsiVariable psiVariable)
    {
        AlignableToken lastNonblankToken;
        setStatementType(LineGroup.VAR_OR_FIELD_DECLARATION);
        final int startChild = handleDocComment(psiVariable) ? 1 : 0;
        for (int i = startChild; i < psiVariable.getChildren().length; i++)
        {
            final PsiElement child = psiVariable.getChildren()[i];

            if (child == psiVariable.getModifierList())
            {
                ModifierListParser mlp = new ModifierListParser(this, codeStyleSettings, settings, tab_size, annotations, modifiers);
                child.accept(mlp);
                continue;
            }
            if (child == psiVariable.getTypeElement())
            {
                lastNonblankToken = addToken(child, types);
                i                 = appendBrackets(lastNonblankToken, psiVariable, i);
                continue;
            }
            if (child == psiVariable.getNameIdentifier())
            {
                handleNewline(2);
                lastNonblankToken = addToken(child, names);
                i                 = appendBrackets(lastNonblankToken, psiVariable, i);
                continue;
            }
            if (child instanceof PsiComment && !(child instanceof PsiDocComment))
            {
                if (child.getText().indexOf('\n') < 0)
                {
                    handleComment((PsiComment) child);
                    continue;
                }
                else
                {
                    addMultilineElement(child, null);
                }
                continue;
            }
            if (child instanceof PsiJavaToken)
            {
                final PsiJavaToken token = (PsiJavaToken) child;
                if (token.getTokenType() == JavaTokenType.COMMA)
                {
                    addComma(child, commas);
                    continue;
                }
                if (token.getTokenType() == JavaTokenType.SEMICOLON)
                {
                    addToken(new AlignableToken(child,
                                                codeStyleSettings.SPACE_BEFORE_SEMICOLON,
                                                codeStyleSettings.SPACE_AFTER_SEMICOLON  ), semicolons);
                    continue;
                }
                if (token.getTokenType() == JavaTokenType.LBRACKET ||
                    token.getTokenType() == JavaTokenType.RBRACKET   )
                {
                    // Don't assign to any column; this
                    // will cause brackets to be appended to the previous token.  Don't insert spaces.
                    addToken(token, null);
                    continue;
                }
                if (!(child instanceof PsiComment))
                {
                    handleNewline(2);
                    addToken(new AlignableToken(child,
                                                codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS    &&
                                                (!settings.no_space_before_assignment_operators.get()),
                                                codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS      ),
                             assignmentOperators                                                           );
                    continue;
                }
            }
            if (child == psiVariable.getInitializer())
            {
                handleNewline(2);
                final ExpressionParser ep = new ExpressionParser(expressions, codeStyleSettings, settings, this, 0);
                child.accept(ep);
                continue;
            }
            checkForNewline(child);
            child.accept(this);
        }
        undoIndentBias();
    }

    /**
     * if next tokens are square brackets [], append them to the last nonblank token.
     * 
     * @param lastNonblankToken 
     * @param element           
     * @param i                 
     * @return index of last child to be processed by this method.
     */
    private static int appendBrackets(final AlignableToken lastNonblankToken, final PsiElement element, final int i)
    {
        final StringBuffer buffer         = new StringBuffer();
              int          lastTokenIndex = 0;
              boolean      sawBrackets    = false;
        for (int j = i + 1; j < element.getChildren().length; j++)
        {
            final PsiElement child = element.getChildren()[j];
            if (child instanceof PsiWhiteSpace)
            {
                buffer.append(child.getText());
                continue;
            }
            if (child instanceof PsiJavaToken)
            {
                final PsiJavaToken token = (PsiJavaToken) child;
                if (token.getTokenType() == JavaTokenType.LBRACKET ||
                    token.getTokenType() == JavaTokenType.RBRACKET   )
                {
                    buffer.append(token.getText());
                    sawBrackets    = true;
                    lastTokenIndex = buffer.length();
                    continue;
                }
            }
            if (sawBrackets)
            {
                buffer.setLength(lastTokenIndex);
                lastNonblankToken.setAlternateRepresentation(lastNonblankToken.getValue() + buffer.toString());
                return j - 1;
            }
            else
                break;
        }
        return i;
    }
}