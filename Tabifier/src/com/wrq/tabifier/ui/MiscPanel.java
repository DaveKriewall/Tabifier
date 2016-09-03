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

import com.wrq.tabifier.tabifier;
import com.wrq.tabifier.settings.TabifierSettings;
import com.intellij.openapi.project.Project;

import javax.swing.tree.DefaultMutableTreeNode;

public class MiscPanel
extends TabifierSubsetPanel
{
    public MiscPanel(final TabifierSettings externalSettings, final Project project
    )
    {
        super(externalSettings, project,
        "Miscellaneous", "data/com/wrq/tabifier/Misc.java");
    }

    protected void createNodes(final DefaultMutableTreeNode top)
        {
            DefaultMutableTreeNode category;
            DisplayInfo[]          info;

            category = new DefaultMutableTreeNode(paneLabel);
            top.add(category);
        info = new DisplayInfo[]{
                new CheckboxDisplayInfo("Execute tabifier after Reformat plugin (if present)",
                                        settings.chain_from_reformat_plugin                   ),
                new CheckboxDisplayInfo("Run Code Layout tool before tabifying preview pane" ,
                                        settings.run_code_layout_on_preview_pane              ),
                new CheckboxDisplayInfo("Enable debug output"                                ,
                                        settings.debug                                        ),
                                                                                                };
        addDisplayInfo(info, category);

        final DefaultMutableTreeNode subcategory = new DefaultMutableTreeNode("If no selection exists:");
        category.add(subcategory);
        info = new DisplayInfo[]{
                new RadioButtonDisplayInfo("tabify entire file"                ,
                                           settings.no_selection_behavior      , false),
                new RadioButtonDisplayInfo("tabify only line containing cursor",
                                           settings.no_selection_behavior      , true ),
                                                                                        };
        addGroupedDisplayInfo(info, subcategory);
        DefaultMutableTreeNode versionLabel = new DefaultMutableTreeNode("Version " + tabifier.VERSION);
        top.add(versionLabel);
        versionLabel.setAllowsChildren(false);
    }

    protected DisplayInfo[] getDisplayInfo() {
        return null; // unused, not called by this subclass's createNodes() method.
    }
}
