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

/**
 * Test end-to-end parsing-to-alignment of assignment statements.
 */
public final class AssignmentAlignmentTest extends LightCodeInsightTestCase
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
        ts.right_justify_numeric_literals.set(false);
        css = CodeStyleSettingsManager.getInstance().getCurrentSettings().clone();
    }

    public final void testSimpleAssignment() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/AssignmentAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_modifiers.set(false);
        ts.align_trailing_comments.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.delimit_by_blank_lines.set(true);
        final boolean oldSBMP = css.SPACE_BEFORE_METHOD_PARENTHESES;
        css.SPACE_BEFORE_METHOD_PARENTHESES = false;
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        css.SPACE_BEFORE_METHOD_PARENTHESES = oldSBMP;
        super.checkResultByFile("/com/wrq/tabifier/parse/AssignmentAlignmentResult1.java");
    }

    public final void testSimpleAssignmentWithDocComment() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/AssignmentAlignmentTest2.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_modifiers.set(false);
        ts.align_trailing_comments.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.delimit_by_blank_lines.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AssignmentAlignmentResult2.java");
    }

    public final void testReferenceAssignment() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/AssignmentAlignmentTest3.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_modifiers.set(false);
        ts.align_trailing_comments.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.delimit_by_blank_lines.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AssignmentAlignmentResult3.java");
    }

    public final void testMethodCallAlignment() throws Exception
    {
        /* Courtesy of Richard Chuo, Universalec.com */
        /** Assignment statements weren't being aligned together because the initialization
         *  portions involved method names that mismatched.  The delimit_by_method_name option
         * should not affect assignment statements.
         */
        configureByFile("/com/wrq/tabifier/parse/AssignmentAlignmentTest4.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.delimit_by_blank_lines.set(false);
        ts.align_method_call_names.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_params.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_method_call_close_parend.set(true);
        ts.method_call_similarity_threshold.set(0);
        css.SPACE_WITHIN_METHOD_CALL_PARENTHESES = true;
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AssignmentAlignmentResult4.java");
    }

    public final void testTernaryExpressionAlignment() throws Exception
    {
        /* Courtesy of Tom Brus, Tom_Brus@nl.CompuWare.com */
        /** Ternary expressions (bool ? a : b) weren't being handled properly.
         */
        configureByFile("/com/wrq/tabifier/parse/AssignmentAlignmentTest5.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.delimit_by_blank_lines.set(false);
        ts.align_method_call_names.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.delimit_by_blank_lines.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AssignmentAlignmentResult5.java");
    }

    public final void testStringWrappingAlignment() throws Exception
    {
        /* Courtesy of Joe Martinez */
        configureByFile("/com/wrq/tabifier/parse/AssignmentAlignmentTest20.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.delimit_by_blank_lines.set(false);
        ts.align_method_call_names.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_arithmetic_operators.set(false);
        ts.delimit_by_blank_lines.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AssignmentAlignmentResult20.java");
    }

    public final void testStringWrappingAlignmentWithAlignedOperators() throws Exception
    {
        /* Courtesy of Joe Martinez */
        configureByFile("/com/wrq/tabifier/parse/AssignmentAlignmentTest20.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.align_method_call_names.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_arithmetic_operators.set(true);
        ts.delimit_by_blank_lines.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AssignmentAlignmentResult20B.java");
    }

    public final void testPolyadicExpressionAlignmentWithAlignedOperators() throws Exception
    {
        /* Courtesy of Joe Martinez */
        configureByFile("/com/wrq/tabifier/parse/PolyadicExpressionTest1.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.align_method_call_names.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_arithmetic_operators.set(true);
        ts.delimit_by_blank_lines.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/PolyadicExpressionResult1.java");
    }

    public final void testClassAttr() throws Exception
    {
        /* Courtesy of Olivier Descout */
        configureByFile("/com/wrq/tabifier/parse/ClassAttrTest1.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        ts.align_method_call_names.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_arithmetic_operators.set(true);
        ts.delimit_by_blank_lines.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ClassAttrResult1.java");
    }
}
