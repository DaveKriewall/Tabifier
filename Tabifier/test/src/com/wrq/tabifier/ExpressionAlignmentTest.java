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
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Level;

/**
 * Test end-to-end parsing-to-alignment of assignment statements.
 */
public final class ExpressionAlignmentTest
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
        ts.right_justify_numeric_literals.set(false);
        css = CodeStyleSettingsManager.getInstance().getCurrentSettings().clone();
    }

    public final void testIteration1() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ExpressionAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(false);
        ts.align_arithmetic_operators.set(false);
        ts.align_semicolons.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ExpressionAlignmentResult1.java");
    }

    public final void testIteration2() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ExpressionAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(false);
        ts.align_arithmetic_operators.set(true);
        ts.align_semicolons.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ExpressionAlignmentResult1B.java");
    }

    public final void testIteration3() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ExpressionAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(false);
        ts.align_arithmetic_operators.set(false);
        ts.align_semicolons.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ExpressionAlignmentResult1C.java");
    }

    public final void testIteration4() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ExpressionAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(false);
        ts.align_arithmetic_operators.set(true);
        ts.align_semicolons.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ExpressionAlignmentResult1D.java");
    }

    public final void testIteration5() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ExpressionAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(true);
        ts.align_arithmetic_operators.set(true);
        ts.align_semicolons.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ExpressionAlignmentResult1E.java");
    }

    public final void testReturnExpression() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ReturnAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(true);
        ts.align_arithmetic_operators.set(true);
        ts.align_semicolons.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ReturnAlignmentResult1.java");
    }

    public final void testTernaryExpression() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ExpressionAlignmentTest2.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(true);
        ts.align_arithmetic_operators.set(true);
        ts.align_semicolons.set(false);
        ts.expression_parse_nesting_level.set(3);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ExpressionAlignmentResult2.java");
    }

    public final void testTernaryExpressionInsufficientNesting() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ExpressionAlignmentTest2.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(true);
        ts.align_arithmetic_operators.set(true);
        ts.align_semicolons.set(false);
        ts.expression_parse_nesting_level.set(1);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ExpressionAlignmentResult2A.java");
    }

    public final void testDeepExpressionAlignment() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ExpressionAlignmentTest3.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(true);
        ts.align_arithmetic_operators.set(true);
        ts.align_semicolons.set(false);
        ts.expression_parse_nesting_level.set(8);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ExpressionAlignmentResult3.java");
    }

    public final void testExtendedBinaryExprContainingTernaryExpr() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ExpressionAlignmentTest4.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(true);
        ts.align_arithmetic_operators.set(true);
        ts.align_semicolons.set(false);
        ts.expression_parse_nesting_level.set(4);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ExpressionAlignmentResult4.java");
    }

    public final void testSpacing4_5bug() throws Exception
    {

        configureByFile("/com/wrq/tabifier/parse/ContributedTest7.java");
        final PsiFile file = getFile();
        ts.right_justify_numeric_literals.set(true);
        ts.align_arithmetic_operators.set(true);
        ts.align_semicolons.set(false);
        ts.expression_parse_nesting_level.set(4);
        boolean oldswp = css.SPACE_WITHIN_PARENTHESES;
        css.SPACE_WITHIN_PARENTHESES = true;
        boolean oldswwp = css.SPACE_WITHIN_WHILE_PARENTHESES;
        css.SPACE_WITHIN_WHILE_PARENTHESES = true;
        boolean oldswip = css.SPACE_WITHIN_IF_PARENTHESES;
        css.SPACE_WITHIN_IF_PARENTHESES = true;
        boolean oldswmcp = css.SPACE_WITHIN_METHOD_CALL_PARENTHESES;
        css.SPACE_WITHIN_METHOD_CALL_PARENTHESES = true;
        try
        {
            final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
            final TabifierActionHandler wa = new TabifierActionHandler();
            wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
            super.checkResultByFile("/com/wrq/tabifier/parse/ContributedResult7.java");
        }
        finally
        {
            css.SPACE_WITHIN_METHOD_CALL_PARENTHESES = oldswmcp;
            css.SPACE_WITHIN_PARENTHESES = oldswp;
            css.SPACE_WITHIN_WHILE_PARENTHESES = oldswwp;
            css.SPACE_WITHIN_IF_PARENTHESES = oldswip;
        }

    }

    public final void testArrayIndexExpr() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ArrayIndexTest1.java");
        final PsiFile file = getFile();
        boolean oldSwb = css.SPACE_WITHIN_BRACKETS;
        css.SPACE_WITHIN_BRACKETS = true;
        try {
            final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
            final TabifierActionHandler wa = new TabifierActionHandler();
            wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
            super.checkResultByFile("/com/wrq/tabifier/parse/ArrayIndexResult1.java");
        }
        finally {
            css.SPACE_WITHIN_BRACKETS = oldSwb;
        }
    }

    public final void testAnonymousInnerClass() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/AnonymousInnerClassTest.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        ts.align_method_decl_open_parend.set(true);
        ts.align_method_decl_close_parend.set(true);
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AnonymousInnerClassResult.java");
    }

    public final void testExpressionParser1() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ExpressionParserTest1.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        ts.align_method_decl_open_parend.set(true);
        ts.align_method_decl_close_parend.set(true);
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ExpressionParserResult1.java");
    }
}