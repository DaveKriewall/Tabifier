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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Test end-to-end parsing-to-alignment of declarations.
 */
public class DeclarationAlignmentTest
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
        // to enable debugging, reverse the commenting on the following two lines
//        logger.setLevel(Level.DEBUG);
        logger.setLevel(Level.INFO);
        ts = new TabifierSettings();
        ts.right_justify_numeric_literals.set(false);
        css = CodeStyleSettingsManager.getInstance().getCurrentSettings().clone();
    }

    public final void testUnrearrangedModifiers() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_modifiers.setRearrange(false);
        ts.align_trailing_comments.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult1.java");
    }

    public final void testAlignedModifiers() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest2.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_modifiers.setRearrange(true);
        ts.align_trailing_comments.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult2.java");
    }

    public final void testAlignedModifiersAndComments() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest2A.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_modifiers.setRearrange(true);
        ts.align_trailing_comments.set(true);
        ts.align_variable_names.set(true);
        ts.align_variable_types.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult2A.java");
    }

    public final void testNoTabAlignment() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest3A.java");
        final PsiFile file = getFile();

        ts.align_trailing_comments.setAligned(false);
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(2);
        ts.align_assignment_operators.setTabs(true);
        ts.align_commas.set(false);
        ts.align_terms.setAligned(false);
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(true);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(getProject());
        final Document doc = documentManager.getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(false);
        wa.setSmartTabs(false);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult3A.java");
    }

    public final void testAlignmentWithMaxTabs() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest3B.java");
        final PsiFile file = getFile();
        ts.align_trailing_comments.setAligned(false);
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(2);
        ts.align_assignment_operators.setTabs(true);
        ts.align_commas.set(false);
        ts.align_terms.setAligned(false);
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(true);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(true);
        wa.setSmartTabs(false);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult3B.java");
    }

    public final void testAlignmentWithLeadingTabs() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest3C.java");
        final PsiFile file = getFile();
        ts.align_trailing_comments.setAligned(false);
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(2);
        ts.align_assignment_operators.setTabs(true);
        ts.align_commas.set(false);
        ts.align_terms.setAligned(false);
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(true);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(true);
        wa.setSmartTabs(true);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult3C.java");
    }

    public final void testNoTabNoModifierAlignment() throws Exception
    {
        ts.align_modifiers.setRearrange(false);
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest3D.java");
        final PsiFile file = getFile();
        ts.align_trailing_comments.setAligned(false);
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(2);
        ts.align_assignment_operators.setTabs(true);
        ts.align_commas.set(false);
        ts.align_terms.setAligned(false);
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(true);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(false);
        wa.setSmartTabs(false);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult3D.java");
    }

    public final void testLeaveUnknownLinesIntact() throws Exception
    {
        ts.align_modifiers.set(true);
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest4.java");
        final PsiFile file = getFile();
        ts.align_trailing_comments.setAligned(false);
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(2);
        ts.align_assignment_operators.setTabs(true);
        ts.align_commas.set(false);
        ts.align_terms.setAligned(false);
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(true);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(false);
        wa.setSmartTabs(false);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult4.java");
    }

    public final void testRemoveExcessiveExistingTabs() throws Exception
    {
        ts.align_modifiers.set(true);
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest5.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(false);
        ts.align_commas.set(false);
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(2);
        ts.align_trailing_comments.setTabs(true);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(2);
        ts.align_variable_names.setTabs(true);
        ts.align_variable_types.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(true);
        wa.setSmartTabs(false);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult5.java");
    }

    public final void testMultilineDeclarations() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest6.java");
        final PsiFile file = getFile();

        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(2);
        ts.align_assignment_operators.setTabs(false);
        ts.align_trailing_comments.setAligned(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        ts.align_commas.set(false);
        ts.align_terms.setAligned(true);
        ts.align_terms.setCharacters(1);
        ts.align_terms.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(true);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_modifiers.set(false);

        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(false);
        wa.setSmartTabs(false);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult6.java");
    }

    public final void testConditionalStatementAlignment() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest7.java");
        final PsiFile file = getFile();

        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(1);
        ts.align_assignment_operators.setTabs(false);
        ts.align_commas.set(false);
        ts.align_terms.setAligned(true);
        ts.align_terms.setCharacters(0);
        ts.align_terms.setTabs(false);
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_modifiers.set(false);
        ts.align_if_stmt_conditionals.set(true);
        ts.align_if_stmt_conditionals.setCharacters(1);
        ts.align_if_stmt_conditionals.setTabs(false);

        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(false);
        wa.setSmartTabs(false);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult7.java");
    }

    public final void testArrayDeclarationAlignment() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest8.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(1);
        ts.align_assignment_operators.setTabs(false);
        ts.align_commas.set(true);
        ts.align_commas.setCharacters(0);
        ts.align_semicolons.set(true);
        ts.align_semicolons.setCharacters(0);
        ts.align_terms.setAligned(true);
        ts.align_terms.setCharacters(1);
        ts.align_terms.setTabs(false);
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_modifiers.set(false);

        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(false);
        wa.setSmartTabs(false);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult8.java");
    }

    public final void testMalformedUnalignedModifiers() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest9.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(1);
        ts.align_assignment_operators.setTabs(false);
        ts.align_commas.set(false);
        ts.align_commas.setCharacters(0);
        ts.align_terms.setAligned(true);
        ts.align_terms.setCharacters(1);
        ts.align_terms.setTabs(false);
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_modifiers.setRearrange(false);

        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(false);
        wa.setSmartTabs(false);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult9A.java");

    }

    public final void testMalformedAlignedModifiers() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest9.java");
        final PsiFile file = getFile();
        ts.align_assignment_operators.set(true);
        ts.align_assignment_operators.setCharacters(1);
        ts.align_assignment_operators.setTabs(false);
        ts.align_commas.set(false);
        ts.align_commas.setCharacters(0);
        ts.align_terms.setAligned(true);
        ts.align_terms.setCharacters(1);
        ts.align_terms.setTabs(false);
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_modifiers.setRearrange(true);

        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.useTabChar(false);
        wa.setSmartTabs(false);
        wa.setTabSize(4);
        wa.setIndent(4);

        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult9.java");
    }

    public final void testUnalignedModifiersAndVarType() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest10.java");
        final PsiFile file = getFile();
        ts.align_modifiers.setRearrange(false);
        ts.align_variable_types.set(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(8);
        ts.align_variable_names.setTabs(false);
        ts.align_assignment_operators.set(false);
        ts.align_trailing_comments.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult10A.java");
    }

    public final void testCastAndMultipleDecls() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest11.java");
        final PsiFile file = getFile();
        ts.align_modifiers.setRearrange(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_assignment_operators.set(true);
        ts.align_typecast_open_parend.set(true);
        ts.align_typecast_close_parend.set(true);
        ts.align_trailing_comments.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult11.java");
    }

    public final void testCastAndMethodCalls() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest12.java");
        final PsiFile file = getFile();
        ts.align_modifiers.setRearrange(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_assignment_operators.set(true);
        ts.align_typecast_open_parend.set(true);
        ts.align_typecast_close_parend.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_trailing_comments.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult12.java");
    }

    public final void testSubmittedExample1() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest13.java");
        final PsiFile file = getFile();
        ts.align_modifiers.setRearrange(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_assignment_operators.set(true);
        ts.align_typecast_open_parend.set(true);
        ts.align_typecast_close_parend.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_trailing_comments.set(true);
        ts.delimit_by_blank_lines.set(false);
        css.SPACE_BEFORE_METHOD_CALL_PARENTHESES = true;
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult13.java");
    }

    public final void testTrailingComments() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest14.java");
        final PsiFile file = getFile();
        ts.align_modifiers.setRearrange(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_assignment_operators.set(true);
        ts.align_typecast_open_parend.set(true);
        ts.align_typecast_close_parend.set(true);
        ts.align_method_call_open_parend.set(true);
        ts.align_method_call_close_parend.set(true);
        ts.align_trailing_comments.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult14.java");
    }

    public final void testMethodDeclarationAlignment() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/MethodDeclarationAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_modifiers.setRearrange(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_assignment_operators.set(true);
        ts.align_typecast_open_parend.set(true);
        ts.align_typecast_close_parend.set(true);
        ts.align_method_decl_open_parend.set(true);
        ts.align_method_decl_close_parend.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_trailing_comments.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/MethodDeclarationAlignmentResult1.java");
    }

    public final void testRightJustifiedConstants() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest15.java");
        final PsiFile file = getFile();
        ts.align_modifiers.setRearrange(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_assignment_operators.set(true);
        ts.align_typecast_open_parend.set(true);
        ts.align_typecast_close_parend.set(true);
        ts.align_other_open_parend.set(true);
        ts.align_other_close_parend.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_trailing_comments.set(true);
        ts.right_justify_numeric_literals.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult15.java");
    }

    public final void testRightJustifiedNegativeConstants() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest15A.java");
        final PsiFile file = getFile();
        ts.align_modifiers.setRearrange(false);
        ts.align_variable_types.set(true);
        ts.align_variable_types.setCharacters(1);
        ts.align_variable_types.setTabs(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_assignment_operators.set(true);
        ts.align_typecast_open_parend.set(true);
        ts.align_typecast_close_parend.set(true);
        ts.align_other_open_parend.set(true);
        ts.align_other_close_parend.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_trailing_comments.set(true);
        ts.right_justify_numeric_literals.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult15A.java");
    }

    public final void testUnalignedModsAndTypes() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest16.java");
        final PsiFile file = getFile();
        ts.align_modifiers.setRearrange(false);
        ts.align_modifiers.set(false);
        ts.align_variable_types.set(false);
        ts.align_variable_names.set(true);
        ts.align_variable_names.setCharacters(1);
        ts.align_variable_names.setTabs(false);
        ts.align_assignment_operators.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_trailing_comments.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult16.java");
    }

    public final void testCommentAlignmentAfterMethod() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/CommentAlignmentTest1.java");
        final PsiFile file = getFile();
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/CommentAlignmentResult1.java");
    }

    public final void testCommentAlignmentAfterField() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/CommentAlignmentTest2.java");
        final PsiFile file = getFile();
        ts.align_trailing_comments.set(true);
        ts.align_trailing_comments.setCharacters(1);
        ts.align_trailing_comments.setTabs(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/CommentAlignmentResult2.java");
    }

    public final void testClassInitializer() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ClassAlignmentTest1.java");
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
        super.checkResultByFile("/com/wrq/tabifier/parse/ClassAlignmentResult1.java");
    }

    public final void testTryCatch() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ClassAlignmentTest2.java");
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
        css.SPACE_BEFORE_METHOD_CALL_PARENTHESES = false;
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ClassAlignmentResult2.java");
    }

    public final void testCommentFollowingDecl() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ClassAlignmentTest3.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ClassAlignmentResult3.java");
    }

    public final void testContributedTest1() throws Exception
    {
        /* Courtesy of Joe Martinez */
        configureByFile("/com/wrq/tabifier/parse/ContributedTest1.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_modifiers.setRearrange(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ContributedResult1.java");
    }

    public final void testContributedTest2() throws Exception
    {
        /* Courtesy of Joe Martinez */
        configureByFile("/com/wrq/tabifier/parse/ContributedTest2.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ContributedResult2.java");
    }

    public final void testContributedTest3() throws Exception
    {
        /* Courtesy of Joe Martinez */
        configureByFile("/com/wrq/tabifier/parse/ContributedTest3.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ContributedResult3.java");
    }

    public final void testContributedTest4() throws Exception
    {
        /* Courtesy of Jens Albers */
        configureByFile("/com/wrq/tabifier/parse/ContributedTest4.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.right_justify_numeric_literals.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ContributedResult4.java");
    }

    public final void testContributedTest5A() throws Exception
    {
        /* courtesy of Joe Martinez. */
        /** problems using align_params without align_expressions.  Seems to be some problem with the combination
         * of the two, hence four tests based on his supplied example.
         */
        configureByFile("/com/wrq/tabifier/parse/ContributedTest5.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_initial_params.set(false);
        ts.align_subsequent_params.set(false);
        ts.align_terms.set(false);
        ts.delimit_method_declarations.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ContributedResult5AB.java");
    }

    public final void testContributedTest5B() throws Exception
    {
        /* courtesy of Joe Martinez. */
        /** problems using align_params without align_expressions.  Seems to be some problem with the combination
         * of the two, hence four tests based on his supplied example.
         */
        configureByFile("/com/wrq/tabifier/parse/ContributedTest5.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_initial_params.set(false);
        ts.align_subsequent_params.set(false);
        ts.align_terms.set(true);
        ts.delimit_method_declarations.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ContributedResult5AB.java");
    }

    public final void testContributedTest5C() throws Exception
    {
        /* courtesy of Joe Martinez. */
        /** problems using align_params without align_expressions.  Seems to be some problem with the combination
         * of the two, hence four tests based on his supplied example.
         */
        configureByFile("/com/wrq/tabifier/parse/ContributedTest5.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_terms.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ContributedResult5CD.java");
    }

    public final void testContributedTest5D() throws Exception
    {
        /* courtesy of Joe Martinez. */
        /** problems using align_params without align_expressions.  Seems to be some problem with the combination
         * of the two, hence four tests based on his supplied example.
         */
        configureByFile("/com/wrq/tabifier/parse/ContributedTest5.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_terms.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ContributedResult5CD.java");
    }

    public final void testContributedTest6() throws Exception
    {
        /* courtesy of Joe Martinez. */
        /** problems with align_expressions (align_terms) = false.
         */
        configureByFile("/com/wrq/tabifier/parse/ContributedTest6.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_initial_param_commas.set(false);
        ts.align_subsequent_param_commas.set(false);
        ts.align_initial_params.set(true);
        ts.align_subsequent_params.set(true);
        ts.align_terms.set(false);
        ts.align_method_call_close_parend.set(false);
        ts.align_method_decl_close_parend.set(false);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ContributedResult6.java");
    }

    public final void testRJNumerics() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest17.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_modifiers.set(true);
        ts.align_assignment_operators.set(true);
        ts.align_terms.set(true);
        ts.right_justify_numeric_literals.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult17.java");
    }
    public final void testSingleLineCodeBlock() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/DeclarationAlignmentTest18.java");
        final PsiFile file = getFile();
        ts.delimit_by_blank_lines.set(false);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_modifiers.set(true);
        ts.align_assignment_operators.set(true);
        ts.align_terms.set(true);
        ts.right_justify_numeric_literals.set(true);
        ts.align_braces.set(true);
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/DeclarationAlignmentResult18.java");
    }

    public final void testAnnotation1() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/AnnotationTest1.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AnnotationResult1.java");
    }

    public final void testAnnotationAlignment1() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/AnnotationAlignmentTest1.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        ts.delimit_by_blank_lines.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_modifiers.set(true);
        ts.align_assignment_operators.set(true);
        ts.align_terms.set(true);
        ts.right_justify_numeric_literals.set(true);
        ts.align_braces.set(true);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AnnotationAlignmentResult1.java");
    }

    public final void testAnnotationAlignment2() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/AnnotationTest2.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        ts.delimit_by_blank_lines.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_modifiers.set(true);
        ts.align_assignment_operators.set(true);
        ts.align_terms.set(true);
        ts.right_justify_numeric_literals.set(true);
        ts.align_braces.set(true);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AnnotationResult2.java");
    }

    public final void testAnnotationAlignment3() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/AnnotationTest3.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        ts.delimit_by_blank_lines.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_modifiers.set(true);
        ts.align_assignment_operators.set(true);
        ts.align_terms.set(true);
        ts.right_justify_numeric_literals.set(true);
        ts.align_braces.set(true);
        ts.align_method_declaration_initial_param_commas.set(false);
        ts.align_method_declaration_subsequent_param_commas.set(false);
        ts.align_method_decl_close_parend.set(false);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/AnnotationResult3.java");
    }
 
    public final void testModifierAlignment1() throws Exception
    {
        configureByFile("/com/wrq/tabifier/parse/ModifierTest1.java");
        final PsiFile file = getFile();
        final Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(file);
        ts.delimit_by_blank_lines.set(true);
        ts.align_variable_types.set(true);
        ts.align_variable_names.set(true);
        ts.align_modifiers.set(true);
        ts.align_assignment_operators.set(true);
        ts.align_terms.set(true);
        ts.right_justify_numeric_literals.set(true);
        ts.align_braces.set(true);
        ts.align_method_declaration_initial_param_commas.set(false);
        ts.align_method_declaration_subsequent_param_commas.set(false);
        ts.align_method_decl_close_parend.set(false);
        final TabifierActionHandler wa = new TabifierActionHandler();
        wa.tabifyPsiFile(file, 0, file.getTextRange().getEndOffset(), css, ts, doc);
        super.checkResultByFile("/com/wrq/tabifier/parse/ModifierResult1.java");
    }
}
