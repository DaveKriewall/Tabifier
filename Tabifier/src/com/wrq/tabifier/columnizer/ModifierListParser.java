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
 * Parses modifier lists.  These used to be simple, before annotations; they consisted of simply the list of
 * modifiers.  Annotations can span multiple lines, and may or may not have parameter lists.
 */
public class ModifierListParser extends NestedParser
{
    private final ColumnChoice annotations;
    private final ModifierTokenColumn modifiers;

    public ModifierListParser(
            NestedParser superParser,
            CodeStyleSettings codeStyleSettings,
            TabifierSettings settings,
            int tab_size,
            ColumnChoice annotations,
            ModifierTokenColumn modifiers)
    {
        super(superParser, codeStyleSettings, settings, tab_size);
        this.annotations = annotations;
        this.modifiers = modifiers;
    }

    @Override
    public void visitModifierList(PsiModifierList child)
    {
        // Annotation support is completely re-architected now to place each annotation in a separate column.
        // The "annotations" ColumnChoice will contain a tree of all the annotation text.  It consists of a single
        // ColumnSequence which in turn contains as many ColumnChoices as there are annotations on a given line.
        // Each of these per-annotation columns contains a columnSequence of annotation name, open parenthesis, reference parameter list, and close parenthesis.
        // (An annotation without any parameters would therefore only contribute a token to the annotation name column.)
        // (For now, just put all the name-value pairs into the reference parameter list as tokens, no alignment.)
        //
        final ColumnSequence sequence = annotations.findOrAppend(ColumnSequenceNodeType.ANNOTATION_GROUP);
        int annotationIndex = 1; // 1 = first annotation on a line, 2 = 2nd, etc.
        boolean keywordSeen = false;
        int firstKeywordOffset = 0;
        for (PsiElement c : child.getChildren())
        {
            if (c instanceof PsiAnnotation)
            {
                ColumnChoice annotationColumn = (ColumnChoice) sequence.findNth(AlignableColumnNodeType.ANNOTATIONS, annotationIndex);
                annotationIndex++;
                if (annotationColumn == null)
                {
                    annotationColumn = sequence.appendChoiceColumn(settings.align_annotations, AlignableColumnNodeType.ANNOTATIONS);
                }
                PsiAnnotation annotation = (PsiAnnotation) c;
                ColumnSequence annotationSequence = annotationColumn.findOrAppend(ColumnSequenceNodeType.ANNOTATION);
                TokenColumn annotationName = annotationSequence.findTokenColumn(AlignableColumnNodeType.METHOD_NAME);
                if (annotationName == null) annotationName = annotationSequence.appendTokenColumn(settings.align_annotations, AlignableColumnNodeType.METHOD_NAME);
                TokenColumn lParenthesis = annotationSequence.findTokenColumn(AlignableColumnNodeType.OPEN_PAREND);
                if (lParenthesis == null) lParenthesis = annotationSequence.appendTokenColumn(settings.align_annotation_open_parend, AlignableColumnNodeType.OPEN_PAREND);
                TokenColumn valuePairs = annotationSequence.findTokenColumn(AlignableColumnNodeType.PARAMS);
                if (valuePairs == null) valuePairs = annotationSequence.appendTokenColumn(settings.align_annotation_value_pairs, AlignableColumnNodeType.PARAMS);
                TokenColumn rParenthesis = annotationSequence.findTokenColumn(AlignableColumnNodeType.CLOSE_PAREND);
                if (rParenthesis == null) rParenthesis = annotationSequence.appendTokenColumn(settings.align_annotation_close_parend, AlignableColumnNodeType.CLOSE_PAREND);
                PsiElement atElement = null;
                for (PsiElement annotationChild : annotation.getChildren())
                {
                    if (annotationChild instanceof PsiJavaToken && annotationChild == annotation.getChildren()[0])
                    {
                        atElement = annotationChild;
                    }
                    else if (annotationChild instanceof PsiJavaCodeReferenceElement)
                    {
                        AlignableToken name = new AlignableToken(atElement);
                        name.setAlternateRepresentation("@" + annotationChild.getText());
                        addToken(name, annotationName);
                    }
                    else if (annotationChild instanceof PsiAnnotationParameterList)
                    {
                        PsiAnnotationParameterList list = (PsiAnnotationParameterList) annotationChild;
                        for (PsiElement paramListChild : list.getChildren())
                        {
                            if (paramListChild == list.getFirstChild() && paramListChild instanceof PsiJavaToken)
                            {
                                addToken(paramListChild, lParenthesis);
                            }
                            else if (paramListChild == list.getLastChild() && paramListChild instanceof PsiJavaToken)
                            {
                                addToken(paramListChild, rParenthesis);
                            }
                            else if (paramListChild instanceof PsiNameValuePair)
                            {
                                addToken(paramListChild, valuePairs);
                            }
                            else paramListChild.accept(this);
                        }
                    }
                    else
                    {
                        annotationChild.accept(this);
                    }
                }
            }
            else
            {
                if (c instanceof PsiKeyword && !keywordSeen) {
                    keywordSeen = true;
                    firstKeywordOffset = c.getTextOffset() - child.getTextOffset();
                }
                if (!keywordSeen) {
                    checkForNewline(c);
                    if (isSawNewline())
                    {
                        // if we encounter white space containing a \n between annotations, reset the annotation index to 1
                        annotationIndex = 1;
                    }
                    c.accept(this);
                }
            }
        }
        if (keywordSeen)
        {
            addToken(new AlignableToken(child, firstKeywordOffset, child.getTextLength() - firstKeywordOffset), modifiers);
        }
    }
}
