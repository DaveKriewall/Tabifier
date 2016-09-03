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

import com.wrq.tabifier.settings.TabifierSettings;
import com.intellij.openapi.project.Project;

public class GroupingPanel extends TabifierSubsetPanel
{
    public GroupingPanel(final TabifierSettings externalSettings, final Project project)
    {
        super(externalSettings,
                project,
        "Line Grouping",
        "data/com/wrq/tabifier/Grouping.java");
    }

    protected DisplayInfo[] getDisplayInfo() {
        return
         new DisplayInfo[]{
                new CheckboxDisplayInfo("Blank lines delimit alignable groups"                        ,
                                        settings.delimit_by_blank_lines                                ),
                new CheckboxDisplayInfo("Non-blank lines (non-tabifiable statements) delimit groups"  ,
                                        settings.delimit_by_non_blank_lines                            ),
                new CheckboxDisplayInfo("Group statements of different types separately"              ,
                                        settings.delimit_by_statement_type                             ),
                new CheckboxDisplayInfo("Group declarations with assignments (override previous rule)",
                                        settings.group_assignments_with_declarations                   ),
                                                                                                         };

    }
}
