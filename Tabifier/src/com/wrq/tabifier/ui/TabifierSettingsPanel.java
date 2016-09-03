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
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Contains code for tabifier settings panel.
 */
public final class TabifierSettingsPanel
        extends JPanel
{
    private static final Logger           logger              = Logger.getLogger("com.wrq.tabifier.PreferencesPanel");

    private final TabifierSettings          settings;
    private final Project                   project;
    private       List<TabifierSubsetPanel> subsetPanels;

    public TabifierSettings getSettings() {
        return settings;
    }

    public TabifierSettingsPanel(final TabifierSettings externalSettings, final Project project)
    {
        logger.debug("TabifierSettingsPanel constructor, project=" + project);
        this.project = project;
        subsetPanels = new ArrayList<TabifierSubsetPanel>();
        settings     = (TabifierSettings) externalSettings.deepCopy();
        logger.debug("settings copied, adding change listener");
        setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor     = GridBagConstraints.NORTHWEST;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.gridwidth  = GridBagConstraints.REMAINDER;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.weightx    = 1.0;
        constraints.weighty    = 1.0;
        add(getSettingsPanel(), constraints);
    }

    private Component getSettingsPanel()
    {
        final JTabbedPane containerPanel = new JTabbedPane();
        // add all the panes.
        subsetPanels.add(new DeclSubsetPanel(settings, project));
        subsetPanels.add(new ExpressionSubsetPanel(settings, project));
        subsetPanels.add(new MethodDeclSubsetPanel(settings, project));
        subsetPanels.add(new MethodCallSubsetPanel(settings, project));
        subsetPanels.add(new IfStmtSubsetPanel(settings, project));
        subsetPanels.add(new SpacingOptionsPanel(settings, project));
        subsetPanels.add(new GroupingPanel(settings, project));
        subsetPanels.add(new MiscAlignmentPanel(settings, project));
        subsetPanels.add(new MiscPanel(settings, project));

        ListIterator li = subsetPanels.listIterator();
        while (li.hasNext()) {
            TabifierSubsetPanel panel = (TabifierSubsetPanel) li.next();
            containerPanel.addTab(panel.getPaneLabel(), panel);
        }
        return containerPanel;
    }

    public void dispose()
    {
        ListIterator li = subsetPanels.listIterator();
        while (li.hasNext()) {
            TabifierSubsetPanel panel = (TabifierSubsetPanel) li.next();
            panel.dispose();
        }
    }

}