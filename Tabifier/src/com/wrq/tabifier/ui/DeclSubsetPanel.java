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

public class DeclSubsetPanel
extends TabifierSubsetPanel
{
    public DeclSubsetPanel(final TabifierSettings externalSettings,
                           final Project project)
    {
        super(externalSettings,
                project,
                "Declarations/Assignments",
                "data/com/wrq/tabifier/Decl.java");
    }

    protected DisplayInfo[] getDisplayInfo() {
        return  new DisplayInfo[]{
                new AlignableCheckboxDisplayInfo("Align 2nd and subsequent annotations on a line",
                                                 "to preceding token"       ,
                                                 settings.align_annotations             ),
                new AlignableCheckboxDisplayInfo("Align annotation open parends",
                                                 "to preceding token",
                                                 settings.align_annotation_open_parend),
                new AlignableCheckboxDisplayInfo("Align annotation close parends"                   ,
                                                 "to preceding token"       ,
                                                 settings.align_annotation_close_parend),
                new AlignableCheckboxDisplayInfo("Align modifiers"                   ,
                                                 "to preceding token (if any)"       ,
                                                 settings.align_modifiers             ),
                new AlignableCheckboxDisplayInfo("Align field/method/variable types"              ,
                                                 "to longest modifier"               ,
                                                 settings.align_variable_types        ),
                new AlignableCheckboxDisplayInfo("Align field/method/variable names"              ,
                                                 "to longest variable type"          ,
                                                 settings.align_variable_names        ),
                new AlignableCheckboxDisplayInfo("Align assignment operators (\"=\")",
                                                 "to longest variable name"          ,
                                                 settings.align_assignment_operators  ),
                new AlignableCheckboxDisplayInfo("Align expressions"                 ,
                                                 "to assignment operator (\"=\")"    ,
                                                 settings.align_terms                 ),
                new AlignableCheckboxDisplayInfo("Align commas"                      ,
                                                 "to longest expression"             ,
                                                 settings.align_commas                ),
                new AlignableCheckboxDisplayInfo("Align semicolons"                  ,
                                                 "to longest preceding token"        ,
                                                 settings.align_semicolons            ),
                new AlignableCheckboxDisplayInfo("Align trailing comments"           ,
                                                 "to comma or semicolon"             ,
                                                 settings.align_trailing_comments     ),
                                                                                        };
    }
}
