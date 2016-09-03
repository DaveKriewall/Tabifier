/**
 * Id$
 *
 * Tabifier (major release 2) plugin for IntelliJ IDEA.  Based on Jordan Zimmerman's work in release 1, but
 * completely rewritten to support more flexible alignment for any type of syntactic arrangement.
 *
 * Source code may be freely copied and reused.  Please copy credits, and send any bug fixes to the author.
 *
 * @author Dave Kriewall, WRQ, Inc.
 * September, 2003
 */
package com.wrq.tabifier;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.testFramework.LightCodeInsightTestCase;
import com.wrq.tabifier.settings.TabifierSettings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

public final class IfStmtAlignmentTest
        extends LightCodeInsightTestCase
{
    private TabifierSettings ts;
    private CodeStyleSettings css;

    protected final void setUp()
            throws Exception
    {
        super.setUp();
        Logger logger = Logger.getLogger("com.wrq.tabifier");
        logger.setAdditivity(false);
        logger.addAppender(new ConsoleAppender(new PatternLayout("[%7r] %6p - %30.30c - %m \n")));
        logger.setLevel(Level.DEBUG);
//        logger.setLevel(Level.INFO);
        ts = new TabifierSettings();
        ts.right_justify_numeric_literals.set(false);
        css = CodeStyleSettingsManager.getInstance().getCurrentSettings().clone();
    }

    public final void testSimpleAssignment()
            throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/IfStmtTest1.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_modifiers.set(false);
        ts.align_trailing_comments.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.align_other_open_parend.set(true);
        ts.delimit_by_blank_lines.set(true);
        ts.delimit_by_statement_type.set(true);
        final boolean oldSWMC = css.SPACE_WITHIN_METHOD_CALL_PARENTHESES;
        css.SPACE_WITHIN_METHOD_CALL_PARENTHESES = false;
        try {
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/IfStmtResult1.java");
        }
        finally {
            css.SPACE_WITHIN_METHOD_CALL_PARENTHESES = oldSWMC;
        }
    }

    public final void testConditionalsOnSeparateLine()
            throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/IfStmtTest2.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_modifiers.set(false);
        ts.align_trailing_comments.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.align_if_stmt_open_parend.set(false);
        ts.delimit_by_blank_lines.set(true);

        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/IfStmtResult2.java");
    }


    public final void testMultilineCondition()
            throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/IfStmtTest3.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_modifiers.set(false);
        ts.align_trailing_comments.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.align_other_open_parend.set(true);
        ts.delimit_by_blank_lines.set(true);

        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/IfStmtResult3.java");
    }

    public final void testParallelIfStmts()
            throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/IfStmtTest4.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_modifiers.set(false);
        ts.align_trailing_comments.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.align_other_open_parend.set(true);
        ts.delimit_by_blank_lines.set(true);

        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/IfStmtResult4.java");
    }

    public final void testElseBreak()
            throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/IfStmtTest5.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/IfStmtResult5.java");
    }

    public final void testBlockStmt()
            throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/IfStmtTest6.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/IfStmtResult6.java");
    }

    public final void testCascadingIfStmt()
            throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/IfStmtTest7.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/IfStmtResult7.java");
    }
}
