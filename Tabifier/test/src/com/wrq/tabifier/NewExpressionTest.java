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

import com.intellij.testFramework.LightCodeInsightTestCase;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.openapi.editor.Document;
import com.wrq.tabifier.settings.TabifierSettings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

/**
 * Test end-to-end parsing-to-alignment of 'new' expressions.
 */
public final class NewExpressionTest
        extends LightCodeInsightTestCase
{
    private TabifierSettings ts;
    private CodeStyleSettings css;

    protected final void setUp() throws Exception
    {
        super.setUp();
        Logger logger = Logger.getLogger("com.wrq.tabifier");
        logger.setAdditivity(false);
        logger.addAppender(new ConsoleAppender(new PatternLayout("[%7r] %6p - %30.30c - %m \n")));
//        logger.setLevel(Level.DEBUG);
        logger.setLevel(Level.INFO);
        ts = new TabifierSettings();
        ts.right_justify_numeric_literals.set(true);
        css = CodeStyleSettingsManager.getInstance().getCurrentSettings().clone();
    }

    public final void testAnonymousInnerClass() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/NewAlignmentTest1.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/NewAlignmentResult1.java");
    }

    public final void testNewObjectWithParams() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/NewAlignmentTest2.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_trailing_comments.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_initial_param_commas.set(true);
        ts.align_subsequent_param_commas.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/NewAlignmentResult2.java");
    }

    public final void testArrayInitializers() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/NewAlignmentTest3.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_trailing_comments.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/NewAlignmentResult3.java");
    }

    public final void testMultilineParams() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/NewAlignmentTest4.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_trailing_comments.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/NewAlignmentResult4.java");
    }

    public final void testNestedMultilineParams() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/NewAlignmentTest5.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_trailing_comments.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.expression_parse_nesting_level.set(1);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/NewAlignmentResult5.java");
    }

    public void testNewSpacing1() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/NewSpacingTest1.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_trailing_comments.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.expression_parse_nesting_level.set(1);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/NewSpacingResult1.java");
    }

}
