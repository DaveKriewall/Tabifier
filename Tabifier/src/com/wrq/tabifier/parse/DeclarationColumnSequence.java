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
package com.wrq.tabifier.parse;

import com.wrq.tabifier.settings.TabifierSettings;

/**
 * A ColumnSequence for field, variable, and method declarations.
 */
public class DeclarationColumnSequence
        extends ColumnSequence
{
    public DeclarationColumnSequence(AlignableColumn parent, int tabSize, TabifierSettings settings)
    {
        super(ColumnSequenceNodeType.DECLARATION, parent, tabSize, settings);
        appendChoiceColumn       (settings.align_annotations, AlignableColumnNodeType.ANNOTATIONS                  );
        appendModifierTokenColumn(settings.align_modifiers                                                         );
        appendTokenColumn        (settings.align_variable_types, AlignableColumnNodeType.VARTYPES                  );
        appendTokenColumn        (settings.align_variable_names, AlignableColumnNodeType.VARNAMES                  );
//        appendChoiceColumn       (settings.align_method_decl_open_parend, AlignableColumnNodeType.PARAMS           );   todo
        appendTokenColumn        (settings.align_assignment_operators, AlignableColumnNodeType.ASSIGNMENT_OPERATORS);
        appendChoiceColumn       (settings.align_terms, AlignableColumnNodeType.TERMS                              );
        appendTokenColumn        (settings.align_commas, AlignableColumnNodeType.COMMAS                            );
        appendTokenColumn        (settings.align_semicolons, AlignableColumnNodeType.STATEMENT_SEMICOLONS          );
    }
}
