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

/**
 * Contains a variety of Java syntax to illustrate behavior of the tabifier's options.
 * An option controls whether or not IDEA's code layout tool is performed on this file
 * before tabifying.  This allows you to see the combined effect of other code layout options.
 *
 * The sample file is laid out by sections corresponding to the configuration option tree.
 */
public abstract class Sample
{
    /**
     * Field / Variable Declarations and Assignment Statements
     */

    /**
     * Modifiers are reserved words like "public", "private", "final", "static", etc.
     * "Align modifiers" causes the entire string of modifiers to be aligned.  Since
     * modifiers usually appear at the beginning of the line anyway, this option rarely
     * affects output visibly.
     *
     * "Rearrange and align modifiers" causes the string of modifiers to be rearranged in
     * the order specified by java.lang.reflect.Modifier and aligned with each other.
     * Mutually exclusive modifiers (like "public" and "private") occupy the same vertical
     * space.
     */
    public final String court = "s";
    final String veryLong = "abcdefghijklmnopqrstuvwxyz";// trailing comment 1
    private boolean isOn = true; // comment 2

    boolean notAssigned;
    final boolean isConnectedOrWillBeSoon = true; // trailing comment 3
    final char[] digit = {'0', '1', '2'}; // trailing comment 4

    /**
     * Expressions
     */

    /**
     * Use the option "Stop aligning expressions after N levels of recursion"
     * to provide some control over the appearance of deeply nested expressions.
     * Any subexpression deeper than N levels (where parenthesized expressions are
     * one level higher than the outer expression) will not be reformatted, except
     * that if it contains multiple lines, these will all be aligned at the beginning
     * of the subexpression.
     */

    abstract int m(int a, int b);
    abstract Object getObject(int type);

    void expressions()
    {
        // right justify integer literals
        int literalA = 25;
        int literalB = 5;
        int literalC = 500;

        m(1, 100); m(500, 5);    // note effect of Method Calls..Align Subsequent Parameters
        m(10, 1000); m(20, 500);
        m(400,
                5);

        // recursion example
        literalA = 25 +
                35 +
                (27 *   4) +
                (4     * 300) +
                (literalB   >3?5
                :6) +
                (5 * (20%   6)) +
                (44*(300% 2));

        // typecasts -- disable "Align assignment operators" for full effect
        Integer ii;
        Float j;
        String str;
        Object o = null; // return value from some method, say
        ii = (Integer) getObject(1);
        str = (String) getObject(10);
        j = (Float) getObject(100);

        // Align conditional expression example
        literalA = str.equals("X") ? 10 : 20;
        int jj = literalA > 5 ? 5 : 0;
        int multiline = str.equals("Long String") ? m(4000, 200)
            : m(20, 1);

        // Align 'new' operator example
        Integer intgr = new Integer(50);
        Float f = new Float("5.0");

        // Align arithmetic, logical and relational operators, and parentheses
        // (set recursion depth to vary effect, up to level 5)
        if (((literalA + 3 < jj + literalB) && str.equals("X")) ||
                ((jj + 25 >= literalC) && jj != 27))
            System.out.println("it's true");
    }

    /**
     * Method Declarations.  Alignment of parameter modifier, type and name is
     * controlled by the corresponding options for declaration statements above.
     * First parameter of each line can be aligned independently of subsequent
     * parameters.
     *
     * If first parameters are aligned, these parameters align to the position
     * of the first parameter, which is either immediately to the right of the
     * open parenthesis or on a continuation line.  If first parameters are not
     * aligned, all other lines of parameters appear on a continuation line.
     * (Continuation lines are indented two tabstops from the current left margin.)
     *
     * Parameters on continuation lines (if any) are aligned separately from those
     * at the normal indent level.
     */
    
    abstract void lotsOfParamsMethod(int param1, float floatingPointParam,
                                    final String p, Object o,
                                    int param6, boolean isItTrue);

    /**
     * Method Calls.
     *
     * Use the option "Align calls to methods with N identical leading characters
     * together" to control the degree to which method calls are considered similar
     * and therefore should be aligned as a group.  With the default value of "4",
     * the functions fn_ABS, fn_ARCCOS and fn_ARCSIN are all grouped together because
     * they all begin with "fn_A".  By using a value of "5", the ARCCOS and ARCSIN
     * functions are grouped together but fn_ABS is grouped separately.
     *
     * See comment above for description of behavior when first parameter of each
     * line is or is not aligned.
     */
    abstract int fn_ABS(int x);
    abstract float fn_ARCCOS(int degrees);
    abstract float fn_ARCSIN(int degrees);

    void makeCalls()
    {
        int i = fn_ABS(25) + fn_ABS(300);
        int j = fn_ABS(2) - fn_ABS(2);
        float f1 = fn_ARCCOS(180) / fn_ARCSIN(45);
        float fl2 = fn_ARCCOS(25) * fn_ARCSIN(150);
        lotsOfParamsMethod(25, 2.7f,
                "P string", new String("aa"),
                60 * i, j < 20);
        method1(m(2, 40), true, 300);
        method1(25, false, m(1000, 25));
        m(2, 10);
        method1(150, true, 300);
    }

    /**
     * If and While Statements.
     */
    private void method1(int param1,
                         final boolean param2,
                         int p3)
    {
        int j = 5;

        if (param1 == p3)
            p3 = 6;
        else if (param1 == 2)
            method1(param1, true, 3);
        else
            param1 = 7;

        if (param1 == p3 &&
                j > 5)
            p3 = 7;

        if (param1 == p3) p3 = 6;
        else if (j == param1) param1 = p3;

        /**
         * Miscellaneous Spacing Options.
         */

        expressions(); // space between empty parentheses
        String[] sa = new String[] {}; // space between empty braces
        j = 8;   // eliminate space before assignment operators -- note:
        p3 += 6; // requires that operators not be aligned, or have 0 spaces padding

        /**
         * Force space before array initializer left brace; and
         * force spaces within non-empty array initializer braces.
         */
        String[] sa2 = new String[]{"abc", "def"};

        /**
         * General Alignment Options.
         */

        /**
         * multiple statements per line.
         */
        j = 5; p3 = 100;
        param1 = 250; j -= p3;
    }


}
