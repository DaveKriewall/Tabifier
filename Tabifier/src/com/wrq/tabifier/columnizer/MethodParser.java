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
import com.wrq.tabifier.parse.ColumnSequenceNodeType;
import com.wrq.tabifier.settings.TabifierSettings;

/**
 * Parses method declarations and method body into the column tree.   If the method body (method's code block) is
 * contained on one line, parse it horizontally so that it can be aligned with adjacent code blocks.
 */
public class MethodParser
        extends NestedParser
{
    private final ColumnChoice        parent;
    private final ColumnSequence      methodDeclListSequence;
    private final ColumnChoice        annotations;
    private final ModifierTokenColumn modifiers;
    private final TokenColumn         types;
    private final TokenColumn         names;
    private final ColumnSequence      parameterDecls;
    private final TokenColumn         semicolons;

    public MethodParser(final ColumnChoice parent,
                        final CodeStyleSettings codeStyleSettings,
                        final TabifierSettings settings,
                        final NestedParser documentParser)
    {
        super(documentParser, codeStyleSettings, settings, documentParser.tab_size);
        this.parent = parent;
        methodDeclListSequence = parent.findOrAppend(ColumnSequenceNodeType. METHOD_DECLARATION_PARAMETERS);
        if (methodDeclListSequence.findModifierTokenColumn(AlignableColumnNodeType.ASSIGNMENT_MODIFIERS) == null)
        {
            methodDeclListSequence.appendChoiceColumn       (settings.align_annotations, AlignableColumnNodeType.ANNOTATIONS        );
            methodDeclListSequence.appendModifierTokenColumn(settings.align_modifiers                                               );
            methodDeclListSequence.appendTokenColumn        (settings.align_variable_types, AlignableColumnNodeType.VARTYPES        );
            methodDeclListSequence.appendTokenColumn        (settings.align_variable_names, AlignableColumnNodeType.VARNAMES        );
            methodDeclListSequence.appendChoiceColumn       (settings.align_method_decl_open_parend, AlignableColumnNodeType.PARAMS );
            methodDeclListSequence.appendTokenColumn        (settings.align_semicolons, AlignableColumnNodeType.STATEMENT_SEMICOLONS);
        }
        annotations    = methodDeclListSequence.findColumnChoice(AlignableColumnNodeType.ANNOTATIONS);
        modifiers      = methodDeclListSequence.findModifierTokenColumn(AlignableColumnNodeType.ASSIGNMENT_MODIFIERS);
        types          = methodDeclListSequence.findTokenColumn(AlignableColumnNodeType.VARTYPES            );
        names          = methodDeclListSequence.findTokenColumn(AlignableColumnNodeType.VARNAMES            );
        ColumnChoice c = methodDeclListSequence.findColumnChoice(AlignableColumnNodeType.PARAMS             );
        parameterDecls = c.findOrAppend(ColumnSequenceNodeType.PARAMLIST);
        semicolons     = methodDeclListSequence.findTokenColumn(AlignableColumnNodeType.STATEMENT_SEMICOLONS);
    }

    public final void visitMethod(final PsiMethod psiMethod)
    {
        final LineGroup.LineType lineType;
        if ( //psiMethod.getBody() == null ||   // todo - remove
                psiMethod.getText().indexOf('\n') >= 0) {
                    lineType = LineGroup.MULTILINE_METHOD_DECLARATION;
                    scheduleAlignment("Forcing multiline method to separate group");
                }
        else {
            lineType = LineGroup.SINGLELINE_METHOD_DECLARATION;
        }
        setStatementType(lineType);
        for (int i = 0; i < psiMethod.getChildren().length; i++)
        {
            final PsiElement child = psiMethod.getChildren()[i];
            if (child == psiMethod.getModifierList())
            {
                ModifierListParser mlp = new ModifierListParser(this, codeStyleSettings, settings, tab_size, annotations, modifiers);
                child.accept(mlp);
                continue;
            }
            if (child == psiMethod.getReturnTypeElement())
            {
                addToken(child, types);
                continue;
            }
            if (child == psiMethod.getNameIdentifier())
            {
                addToken(child, names);
                continue;
            }
            if (child == psiMethod.getParameterList())
            {
//                setStatementType(LineGroup.MULTILINE_METHOD_DECLARATION);
                suspendStatementTypeChecking();
                final ParameterListParser plp = new ParameterListParser(
                        parameterDecls,
                        codeStyleSettings,
                        settings,
                        this);
                child.accept(plp);
//                setStatementType(LineGroup.NONE);
                resumeStatementTypeChecking();
                /**
                 * The setting "delimit method declarations" should only apply to methods with a horizontal
                 * code block.  Always delimit all other methods (abstract, or those with a multiline code block.)
                 */
                if (// psiMethod.getBody()               == null  ||      todo - remove
                    psiMethod.getText().indexOf('\n') >= 0     ||
                    settings.delimit_method_declarations.get()   )
                {
                    scheduleAlignment("delimit method declarations");
                }
                continue;
            }
            if (child instanceof PsiJavaToken && child.getText().equals(";"))
            {
                addToken(child, semicolons);
                continue;
            }
            if (child instanceof PsiComment && !(child instanceof PsiDocComment))
            {
                handleComment((PsiComment) child);
                continue;

            }
            child.accept(this);
        }
    }

    /**
     * A code block consists of an open bracket, a choice of statement types, followed by a close bracket.
     * The brackets will never be aligned, so we don't have to put them in columns.
     *
     * @param psiCodeBlock PsiElement corresponding to the code block.
     */
    public final void visitCodeBlock(final PsiCodeBlock psiCodeBlock)
    {
        ColumnChoice choice;
        if (psiCodeBlock.getText().indexOf('\n') < 0)  {
            // this is a horizontal code block.  Place the block after the parameter list.
            if (methodDeclListSequence.findColumnChoice(AlignableColumnNodeType.CODE_BLOCK) == null) {
                methodDeclListSequence.appendChoiceColumn(settings.align_code_block, AlignableColumnNodeType.CODE_BLOCK);
            }
            choice = methodDeclListSequence.findColumnChoice(AlignableColumnNodeType.CODE_BLOCK);
        }
        else {
            choice = parent;
        }
        final CodeBlockParser cbp = new CodeBlockParser(choice,
                                                  codeStyleSettings,
                                                  settings,
                                                  this,
                codeStyleSettings.SPACE_BEFORE_METHOD_LBRACE);
        psiCodeBlock.accept(cbp);
    }


}
