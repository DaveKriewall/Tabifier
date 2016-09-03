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
package com.wrq.tabifier;

import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.wrq.tabifier.columnizer.DocumentParser;
import com.wrq.tabifier.parse.Line;
import com.wrq.tabifier.parse.LineGroup;
import com.wrq.tabifier.parse.PsiTreeUtil;
import com.wrq.tabifier.settings.TabifierSettings;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ListIterator;

final public class TabifierActionHandler extends EditorWriteActionHandler
{
    private int tab_size;
    private int indent;
    private boolean smart_tabs;
    private boolean use_tab_char;
    private static final Logger logger = Logger.getLogger("com.wrq.tabifier.TabifierActionHandler");
//    private ProgressBar progressBar;
    private Project project;

    public TabifierActionHandler()
    {
        CodeStyleSettingsManager cssm = CodeStyleSettingsManager.getInstance();
        final CodeStyleSettings cssettings;
        if (cssm.USE_PER_PROJECT_SETTINGS)
        {
            final Project defaultProject = ProjectManager.getInstance().getDefaultProject();
            logger.debug("TabifierActionHandler: obtaining code style settings for " + defaultProject);
            cssm = CodeStyleSettingsManager.getInstance(defaultProject);
        }
        else
        {
            logger.debug("TabifierActionHandler: using code style settings for application (not project-specific)");
        }
        cssettings = cssm.getCurrentSettings();
        FileType javaFileType = FileTypeManager.getInstance().getFileTypeByExtension("java"); // was FileType.JAVA
        indent = cssettings.getIndentSize(javaFileType);
        smart_tabs = cssettings.isSmartTabs(javaFileType);
        tab_size = cssettings.getTabSize(javaFileType);
        use_tab_char = cssettings.useTabCharacter(javaFileType);
        logger.debug("TabifierActionHandler: determined indent=" +
                indent +
                ", tab_size=" +
                tab_size +
                ", use_tab_char=" +
                use_tab_char +
                ", smart tabs=" +
                smart_tabs);
    }

    private static PsiFile getFile(final Editor editor,
                                   final DataContext context)
    {
        final Project project = (Project) context.getData(DataConstants.PROJECT);
        final Document document = editor.getDocument();
        final FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        final PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile file = psiManager.findFile(fileDocumentManager.getFile(document));
        return file;
    }

    /**
     * Called by IDEA to tabify a text selection.  If no selection is made, the value of the no_selection_behavior
     * setting controls whether the entire file is tabified, or only the line where the cursor is.
     * 
     * @param editor  current com.intellij.openapi.editor object; contains editor
     *                settings.
     * @param context used to obtain a reference to the current IDEA project.
     */
    public final void executeWriteAction(final Editor editor,
                                         final DataContext context)
    {
        if (editor == null)
        {
            return;
        }
        project = (Project) context.getData(DataConstants.PROJECT);
        final Application application = ApplicationManager.getApplication();
        final Document document = editor.getDocument();
        final SelectionModel selection = editor.getSelectionModel();
        /**
         * Per instructions from IntelliJ, we have to commit any changes to the document to the Psi
         * tree.
         */
        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        documentManager.commitDocument(document);
//        progressBar = new ProgressBar(project);
        /**
         * if no selection is made, then depending on the no_selection_made setting, either tabify
         * the entire file (and reset selection later) or select the declaration or expression
         * statement around caret position. If a selection is made, extend the selection to the
         * beginning and end of the selected lines. Include any additional white space at the
         * beginning or end of that selection.
         */
        final tabifier the_tabifier = application.getComponent(tabifier.class);
        final TabifierSettings settings = the_tabifier.getSettings();
        final PsiFile psiFile = getFile(editor, context);
        if (!psiFile.getName().endsWith(".java"))
        {
            logger.debug("not a .java file -- skipping " + psiFile.getName());
            return;
        }

//        progressBar.setMaximum(psiFile.getTextRange().getEndOffset());
        if (psiFile.isWritable() && psiFile.getFileType().equals(StdFileTypes.JAVA))
        {
            final TextRange range;
            final boolean hasSelection = selection.hasSelection();
            final LogicalPosition original_position = editor.getCaretModel().getLogicalPosition();
            final TextRange originalRange = getSelectedRange(editor);                    // side effect: creates selection
            boolean resetToOriginalRange = false;
            if (!hasSelection)
            {
                logger.debug("no user selection found; original position=" + original_position);
            }
            else
            {
                logger.debug("selection found: start=" + originalRange.getStartOffset() +
                        ", end=" + originalRange.getEndOffset());
            }
            logger.debug("no_selection_behavior setting: " +
                    (settings.no_selection_behavior.get() ? "tabify entire file"
                    : "tabify only selection (or line if no selection)"));
            if (settings.no_selection_behavior.get() && !hasSelection)
            {
                logger.debug("entire file range: start=" +
                        psiFile.getTextRange().getStartOffset() +
                        ", end=" +
                        psiFile.getTextRange().getEndOffset());
                range = new TextRange(psiFile.getTextRange().getStartOffset(),
                        psiFile.getTextRange().getEndOffset());
                logger.debug("range: start=" +
                        range.getStartOffset() +
                        ", end=" +
                        range.getEndOffset());
                resetToOriginalRange = true;
            }
            else
            {
                range = originalRange;
            }
            logger.debug("setting range to tabify: start=" +
                    range.getStartOffset() +
                    ", end=" +
                    range.getEndOffset());
            editor.getSelectionModel().setSelection(range.getStartOffset(), range.getEndOffset());
            tabify(editor, context, psiFile, range.getStartOffset(), range.getEndOffset());
            if (resetToOriginalRange)
            {
                if (hasSelection)
                {
                    editor.getSelectionModel().setSelection(originalRange.getStartOffset(), originalRange.getEndOffset());
                }
                else
                {
                    editor.getSelectionModel().setSelection(0, 0);
                    editor.getCaretModel().moveToLogicalPosition(original_position);
                }
            }
        }
        logger.debug("end execute_write_action");
    }

    /**
     * called by ReformatPlugin callback after code layout has happened.  Tabify the entire file.
     */
    public final void tabify(final Editor editor,
                             final DataContext context)
    {
        final PsiFile psiFile = getFile(editor, context);
        tabify(editor,
                context,
                psiFile,
                psiFile.getTextRange().getStartOffset(),
                psiFile.getTextRange().getEndOffset());
    }

    private TextRange getSelectedRange(final Editor editor)
    {
        final SelectionModel selection = editor.getSelectionModel();
        final LogicalPosition start_pos = editor.offsetToLogicalPosition(selection.getSelectionStart());

        if (selection.hasSelection())
        {
            final CaretModel caret = editor.getCaretModel();
            caret.moveToOffset(selection.getSelectionEnd() - 1);
        }
        selection.selectLineAtCaret();

        final LogicalPosition new_start_pos = new LogicalPosition(start_pos.line, 0);
        final int new_start = editor.logicalPositionToOffset(new_start_pos);

        return new TextRange(new_start, selection.getSelectionEnd());
    }

    /**
     * Obtains current settings for indent, tab_size, use_tab_chars, smart_tabs, etc. from the appropriate source
     * (application or project settings).  Also obtains current tabifier settings, and the Document to be updated.
     * Passes these to tabifyPsiFile.
     * 
     * @param editor      current com.intellij.openapi.editor object; contains editor
     *                    settings.
     * @param context     used to obtain a reference to the current IDEA project.
     * @param psiFile     PsiFile in which reformatting will take place.
     * @param startOffset starting offset of file for reformatting.
     * @param endOffset   ending offset of file for reformatting.
     */
    private void tabify(final Editor editor,
                        final DataContext context,
                        final PsiFile psiFile,
                        final int startOffset,
                        final int endOffset)
    {
        final Project project = (Project) context.getData(DataConstants.PROJECT);
        final Document document = editor.getDocument();
        final CodeStyleSettings cssettings = getCodeStyleSettings(project);

        FileType javaFileType = FileTypeManager.getInstance().getFileTypeByExtension("java"); // was FileType.JAVA
        indent = cssettings.getIndentSize(javaFileType);
        smart_tabs = cssettings.isSmartTabs(javaFileType);
        tab_size = cssettings.getTabSize(javaFileType);
        use_tab_char = cssettings.useTabCharacter(javaFileType);

        logger.debug("executeWriteAction: determined indent=" + indent +
                ", tab_size=" + tab_size +
                ", use_tab_char=" + use_tab_char +
                ", smart tabs=" + smart_tabs);
        final Application application = ApplicationManager.getApplication();
        final tabifier the_tabifier = (tabifier) application.getComponent(tabifier.class);
        final TabifierSettings settings = the_tabifier.getSettings();
        tabifyPsiFile(psiFile, startOffset, endOffset, cssettings, settings, document);
    }

    public static CodeStyleSettings getCodeStyleSettings(final Project project)
    {
        CodeStyleSettingsManager cssm = CodeStyleSettingsManager.getInstance();
        if (cssm.USE_PER_PROJECT_SETTINGS)
        {
            logger.debug("tabify: obtaining code style settings for " + project);
            cssm = CodeStyleSettingsManager.getInstance(project);
        }
        else
        {
            logger.debug("tabify: using code style settings for application (not project-specific)");
        }
        return cssm.getCurrentSettings();
    }

    //
    // following four routines are present for use by test routines.  There's no way to set these fields
    // in the code style settings.
    //
    void setIndent(final int indent)
    {
        this.indent = indent;
    }

    void setSmartTabs(final boolean smartTabs)
    {
        this.smart_tabs = smartTabs;
    }

    void setTabSize(final int tabSize)
    {
        this.tab_size = tabSize;
    }

    void useTabChar(final boolean use_tab_char)
    {
        this.use_tab_char = use_tab_char;
    }

    /**
     * Workhorse of the tabifier. Given a selection in a java file, inspect the corresponding
     * program structure information (Psi) and break the selection into groups of lines at similar
     * indentation levels. Group these lines according to grouping rules and reformat each line.
     * 
     * @param psiFile     Java file in which selection was made.
     * @param startOffset beginning of selection; already forced to be at beginning of line.
     * @param endOffset   end of selection; already forced to be at end of line.
     * @param settings    current tabifier settings.
     * @param document    IDEA text document, which will be updated with new aligned text.
     */
    public void tabifyPsiFile(final PsiFile psiFile,
                              final int startOffset,
                              final int endOffset,
                              final CodeStyleSettings codeStyleSettings,
                              final TabifierSettings settings,
                              final Document document)
    {
        /** determine if logging. */
        boolean logging = false;
        {
            Category c = logger;
            while (c != null && !logging)
            {
                if (c.getLevel() == Level.DEBUG) logging = true;
                c = c.getParent();
            }
        }
        if (logging)
        {
            logger.debug("tabifyPsiFile: file=" + PsiTreeUtil.elname(psiFile) +
                    ", selection start/end=[" + startOffset + "," +
                    endOffset + "]");
            logger.debug("tabifyPsiFile: indent=" + indent + ", smart_tabs=" + smart_tabs +
                    ", tab_size=" + tab_size + ", use_tab_char=" + use_tab_char);
            logger.debug("tabifyPsiFile: tabifier settings are as follows:");
            final ListIterator /* <Setting> */ li = settings.getSettings();
            while (li.hasNext())
            {
                logger.debug(li.next());
            }
        }
        /**
         * for now, always create a progress bar, even when run from JUnit test.
         */
        if (project == null)
        {
            project = ProjectManager.getInstance().getDefaultProject();
        }
//        if (progressBar == null)
//        {
//            progressBar = new ProgressBar(project);
//        }
        /**
         * parse document into lines of tokens.
         */
//        progressBar.getProgressBarDialog();
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run()
//            {
//                progressBar.setFilename(psiFile.getName());
//                progressBar.setCurrent(0);
//                progressBar.showDialog();
//            }
//        });

        final DocumentParser cp = new DocumentParser(startOffset,
                endOffset,
                codeStyleSettings,
                settings,
                tab_size,
//                progressBar,
                use_tab_char,
                smart_tabs,
                indent);
//        Runnable parseAndTabify = new Runnable()
//        {
//            public void run()
//            {
//                ApplicationManager.getApplication().runReadAction(new Runnable()
//                {
//                    public void run()
//                    {
//                        logger.debug("worker thread running in Application.runReadAction()");
//                        try
//                        {
                            psiFile.accept(cp);
//                        }
//                        catch (CancelOperationException coe)
//                        {
//                            logger.debug("tabifier operation cancelled in DocumentParser");
//                            return;
//                        }
//                    }
//                });
//
//                logger.debug("worker thread finished; progressBar.isCancelled=" + progressBar.isCancelled());
//
//                if (!progressBar.isCancelled())
//                {
//                    try
//                    {
//                        SwingUtilities.invokeAndWait(new Runnable()
//                        {
//                            public void run()
//                            {
                                /**
                                 * reformat each line and replace it in the document, if changed.  Do this on the Swing thread.
                                 */
//                                logger.debug("Swing dispatch thread doing document updating");
                                final ListIterator/*<Line>*/ lineIterator = LineGroup.reformatableLines.listIterator(
                                        LineGroup.reformatableLines.size());
                                while (lineIterator.hasPrevious())
                                {
                                    final Line line = (Line) lineIterator.previous();

                                    if (line.isFormatDiffers())
                                    {
                                        final String s = line.getFormattedLine();
                                        if (logger.isDebugEnabled())
                                        {
                                            logger.debug("replacing region from " +
                                                    line.getStartOffset() + " to " +
                                                    line.getEndOffset());
                                            logger.debug("--- OLD REGION ---");
                                            logger.debug(document.getText().substring(line.getStartOffset(),
                                                    line.getEndOffset()));
                                            logger.debug("--- NEW REGION ---");
                                            logger.debug(s);
                                            logger.debug("--- END UPDATE ---");
                                        }
                                        document.replaceString(line.getStartOffset(),
                                                line.getEndOffset(),
                                                s);
                                    }
                                    else
                                    {
                                        if (logger.isDebugEnabled())
                                        {
                                            logger.debug("no change to region from " +
                                                    line.getStartOffset() + " to " +
                                                    line.getEndOffset());
                                        }
                                    }
                                }
                                LineGroup.reset();
//                            }
//                        });
//                    }
//                    catch (InterruptedException e)
//                    {
//                        logger.debug("swing operation (updating document) interrupted");
//                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//                    }
//                    catch (InvocationTargetException e)
//                    {
//                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//                    }
//                }
//                progressBar.closeDialog();
//            }
//        };
//        new Thread(parseAndTabify, "TabifierWorkerThread").start();
    }

}
