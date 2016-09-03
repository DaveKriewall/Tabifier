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
 * Parses declaration statements into the column tree.
 */
final class DeclarationParser
        extends NestedParser
{
    private final ColumnSequence declSequence;
    private       int            declIndex;

    /**
     * @param parentNode     handles the situation where multiple statements appear on one line.
     *                       This parameter points to the appropriate column in which the statement
     *                       should appear.
     * @param settings
     * @param documentParser
     */
    public DeclarationParser(final ColumnChoice      parentNode,
                             final CodeStyleSettings codeStyleSettings,
                             final TabifierSettings  settings,
                             final NestedParser      documentParser    )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
//        final ColumnSequence declNode         = parentNode.findOrAppend(ColumnSequenceNodeType.DECLARATION);     // todo - couldn't we just set declSequence here?
//              ColumnChoice   declColumnChoice = declNode.findColumnChoice(AlignableColumnNodeType.DECLARATIONS);
//        if (declColumnChoice == null)
//        {
//            declColumnChoice = declNode.appendChoiceColumn(settings.align_statements, AlignableColumnNodeType.DECLARATIONS);
//        }
//        declSequence = declColumnChoice.findOrAppend(ColumnSequenceNodeType.DECLARATION);
        declSequence = parentNode.findOrAppend(ColumnSequenceNodeType.FIELD_COMMA_PAIR);
    }

    public final void visitDeclarationStatement(final PsiDeclarationStatement psiDeclarationStatement)
    {
        declIndex = 1;

        for (int i = 0; i < psiDeclarationStatement.getChildren().length; i++)
        {
            ColumnChoice currentDeclColumnNode   = null;
            TokenColumn  currentCommaTokenColumn = null;
            while (currentDeclColumnNode == null)
            {
                currentDeclColumnNode   = (ColumnChoice) declSequence.findNth(AlignableColumnNodeType.FIELD, declIndex );
                currentCommaTokenColumn = (TokenColumn ) declSequence.findNth(AlignableColumnNodeType.COMMAS, declIndex);
                if (currentDeclColumnNode == null)
                {
                    declSequence.appendChoiceColumn(settings.align_multiple_statements, AlignableColumnNodeType.FIELD);
                    declSequence.appendTokenColumn (settings.align_commas, AlignableColumnNodeType.COMMAS            );
                }
            }

            final PsiElement child = psiDeclarationStatement.getChildren()[i];
            if (child instanceof PsiVariable)
            {
                final VariableParser vp = new VariableParser(currentDeclColumnNode, codeStyleSettings, settings, this);
                child.accept(vp);
                continue;
            }
            if (child instanceof PsiJavaToken                               &&
                ((PsiJavaToken) child).getTokenType() == JavaTokenType.COMMA   )
            {
                addComma(child, currentCommaTokenColumn);
                declIndex++;
                continue;
            }
            if (child instanceof PsiComment       &&
                !(child instanceof PsiDocComment)   )
            {
                handleComment((PsiComment) child);
                continue;
            }
            if (child instanceof PsiWhiteSpace)
            {
                if (child.getText().indexOf('\n') >= 0)
                {
                    declIndex = 1;
                }
            }
            child.accept(this);
        }
    }
}
