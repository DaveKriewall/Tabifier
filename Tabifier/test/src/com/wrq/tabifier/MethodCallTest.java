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
 * Test end-to-end parsing-to-alignment of method calls.
 */
public final class MethodCallTest
        extends LightCodeInsightTestCase
{
    private TabifierSettings  ts;
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

    public final void testMethodCallsWithTrailingComment() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set      (true);
        ts.align_trailing_comments.set         (true);
        ts.method_call_similarity_threshold.set(0   );
        ts.delimit_method_declarations.set     (true);
        final boolean oldSWMC = css.SPACE_WITHIN_METHOD_CALL_PARENTHESES;
        css.SPACE_WITHIN_METHOD_CALL_PARENTHESES = false;
        try {
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult1.java");
        }
        finally {
            css.SPACE_WITHIN_METHOD_CALL_PARENTHESES = oldSWMC;
        }

    }

    public final void testMethodCallsWithinDeclaration() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest2.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_trailing_comments.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult2.java");
    }

    public final void testNestedMethodCalls() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest3.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_trailing_comments.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult3.java");
    }

    public final void testMethodCallThreshold0() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest4.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.method_call_similarity_threshold.set(0);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult4A.java");
    }

    public final void testMethodCallThreshold4() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest4.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.method_call_similarity_threshold.set(4);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult4B.java");
    }

    public final void testMethodCallThreshold10() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest4.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.method_call_similarity_threshold.set(10);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult4C.java");
    }

    public final void testMethodCallMultilineParameters() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest5.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.method_call_similarity_threshold.set(10);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult5.java");
    }

    public final void testMethodCallWithDots() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest6.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(1);
        ts.align_assignment_operators.setTabs(false);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.method_call_similarity_threshold.set(4);
        ts.delimit_by_blank_lines.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult6.java");
    }

    public final void testMethodCallNoParamsSpaceWithinParentheses() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest7.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(1);
        ts.align_assignment_operators.setTabs(false);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.method_call_similarity_threshold.set(4);
        ts.delimit_by_blank_lines.set(true);
        final boolean oldSWMCP = css.SPACE_WITHIN_METHOD_CALL_PARENTHESES;
        css.SPACE_WITHIN_METHOD_CALL_PARENTHESES = true;
        try {
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult7.java");
        }
        finally {
            css.SPACE_WITHIN_METHOD_CALL_PARENTHESES = oldSWMCP;
        }

    }

    public final void testMultilineParameter() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest8.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(1);
        ts.align_assignment_operators.setTabs(false);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.method_call_similarity_threshold.set(4);
        ts.delimit_by_blank_lines.set(true);
        ts.expression_parse_nesting_level.set(4);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult8.java");
    }

    public final void testAlignedNestedMultilineParameter() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest9.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_method_call_close_parend.set(false);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult9.java");
    }

    public final void testUnalignedNestedMultilineParameter() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest9.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(false);
        ts.align_subsequent_params.set(false);
        ts.align_method_call_close_parend.set(false);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_terms.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult9.java");
    }

    public final void testNestedParameter() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodCallAlignmentTest10.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(false);
        ts.align_subsequent_params.set(false);
        ts.align_method_call_close_parend.set(false);
        ts.align_method_call_open_parend.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.right_justify_numeric_literals.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodCallAlignmentResult10.java");
    }
    public final void testIndependentParameterSettingsA() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(false);
        ts.align_subsequent_params.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_method_declaration_initial_params.set(true);
        ts.align_method_declaration_initial_param_commas.set(true);
        ts.align_method_declaration_subsequent_params.set(true);
        ts.align_method_declaration_subsequent_param_commas.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentResult1A.java");
    }
    public final void testIndependentParameterSettingsB() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(false);
        ts.align_subsequent_params.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(true);
        ts.align_method_declaration_initial_params.set(true);
        ts.align_method_declaration_initial_param_commas.set(true);
        ts.align_method_declaration_subsequent_params.set(true);
        ts.align_method_declaration_subsequent_param_commas.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentResult1B.java");
    }
    public final void testIndependentParameterSettingsC() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(false);
        ts.align_subsequent_params.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(true);
        ts.align_method_declaration_initial_params.set(true);
        ts.align_method_declaration_initial_param_commas.set(true);
        ts.align_method_declaration_subsequent_params.set(false);
        ts.align_method_declaration_subsequent_param_commas.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentResult1C.java");
    }
    public final void testIndependentParameterSettingsD() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(false);
        ts.align_subsequent_params.set(true);
        ts.align_initial_param_commas.set(true);
        ts.align_subsequent_param_commas.set(true);
        ts.align_method_declaration_initial_params.set(true);
        ts.align_method_declaration_initial_param_commas.set(false);
        ts.align_method_declaration_subsequent_params.set(false);
        ts.align_method_declaration_subsequent_param_commas.set(false);
        ts.align_variable_names.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentResult1D.java");
    }
    public final void testIndependentParameterSettingsE() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_method_declaration_initial_params.set(false);
        ts.align_method_declaration_initial_param_commas.set(true);
        ts.align_method_declaration_subsequent_params.set(true);
        ts.align_method_declaration_subsequent_param_commas.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentResult1E.java");
    }
    public final void testIndependentParameterSettingsF() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_method_declaration_initial_params.set(false);
        ts.align_method_declaration_initial_param_commas.set(false);
        ts.align_method_declaration_subsequent_params.set(true);
        ts.align_method_declaration_subsequent_param_commas.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentResult1F.java");
    }
    public final void testIndependentParameterSettingsG() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(false);
        ts.align_initial_param_commas.set(true);
        ts.align_subsequent_param_commas.set(true);
        ts.align_method_declaration_initial_params.set(false);
        ts.align_method_declaration_initial_param_commas.set(false);
        ts.align_method_declaration_subsequent_params.set(false);
        ts.align_method_declaration_subsequent_param_commas.set(true);
        ts.align_variable_names.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentResult1G.java");
    }
    public final void testIndependentParameterSettingsH() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_initial_param_commas.set(true);
        ts.align_subsequent_param_commas.set(true);
        ts.align_method_declaration_initial_params.set(false);
        ts.align_method_declaration_initial_param_commas.set(false);
        ts.align_method_declaration_subsequent_params.set(false);
        ts.align_method_declaration_subsequent_param_commas.set(false);
        ts.align_variable_names.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodIndependentAlignmentResult1H.java");
    }
    public final void testSpaceBetweenParends() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/SpaceBetweenParendsTest.java");
        final PsiFile file = getFile();
        ts.spaceBetweenEmptyParentheses.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/SpaceBetweenParendsResult.java");
    }
    public final void testNoSpaceBeforeAssignmentOperators() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/NoSpaceBeforeAssignmentOperatorTest.java");
        final PsiFile file = getFile();
        ts.no_space_before_assignment_operators.set(true);
        ts.align_assignment_operators.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/NoSpaceBeforeAssignmentOperatorResult.java");
    }
    public final void testSpaceBeforeAndWithinArrayInit() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/SpaceBeforeArrayInitTest.java");
        final PsiFile file = getFile();
        ts.force_space_before_array_initializer.set(true);
        ts.force_space_within_array_initializer.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/SpaceBeforeArrayInitResult.java");
    }
 }
