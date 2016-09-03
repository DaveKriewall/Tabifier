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
 * Parses class declarations into the column tree.
 */
final class ClassParser
        extends NestedParser
{
    private final ColumnChoice   parent;
    private final ColumnSequence fieldSequence;
    private final TokenColumn    braces;
    private final TokenColumn    trailingComments;
    private       int            columnIndex;

    /*
     *     parent
     *       |
     *     BRACES
     *       |
     *     CLASS_SEQ: --> class --> trailing comments
     *                          |
     *                        field --> ...
     *
     */
    public ClassParser(final ColumnChoice      parent,
                       final CodeStyleSettings codeStyleSettings,
                       final TabifierSettings  settings,
                       final NestedParser      documentParser    )
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        this.parent = parent;
        final ColumnSequence braceSeq      = parent.findOrAppend(ColumnSequenceNodeType.BRACES   );
        final ColumnSequence classSequence = parent.findOrAppend(ColumnSequenceNodeType.CLASS_SEQ);
        if (braceSeq.findTokenColumn(AlignableColumnNodeType.BRACES) == null) {
            braceSeq.appendTokenColumn(settings.align_braces, AlignableColumnNodeType.BRACES);
        }
        braces = braceSeq.findTokenColumn(AlignableColumnNodeType.BRACES);
        ColumnChoice classColumnChoice = classSequence.findColumnChoice(AlignableColumnNodeType.CLASS);
        if (classColumnChoice == null) {
            classColumnChoice = classSequence.appendChoiceColumn(settings.align_class, AlignableColumnNodeType.CLASS);
        }
        fieldSequence = classColumnChoice.findOrAppend(ColumnSequenceNodeType.FIELD);
        TokenColumn tc = classSequence.findTokenColumn(AlignableColumnNodeType.TRAILING_COMMENTS);
        if (tc == null) {
            tc = classSequence.appendTokenColumn(
                    settings.align_trailing_comments,
                    AlignableColumnNodeType.TRAILING_COMMENTS
                                                             );
        }
        trailingComments = tc;
    }

    protected ColumnChoice getClassColumn()
    {
        return parent;
    }

    public TokenColumn getTrailingComments()
    {
        return trailingComments;
    }

    public final void visitClass(final PsiClass psiClass)
    {
        columnIndex = 0;
        boolean sawLBrace = false;

        for (int i = 0; i < psiClass.getChildren().length; i++) {
                  ColumnChoice currentFieldColumnNode  = null;
                  TokenColumn  currentCommaTokenColumn = null;

            final PsiElement   child                   = psiClass.getChildren()[i];
            if (child instanceof PsiField            ||
                child instanceof PsiMethod           ||
                child instanceof PsiClassInitializer   ) {
                columnIndex++;
            }
            while (currentFieldColumnNode == null && columnIndex > 0) {
                currentFieldColumnNode  =
                        (ColumnChoice) fieldSequence.findNth(AlignableColumnNodeType.FIELD, columnIndex );
                currentCommaTokenColumn =
                        (TokenColumn ) fieldSequence.findNth(AlignableColumnNodeType.COMMAS, columnIndex);
                if (currentFieldColumnNode == null) {
                    fieldSequence.appendChoiceColumn(
                            settings.align_multiple_statements, AlignableColumnNodeType.FIELD
                                                                                             );
                    fieldSequence.appendTokenColumn (settings.align_commas, AlignableColumnNodeType.COMMAS);
                }
            }

            /**
             * skip everything up to the left bracket.
             */
            if (!sawLBrace && child != psiClass.getLBrace()) {
                child.accept(this);
                continue;
            }

            if (child == psiClass.getLBrace()) {
                sawLBrace = true;
                final AlignableToken t = new AlignableToken(child);
                if (codeStyleSettings.SPACE_BEFORE_CLASS_LBRACE) {
                    t.setAlternateRepresentation(" {");
                }
                addToken(t, braces);
                bumpIndentLevel((PsiJavaToken) child);
                scheduleAlignment("saw Class LBRACE");
                continue;
            }
            if (child == psiClass.getRBrace()) {
                reduceIndentLevel((PsiJavaToken) child);
                addToken(child, braces);
                scheduleAlignment("saw Class RBRACE");
                continue;
            }
            if (child instanceof PsiField) {
                final FieldParser fp = new FieldParser(currentFieldColumnNode, codeStyleSettings, settings, this);
                child.accept(fp);
                continue;
            }
            if (child instanceof PsiJavaToken                               &&
                ((PsiJavaToken) child).getTokenType() == JavaTokenType.COMMA   ) {
                addComma(child, currentCommaTokenColumn);
                continue;
            }
            if (child instanceof PsiComment) {
                handleComment((PsiComment) child);
                continue;
            }
            if (child instanceof PsiMethod           ||
                child instanceof PsiClassInitializer   ) {
                final MethodParser mp = new MethodParser(currentFieldColumnNode, codeStyleSettings, settings, this);
                child.accept(mp);
                continue;
            }
            if (child instanceof PsiWhiteSpace) {
                if (child.getText().indexOf('\n') >= 0) {
                    columnIndex = 0;
                }
            }
            child.accept(this); // handle anything else we don't recognize
        }
    }
}
