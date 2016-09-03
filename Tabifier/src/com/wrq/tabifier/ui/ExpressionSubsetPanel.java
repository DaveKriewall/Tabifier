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

package com.wrq.tabifier.ui;

import com.intellij.openapi.project.Project;
import com.wrq.tabifier.settings.TabifierSettings;

public class ExpressionSubsetPanel
extends TabifierSubsetPanel
{
    public ExpressionSubsetPanel(final TabifierSettings externalSettings,
                                 final Project project)
    {
        super(externalSettings,
                project,
                "Expressions",
                "data/com/wrq/tabifier/Expr.java");
    }

    protected DisplayInfo[] getDisplayInfo() {
        return new DisplayInfo[]{
                new CheckboxDisplayInfo("Right justify integer literals", settings.right_justify_numeric_literals),
                new IntegerDisplayInfo("Stop aligning expressions after"      , "levels of recursion",
                                       settings.expression_parse_nesting_level                        )           ,
                new AlignableCheckboxDisplayInfo("Align Typecast open parend"                   ,
                                                 "to preceding token"                           ,
                                                 settings.align_typecast_open_parend             )                ,
                new AlignableCheckboxDisplayInfo("Align Typecast type"                          ,
                                                 "to open parend"                               ,
                                                 settings.align_typecast_type                    )                ,
                new AlignableCheckboxDisplayInfo("Align Typecast close parend"                  ,
                                                 "to type"                                      ,
                                                 settings.align_typecast_close_parend            )                ,
                new AlignableCheckboxDisplayInfo("Align conditional expression '?'"             ,
                                                 "to preceding token"                           ,
                                                 settings.align_question_mark                    )                ,
                new AlignableCheckboxDisplayInfo("Align conditional expression ':'"             ,
                                                 "to preceding token"                           ,
                                                 settings.align_colon                            )                ,
                new AlignableCheckboxDisplayInfo("Align 'new' operator"                         ,
                                                 "to preceding token"                           ,
                                                 settings.align_new                              )                ,
                new AlignableCheckboxDisplayInfo("Align 'new'ed object"                         ,
                                                 "to preceding 'new'"                           ,
                                                 settings.align_new_object                       )                ,
                new AlignableCheckboxDisplayInfo("Align arithmetic operators"                   ,
                                                 "to preceding term"                            ,
                                                 settings.align_arithmetic_operators             )                ,
                new AlignableCheckboxDisplayInfo("Align logical operators (&&, ||)"             ,
                                                 "to preceding term"                            ,
                                                 settings.align_logical_operators                )                ,
                new AlignableCheckboxDisplayInfo("Align relational operators (==, <, <=, >, >=)",
                                                 "to preceding term"                            ,
                                                 settings.align_relational_operators             )                ,
                new AlignableCheckboxDisplayInfo("Align open parends"                           ,
                                                 "to preceding token"                           ,
                                                 settings.align_other_open_parend                )                ,
                new AlignableCheckboxDisplayInfo("Align close parends"                          ,
                                                 "to preceding term"                            ,
                                                 settings.align_other_close_parend               )                ,
                                                                                                                   };
    }
}
