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

/**
 * This class is parsed by IDEA into a PSI tree, which is read by the new testing harness and compiled into a new
 * PsiFile.
 * <p>
 * Sections may be labelled with a Javadoc comment containing the reserved phrase "SOURCE-MARKER <n>" which can be
 * located by test code. Offsets of subsequent text are calculated relative to the end of this comment. This allows new
 * text to be inserted without disrupting offset-based tests.
 */
public final class PsiTestClass
{
    /**
     * SOURCE_MARKER 1.
     * <p>
     * Testing offset of "court" variable and SOURCE_MARKER logic. Leave as first field definition for test.
     */
    final String court = "s";
    final String veryLong = "abcdefghijklmnopqrstuvwxyz";// trailing comment 1
    private boolean isOn = true;
    boolean notAssigned;
    final boolean isConnectedOrWillBeSoon = true; // trailing comment 2
    final char[] digit = {'0', '1', '2', '4', '5', '6', '7', '8', '9'}; // trailing comment 3
    String s1 = "1", // comment 1
           s2 = "2", // comment 2
           s3 = "3";
    private final int a = 10;
    final private int b = 2;
    final int c = 4;
    private int d;
    private int e = 100;

    /**
     * sample method.
     */
    final void method(final boolean x)
    {
        /** SOURCE_MARKER 2.
         * variable declarations
         */
        final boolean myVar; // trailing comment 5
        boolean myVar2 = false;
        final boolean /* bad comment placement */ myVar3;

        /** some assignment statements for alignment */
        myVar = x;
        myVar2 = !myVar; /** silly assignments, huh? */
        if (myVar)
        {
            myVar3 = myVar2; // comment 6
            d /* comment */ = /* comment 2*/ 5 /* comment 3 */;                       // comment 7
            /**
             * statement description
             */
            e = d + b;// comment 8
        }
        else
        {
            myVar3 = !myVar2;
            e = e + 1;
            /** SOURCE_MARKER 3. */
            d = a;
        }
        isOn = myVar3;
    }

    /**
     * experiment with modifiers.
     */
    public static final String PARAM_MAIN_PANEL      = "mainpanel";
    private static final String VALUE_MAIN_PANEL_TABS = "tabs";
    public static final String DEFAULT_MAIN_PANEL    = VALUE_MAIN_PANEL_TABS;

    public static void main(final String[] args)
    {
        int i = 1;
        if (args.length == 0) i = 0;
        else                  i = 1;
        String s2;
    }
}
