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

public class MethodDeclSubsetPanel
        extends TabifierSubsetPanel
{
    public MethodDeclSubsetPanel(final TabifierSettings externalSettings,
                                 final Project project)
    {
        super(externalSettings, project,
        "Method Declarations",
        "data/com/wrq/tabifier/MethodDecl.java");
    }

    protected DisplayInfo[] getDisplayInfo() {
        return new DisplayInfo[]{
                new AlignableCheckboxDisplayInfo("Align open parend"                                      ,
                                                 "to method name"                                         ,
                                                 settings.align_method_decl_open_parend                    ),
                new AlignableCheckboxDisplayInfo("Align first parameter of each line"                     ,
                                                 "to preceding open parend"                       ,
                                                 settings.align_method_declaration_initial_params          ),
                new AlignableCheckboxDisplayInfo("Align first comma of each line"                         ,
                                                 "to parameter"                                           ,
                                                 settings.align_method_declaration_initial_param_commas    ),
                new AlignableCheckboxDisplayInfo("Align subsequent parameters"                            ,
                                                 "to preceding comma"                       ,
                                                 settings.align_method_declaration_subsequent_params       ),
                new AlignableCheckboxDisplayInfo("Align subsequent commas"                                ,
                                                 "to parameter"                                           ,
                                                 settings.align_method_declaration_subsequent_param_commas ),
                new AlignableCheckboxDisplayInfo("Align close parend"                                     ,
                                                 "to preceding token"                                     ,
                                                 settings.align_method_decl_close_parend                   ),
                                                                                                             };
    }
}
