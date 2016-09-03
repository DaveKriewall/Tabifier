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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.util.IncorrectOperationException;
import com.wrq.tabifier.TabifierActionHandler;
import com.wrq.tabifier.settings.*;
import com.wrq.tabifier.util.Constraints;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EventObject;

/**
 * Pane consisting of a subset of Tabifier settings and and corresponding preview text.
 */
public abstract class TabifierSubsetPanel
        extends JPanel
        implements ISettingsChangeListener
{
    private   static final Logger           logger              = Logger.getLogger("com.wrq.tabifier.TabifierSubsetPanel");

    protected        final TabifierSettings settings;
    private                String           previewString       = null;
    private                Editor           myEditor;
    private          final Project          project;

    private                JPanel           outerContainerPanel;
    protected        final String           paneLabel;
    protected        final String           previewTextURI;

    public TabifierSubsetPanel(final TabifierSettings externalSettings,
                               final Project          project,
                                     String           paneLabel,
                                     String           previewTextURI   )
    {
        this.project        = project;
        settings            = externalSettings;
        this.paneLabel      = paneLabel;
        this.previewTextURI = previewTextURI;
        settings.addChangeListener(this);
        setLayout(new GridBagLayout());
        final Constraints constraints = new Constraints(GridBagConstraints.NORTHWEST);
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.gridwidth  = GridBagConstraints.REMAINDER;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.weightx    = 1.0d;
        constraints.weighty    = 1.0d;
        init();
        add(getSettingsPanel(), constraints);
    }

    private String getPreviewText()
    {
        if (previewString == null)
        {
            final InputStream  stream =
                    this.getClass().getClassLoader().getResourceAsStream(previewTextURI);
            final StringBuffer s      = new StringBuffer(1024);
            if (stream != null)
            {
                final byte[] buffer = new byte[1024];
                      int    length;
                try
                {
                    while ((length = stream.read(buffer)) > 0)
                    {
                        s.append(new String(buffer, 0, length));
                    }
                }
                catch (IOException e)
                {
                    logger.error("getPreviewText() failed:", e);
                }
            }
            else {
                logger.warn("could not open preview text for:" + previewTextURI);
            }
            final String result = s.toString();
            previewString = result.replaceAll("\r\n", "\n");
        }
        return previewString;
    }

    public String getPaneLabel() {
        return paneLabel;
    }

    private JPanel getSettingsPanel()
    {
        final JPanel containerPanel = new JPanel(new GridBagLayout());

        // Create the nodes.
        final DefaultMutableTreeNode top = new DefaultMutableTreeNode("node not visible");
        createNodes(top);
        JTree tree;
        tree = new JTree(top);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setRowHeight  (    0);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        final SettingTreeCellRenderer renderer = new SettingTreeCellRenderer();
        tree.setCellRenderer(renderer                       );
        tree.setCellEditor  (new SettingTreeCellEditor(tree));
        tree.setEditable(true);
        /** expand all nodes. */
        for (int i = 0; i < tree.getRowCount(); i++)
        {
            tree.expandRow(i);
        }
        //Create the scroll pane and add the tree to it.
        final JScrollPane treeView = new JScrollPane(tree);

        //Add the scroll panes to a split pane.
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(getTextArea());

        final Dimension minimumSize = new Dimension(100, 50);
        treeView.setMinimumSize(minimumSize);
        final int width = treeView.getPreferredSize().width;
        splitPane.setDividerLocation(width);

        splitPane.setPreferredSize(new Dimension(800, 600));
        // Add the split pane to this frame.
        final Constraints constraints = new Constraints(GridBagConstraints.NORTHWEST);
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.weightedLastRow();
        containerPanel.add(splitPane, constraints.weightedLastCol());
        outerContainerPanel = containerPanel;
        return containerPanel;
    }


    private JPanel getTextArea()
    {
        final JPanel             panel       = new JPanel(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        final Border             etched      = BorderFactory.createEtchedBorder();
        final TitledBorder       title       = BorderFactory.createTitledBorder(etched, "Preview");
        title.setTitleJustification(TitledBorder.LEFT);
        panel.setBorder(title);

        final JScrollPane areaScrollPane;
        areaScrollPane = new JScrollPane(myEditor.getComponent());
        areaScrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        constraints.anchor    = GridBagConstraints.NORTHWEST;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx   = 1.0d;
        constraints.weighty   = 1.0d;
        constraints.fill      = GridBagConstraints.BOTH;
        constraints.gridx     =                            0;
        constraints.gridy     =                            0;
        panel.add(areaScrollPane, constraints);
        logger.debug("initial updatePreview in getTextArea()");
        updatePreview();
        return panel;
    }

    public final void dispose()
    {
        final EditorFactory editorFactory = EditorFactory.getInstance();
        editorFactory.releaseEditor(myEditor);
    }

    public final void settingsChange(final Setting setting)
    {
        logger.debug("setting change:" + setting);
        if (setting == settings.debug)
        {
            Logger.getLogger("com.wrq.tabifier").setLevel(settings.debug.get() ? Level.DEBUG : Level.INFO);
        }
        else if (setting == settings.no_selection_behavior      ||
                 setting == settings.chain_from_reformat_plugin   )
        {
            // don't update preview pane; nothing affects appearance.
        }
        else
        {
            logger.debug("update preview; setting change for " + setting);
            updatePreview();
        }
    }

    protected void addDisplayInfo(final DisplayInfo[] diarray, final DefaultMutableTreeNode category)
    {
        int maxWidth = 0;
        for (DisplayInfo displayInfo : diarray)
        {
            final DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(displayInfo);
            category.add(dmtn);
            displayInfo.setOwner(dmtn);
            final Dimension d = displayInfo.getRendererComponent().getPreferredSize();
            if (d.width > maxWidth) maxWidth = d.width;
        }
        /**
         * set the preferred width of all objects in this group to the maximum for appearance's sake.
         */
        for (DisplayInfo displayInfo : diarray)
        {
            Dimension d = displayInfo.getRendererComponent().getPreferredSize();
            d = new Dimension(maxWidth, d.height);
            displayInfo.getRendererComponent().setPreferredSize(d);
        }
    }

    protected void addGroupedDisplayInfo(final DisplayInfo[] diarray, final DefaultMutableTreeNode category)
    {
        addDisplayInfo(diarray, category);
        final ButtonGroup group = new ButtonGroup();
        for (DisplayInfo displayInfo : diarray)
        {
            final RadioButtonDisplayInfo di = (RadioButtonDisplayInfo) displayInfo;
            group.add(di.button);
        }
        /**
         * now select the one that corresponds to the state of the setting.
         */
        for (DisplayInfo displayInfo : diarray)
        {
            final RadioButtonDisplayInfo di = (RadioButtonDisplayInfo) displayInfo;
            if (((BooleanSetting) di.setting).get() ? !di.correspondsToFalse : di.correspondsToFalse)
            {
                di.button.setSelected(true);
            }
        }
    }

    protected void createNodes(final DefaultMutableTreeNode top)
    {
        DefaultMutableTreeNode category;
        DisplayInfo[]          info;

        category = new DefaultMutableTreeNode(paneLabel);
        top.add(category);
        info = getDisplayInfo();
        addDisplayInfo(info, category);
    }

    abstract protected DisplayInfo[] getDisplayInfo();

    private void init()
    {
        final EditorFactory                        editorFactory = EditorFactory.getInstance();
        final com.intellij.openapi.editor.Document doc           = editorFactory.createDocument("");
        myEditor = editorFactory.createViewer(doc);
    }

    private void updatePreview()
    {
        final Runnable task = new Runnable()
        {
            public void run()
            {
                try
                {
                    final Project            project  = ProjectManager.getInstance().getDefaultProject();
                    final PsiFileFactory     factory  = PsiFileFactory.getInstance(project);
                    final Document           document = myEditor.getDocument();
                    /**
                     * create a psiFile that contains the text from the preview pane.
                     */
                    final PsiFile psiFile = factory.createFileFromText("a.java", getPreviewText());
                    /**
                     * run the IDEA code layout reformatter on it.
                     */
                    if (settings.run_code_layout_on_preview_pane.get())
                    {
                        final CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
                        codeStyleManager.reformat(psiFile);
                    }
                    /**
                     * create a document that contains the newly reformatted text from the psiFile.
                     */
                    final EditorFactory editorFactory = EditorFactory.getInstance();
                    final com.intellij.openapi.editor.Document doc2 = editorFactory.createDocument(psiFile.getText());
                    /**
                     * now tabify the reformatted text.
                     */
                    final TabifierActionHandler wa         = new TabifierActionHandler();
                    final CodeStyleSettings     cssettings = TabifierActionHandler.getCodeStyleSettings(project);
                    wa.tabifyPsiFile(psiFile                              ,
                                     0                                    ,
                                     psiFile.getTextRange().getEndOffset(),
                                     cssettings                           ,
                                     settings                             ,
                                     doc2                                  );
                    /**
                     * And place the tabified text into the preview pane.
                     */
                    String s = doc2.getText();
                    document.replaceString(0, document.getTextLength(), s);
                }
                catch (IncorrectOperationException e)
                {
                    logger.error(e); // Can't actually happen if preview text is correct.
                }
            }
        };

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                ApplicationManager.getApplication().runWriteAction(new Runnable()
                {
                    public void run()
                    {
                        final CommandProcessor x3;
                        x3 = CommandProcessor.getInstance();
                        logger.debug("runWriteAction(UpdatePreview) task executing");
                        x3.executeCommand(project, task, "UpdatePreview", null);
                        logger.debug("runWriteAction(UpdatePreview) task finished");
                    }
                });
            }
        });
        logger.debug("exiting updatePreview");
    }


    abstract class DisplayInfo
    {
        final String                 before;
        final Setting                setting;
              DefaultMutableTreeNode owner;

        public DisplayInfo(final String before, final Setting setting)
        {
            this.before  = before;
            this.setting = setting;
        }

        String getOneLineDescription()
        {
            return before;
        }

        final void setOwner(final DefaultMutableTreeNode owner)
        {
            this.owner = owner;
        }

        abstract JComponent getRendererComponent();

        abstract MyCellEditor getEditorComponent();

    }

    final class IntegerDisplayInfo
            extends DisplayInfo
    {
        final JPanel panel;

        protected IntegerDisplayInfo(final String before, final String after, final IntegerSetting setting)
        {
            super(before, setting);
            panel = new JPanel(new GridBagLayout());
//            panel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
            panel.setBorder    (BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder   (4, 0, 0, 0),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder (Color.gray         ),
                            BorderFactory.createEmptyBorder(         0, 0, 3, 3) )));
            panel.setBackground(Color.white                        );

            final GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor    = GridBagConstraints.NORTHWEST;
            constraints.fill      = GridBagConstraints.VERTICAL;
            constraints.gridwidth =                            1;
            constraints.weightx   = 0.0d;
            constraints.weighty   = 0.0d;
            constraints.gridx     =                            0;
            constraints.gridy     =                            0;
            constraints.insets    = new Insets(0, 3, 0, 0);
            final JLabel beforeLabel = new JLabel(before);
            panel.add(beforeLabel, constraints);
            constraints.gridx = 1;

            final NumberFormat integerInstance = NumberFormat.getIntegerInstance();
            integerInstance.setMaximumIntegerDigits(2);
            integerInstance.setMinimumIntegerDigits(1);
            final JFormattedTextField formattedNumber = new JFormattedTextField(integerInstance);
            formattedNumber.setValue(new Integer("88"));
            final Dimension d = formattedNumber.getPreferredSize();
            d.width += 3;
            formattedNumber.setPreferredSize(d);
            formattedNumber.setValue(setting.get());
            formattedNumber.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
            panel.add(formattedNumber, constraints);
            formattedNumber.addPropertyChangeListener("value", new PropertyChangeListener()
            {
                public void propertyChange(final PropertyChangeEvent evt)
                {
                    final int n = ((Number) formattedNumber.getValue()).intValue();
                    setting.set(n);
                }
            });
            constraints.gridx = 2;
            final JLabel afterLabel = new JLabel(after);
            panel.add(afterLabel, constraints);
        }

        final JComponent getRendererComponent()
        {
            return panel;
        }

        final MyCellEditor getEditorComponent()
        {
            return new MyCellEditor(panel);
        }
    }

    class CheckboxDisplayInfo
            extends DisplayInfo
    {
        final JCheckBox checkBox;

        public CheckboxDisplayInfo(final String before, final BooleanSetting setting)
        {
            super(before, setting);
            checkBox = new JCheckBox();
            checkBox.addActionListener(new ActionListener()
            {
                public void actionPerformed(final ActionEvent e)
                {
                    final JCheckBox checkBox = (JCheckBox) e.getSource();
                    setting.set(checkBox.isSelected());
                }
            });
            checkBox.setText(before);
            checkBox.setSelected(setting.get());
            checkBox.setIcon(null);
            checkBox.setBackground(Color.white);
        }

        JComponent getRendererComponent()
        {
            return checkBox;
        }

        MyCellEditor getEditorComponent()
        {
            return new MyCellEditor(checkBox);
        }
    }

    final class AlignableCheckboxDisplayInfo
            extends CheckboxDisplayInfo
    {
        final String after;
        final JPanel panel;

        protected AlignableCheckboxDisplayInfo(final String before,
                                               final String after,
                                               final ColumnSetting setting)
        {
            super(before, setting);
            this.after = after;
            panel      = new JPanel(new GridBagLayout());
//            panel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
            panel.setBorder    (BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder   (4, 0, 0, 0),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder (Color.gray         ),
                            BorderFactory.createEmptyBorder(         0, 0, 3, 3) )));
            panel.setBackground(Color.white                        );

            final GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor    = GridBagConstraints.NORTHWEST;
            constraints.fill      = GridBagConstraints.NONE;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.gridx     =                            0;
            constraints.gridy     =                            0;

            checkBox.setBackground(Color.white);
            panel.add(checkBox, constraints);

            constraints.fill      = GridBagConstraints.VERTICAL;
            constraints.gridwidth =                           1;
            constraints.weightx   = 0.0d;
            constraints.weighty   = 0.0d;
            constraints.gridx     =                           0;
            constraints.gridy     =                           1;
            constraints.insets    = new Insets(0, 20, 0, 0);
            final JLabel appendLabel = new JLabel("Append");
            panel.add(appendLabel, constraints);
            constraints.insets = new Insets(0, 3, 0, 0);
            constraints.gridx  = 1;

            final NumberFormat integerInstance = NumberFormat.getIntegerInstance();
            integerInstance.setMaximumIntegerDigits(2);
            integerInstance.setMinimumIntegerDigits(1);
            final JFormattedTextField formattedNumber = new JFormattedTextField(integerInstance);
            formattedNumber.setValue(new Integer("88"));
            Dimension d = formattedNumber.getPreferredSize();
            d.width += 3;
            formattedNumber.setPreferredSize(d);
            formattedNumber.setValue(setting.getCharacters());
            formattedNumber.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
            panel.add(formattedNumber, constraints);
            formattedNumber.addPropertyChangeListener("value", new PropertyChangeListener()
            {
                public void propertyChange(final PropertyChangeEvent evt)
                {
                    final int n = ((Number) formattedNumber.getValue()).intValue();
                    setting.setCharacters(n);
                }
            });

            final String[]  chartypes = {"tabs", "spaces"};

            final JComboBox combobox  = new JComboBox(chartypes);
            // hack to figure optimal preferred size.
            combobox.setSelectedIndex(1);
            d       =  combobox.getPreferredSize();
            d.width += 4;
            combobox.setSelectedIndex(setting.isTabs() ? 0 : 1);
            combobox.setPreferredSize(d);
            combobox.addActionListener(new ActionListener()
            {
                public void actionPerformed(final ActionEvent e)
                {
                    final JComboBox cb       = (JComboBox) e.getSource();
                    final String    charname = (String   ) cb.getSelectedItem();
                    setting.setTabs(charname.equals("tabs"));
                }
            });
            constraints.gridx = 2;
            panel.add(combobox, constraints);
            final JLabel afterLabel = new JLabel(after);
            constraints.gridx     =                            3;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            JCheckBox rearrangeBox = null;
            if (setting instanceof RearrangeableColumnSetting)
            {
                rearrangeBox = new JCheckBox("Rearrange and align modifiers");
            }
            constraints.weightx = 1.0d;
            panel.add(afterLabel, constraints);

            if (rearrangeBox != null)
            {
                rearrangeBox.setBackground(Color.white);
                rearrangeBox.setSelected(((RearrangeableColumnSetting) setting).isRearrange());
                constraints.gridx = 0;
                constraints.gridy++;
                constraints.insets = new Insets(0, 20, 0, 0);
                panel.add(rearrangeBox, constraints);
                rearrangeBox.addActionListener(new ActionListener()
                {
                    public void actionPerformed(final ActionEvent e)
                    {
                        final boolean isSelected = ((JCheckBox) e.getSource()).isSelected();
                        ((RearrangeableColumnSetting) setting).setRearrange(isSelected);
                    }
                });
            }

            checkBox.addActionListener(new ActionListener()
            {
                public void actionPerformed(final ActionEvent e)
                {
                    final boolean isEnabled = ((JCheckBox) e.getSource()).isSelected();
                    appendLabel.setEnabled(isEnabled);
                    formattedNumber.setEnabled(isEnabled);
                    combobox.setEnabled(isEnabled);
                    afterLabel.setEnabled(isEnabled);
                }
            });
            appendLabel.setEnabled(setting.get());
            formattedNumber.setEnabled(setting.get());
            combobox.setEnabled(setting.get());
            afterLabel.setEnabled(setting.get());
        }

        final String getOneLineDescription()
        {
            final String        result;
            final ColumnSetting cs     = (ColumnSetting) setting;
            if (((BooleanSetting) setting).get())
            {
                // checkbox enabled, display alignment settings.
                result = before +
                         " (append " +
                         cs.getCharacters() +
                         " " +
                         (cs.isTabs() ? "tabs" : "characters") + " " + after + ")";
            }
            else
            {
                result = before;
            }
            return result;
        }

        final JComponent getRendererComponent()
        {
            return panel;
        }

        final MyCellEditor getEditorComponent()
        {
            return new MyCellEditor(panel);
        }
    }

    final class RadioButtonDisplayInfo
            extends DisplayInfo
    {
        final boolean      correspondsToFalse;
        final JRadioButton button;

        protected RadioButtonDisplayInfo(final String         before            ,
                                       final BooleanSetting setting           ,
                                       final boolean        correspondsToFalse )
        {
            super(before, setting);
            this.correspondsToFalse = correspondsToFalse;
            button                  = new JRadioButton();
            button.addItemListener(new ItemListener()
            {
                public void itemStateChanged(final ItemEvent e)
                {
                    final JRadioButton button = (JRadioButton) e.getItem();
                    setting.set(button.isSelected() ^correspondsToFalse);
                    logger.debug("invalidate/repaint radio button '" + before + "'");
                    button.invalidate();
                    button.repaint();
                    if (outerContainerPanel != null)
                    {
                        logger.debug("repaint/validate outer container panel " + outerContainerPanel.toString());
                        outerContainerPanel.repaint();
                        outerContainerPanel.validate();
                    }
                }
            });
            button.setText(getOneLineDescription());
            button.setIcon(null);
            button.setBackground(Color.white);
        }

        final JComponent getRendererComponent()
        {
            return button;
        }

        final MyCellEditor getEditorComponent()
        {
            return new MyCellEditor(button);
        }
    }

    private final class SettingTreeCellRenderer
            implements TreeCellRenderer
    {
        public final Component getTreeCellRendererComponent(final JTree   tree    ,
                                                            final Object  value   ,
                                                            final boolean selected,
                                                            final boolean expanded,
                                                            final boolean leaf    ,
                                                            final int     row     ,
                                                            final boolean hasFocus )
        {
            final Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof String)
            {
                final JLabel label = new JLabel((String) userObject);
                      Font   f     = label.getFont();
                f = f.deriveFont(Font.BOLD);
                label.setFont(f);
                label.setIcon(null);
                return label;
            }
            else
            {
                /**
                 * must be an instance of DisplayInfo.  Create appropriate component.
                 */
                final DisplayInfo info = (DisplayInfo) userObject;
                return info.getRendererComponent();
            }
        }
    }

    final class SettingTreeCellEditor
            implements TreeCellEditor
    {
              CellEditor realEditor;
        final JTree      ownerTree;

        private SettingTreeCellEditor(final JTree ownerTree)
        {
            this.ownerTree = ownerTree;
        }

        public final Component getTreeCellEditorComponent(final JTree   tree      ,
                                                          final Object  value     ,
                                                          final boolean isSelected,
                                                          final boolean expanded  ,
                                                          final boolean leaf      ,
                                                          final int     row        )
        {
            /**
             * Create a realEditor for the DisplayInfo object.
             */
            final Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof DisplayInfo)
            {
                final DisplayInfo  info   = (DisplayInfo) userObject;
                final MyCellEditor result = info.getEditorComponent();
                realEditor = result;
                return result.component;
            }
            else
            {
                return null;
            }
        }

        public final void cancelCellEditing()
        {
            realEditor.cancelCellEditing();
        }

        public final boolean stopCellEditing()
        {
            return realEditor.stopCellEditing();
        }

        public final Object getCellEditorValue()
        {
            return realEditor.getCellEditorValue();
        }

        public final boolean isCellEditable(final EventObject anEvent)
        {
            if (anEvent instanceof MouseEvent)
            {
                final MouseEvent me   = (MouseEvent) anEvent;
                final TreePath   path = ownerTree.getPathForLocation(me.getX(), me.getY());
                if (path != null)
                {
                    final TreeNode node = (TreeNode) path.getLastPathComponent();
                    return node.isLeaf();
                }
            }
            return false;
        }

        public final boolean shouldSelectCell(final EventObject anEvent)
        {
            return realEditor.shouldSelectCell(anEvent);
        }

        public final void addCellEditorListener(final CellEditorListener l)
        {
            if (realEditor != null)
                realEditor.addCellEditorListener(l);
        }

        public final void removeCellEditorListener(final CellEditorListener l)
        {
            if (realEditor != null)
                realEditor.removeCellEditorListener(l);
        }
    }

    final class MyCellEditor
            implements CellEditor
    {
        final ArrayList<CellEditorListener> listeners;
        final JComponent                    component;

        public MyCellEditor(final JComponent component)
        {
            this.component = component;
            listeners      = new ArrayList<CellEditorListener>();
        }

        public final void cancelCellEditing()
        {
            if (listeners.size() > 0)
            {
                final ChangeEvent ce = new ChangeEvent(this);
                for (int i = listeners.size() - 1; i >= 0; i--)
                {
                    listeners.get(i).editingCanceled(ce);
                }
            }
        }

        public final boolean stopCellEditing()
        {
            if (listeners.size() > 0)
            {
                final ChangeEvent ce = new ChangeEvent(this);
                for (int i = listeners.size() - 1; i >= 0; i--)
                {
                    listeners.get(i).editingStopped(ce);
                }
            }
            return true;
        }

        public final Object getCellEditorValue()
        {
            return component;
        }

        public final boolean isCellEditable(final EventObject anEvent)
        {
            return true;
        }

        public final boolean shouldSelectCell(final EventObject anEvent)
        {
            return true;
        }

        public final void addCellEditorListener(final CellEditorListener l)
        {
            listeners.add(l);
        }

        public final void removeCellEditorListener(final CellEditorListener l)
        {
            listeners.remove(l);
        }
    }
}
