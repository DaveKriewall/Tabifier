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
import com.wrq.tabifier.parse.TokenColumn;
import com.wrq.tabifier.parse.LineGroup;
import com.wrq.tabifier.parse.ColumnChoice;
import com.wrq.tabifier.parse.AlignableToken;
import com.wrq.tabifier.settings.TabifierSettings;

/**
 * Superclass for all parsers to handle common methods.
 */
public abstract class NestedParser
        extends JavaRecursiveElementVisitor
{
    private final NestedParser      superParser;
    final         CodeStyleSettings codeStyleSettings;
    final         TabifierSettings  settings;
    final         int               tab_size;
    private       int               indentBias        = 0;
    private       boolean           sawNewline        = false;

    protected NestedParser(final NestedParser      superParser,
                           final CodeStyleSettings codeStyleSettings,
                           final TabifierSettings  settings,
                           final int               tab_size          )
    {
        this.superParser       = superParser;
        this.codeStyleSettings = codeStyleSettings;
        this.settings          = settings;
        this.tab_size          = tab_size;
    }

    public void visitElement(final PsiElement psiElement)
    {
        if (superParser != null && psiElement.getChildren().length == 0)
        {
            psiElement.accept(superParser);
        }
        else
            super.visitElement(psiElement);
    }

    AlignableToken addToken(final PsiElement element, final TokenColumn tokenColumn)
    {
        return superParser.addToken(element, tokenColumn);
    }

    AlignableToken addToken(final AlignableToken token, final TokenColumn tokenColumn)
    {
        return superParser.addToken(token, tokenColumn);
    }

    public void visitReferenceExpression(final PsiReferenceExpression psiReferenceExpression)
    {
        visitExpression(psiReferenceExpression); //  todo
//        psiReferenceExpression.accept(this);   // - or -
//        for (int i = 0; i < psiReferenceExpression.getChildren().length; i++)
//        {
//            PsiElement e = psiReferenceExpression.getChildren()[i];
//            e.accept(this);
//        }
    }

    void scheduleAlignment(final String reason)
    {
        superParser.scheduleAlignment(reason);
    }

    void bumpIndentLevel(final PsiJavaToken token)
    {
        superParser.bumpIndentLevel(token);
    }

    void reduceIndentLevel(final PsiJavaToken token)
    {
        superParser.reduceIndentLevel(token);
    }

    protected void suspendStatementTypeChecking()
    {
        superParser.suspendStatementTypeChecking();
    }

    protected void resumeStatementTypeChecking()
    {
        superParser.resumeStatementTypeChecking();
    }

    void setStatementType(final LineGroup.LineType type)
    {
        superParser.setStatementType(type);
    }

    ColumnChoice getClassColumn()
    {
        return superParser.getClassColumn();
    }
    /**
     * Utility routine to add requisite spaces before and/or after comma.
     * @param comma PsiElement which contains a comma.
     * @param commaTokenColumn column into which the comma will be placed.
     */
    final void addComma(final PsiElement comma, final TokenColumn commaTokenColumn)
    {
        final AlignableToken token = new AlignableToken(comma,
                                                        codeStyleSettings.SPACE_BEFORE_COMMA,
                                                        codeStyleSettings.SPACE_AFTER_COMMA  );
        addToken(token, commaTokenColumn);
    }

    void adjustIndentBias(final int adjustment)
    {
        superParser.adjustIndentBias(adjustment);
    }

    boolean isCurrentLineBlank()
    {
        return superParser.isCurrentLineBlank();
    }
    /**
     * if a newline character has been seen prior to the token being handled, mark it as belonging to a new line
     * with an adjusted indentation.
     */
    final void handleNewline(final int bias)
    {
        if (sawNewline) {
            adjustIndentBias(bias);
            indentBias += bias;
            sawNewline =  false;
        }
    }

    final void checkForNewline(final PsiElement child)
    {
        sawNewline = false;
        if (child instanceof PsiWhiteSpace) {
            if (child.getText().indexOf('\n') >= 0) {
                sawNewline = true;
            }
        }
    }

    final boolean isSawNewline()
    {
        return sawNewline;
    }

    final void undoIndentBias()
    {
        adjustIndentBias(-indentBias);
        indentBias = 0;
    }

    AlignableToken addMultilineElement(final PsiElement element, final TokenColumn column)
    {
        return superParser.addMultilineElement(element, column);
    }

    void handleComment(PsiComment comment)
    {
        superParser.handleComment(comment);
    }
}
