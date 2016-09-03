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

import com.wrq.tabifier.parse.LineFormatter;
import com.wrq.tabifier.parse.ModifierUtils;
import junit.framework.TestCase;

import java.lang.reflect.Modifier;

/**
 * Contains various tests of modifier handling and rearrangement.
 */
public final class ModifierTest
        extends TestCase
{
    public final void testCalculateLongestModifierString() throws Exception
    {
        final int[][] expectedResults = {
            {Modifier.FINAL, Modifier.FINAL},
            {Modifier.PUBLIC | Modifier.FINAL, Modifier.PUBLIC | Modifier.FINAL},
            {Modifier.PUBLIC | Modifier.PRIVATE, Modifier.PRIVATE},
            {Modifier.PUBLIC | Modifier.PROTECTED, Modifier.PROTECTED},
            {Modifier.PUBLIC | Modifier.PROTECTED | Modifier.FINAL, Modifier.PROTECTED | Modifier.FINAL},
            {Modifier.SYNCHRONIZED, Modifier.SYNCHRONIZED},
            {Modifier.SYNCHRONIZED | Modifier.TRANSIENT, Modifier.SYNCHRONIZED},
            {Modifier.FINAL | Modifier.VOLATILE, Modifier.VOLATILE}
        };
        int index = 0;
        for (int[] expectedResult : expectedResults) {
            assertEquals("incorrect calculation of modifier string length, test # " + index,
                    Modifier.toString(expectedResult[1]).length(),
                    ModifierUtils.calculateLongestModifierString(expectedResult[0]));
            index++;
        }
    }

    public final void testGenerateModifierStringNoTabs() throws Exception
    {
        final class Result
        {
            final int completeMask;
            final int lineMask;
            final String result;

            private Result(final int completeMask, final int lineMask, final String result)
            {
                this.completeMask = completeMask;
                this.lineMask = lineMask;
                this.result = result;
            }
        }

        final Result[] results = {
            new Result(Modifier.PUBLIC, Modifier.PUBLIC, "public"),                                                                                 // test #0
            new Result(Modifier.PUBLIC, 0, "      "),
            new Result(Modifier.PROTECTED, Modifier.PROTECTED, "protected"),
            new Result(Modifier.PROTECTED | Modifier.PUBLIC, Modifier.PROTECTED, "protected"),
            new Result(Modifier.PROTECTED | Modifier.PUBLIC, Modifier.PUBLIC, "public   "),
            new Result(Modifier.PUBLIC | Modifier.FINAL, Modifier.PUBLIC, "public      "),
            new Result(Modifier.PUBLIC | Modifier.FINAL, Modifier.FINAL,                                        "       final"),
            new Result(Modifier.PUBLIC | Modifier.FINAL, Modifier.PUBLIC | Modifier.FINAL,                      "public final"),
            new Result(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.FINAL, Modifier.PUBLIC | Modifier.FINAL, "public    final"),
            new Result(Modifier.SYNCHRONIZED | Modifier.TRANSIENT, Modifier.SYNCHRONIZED, "synchronized"),
            new Result(Modifier.SYNCHRONIZED | Modifier.TRANSIENT, Modifier.TRANSIENT,    "transient   "),                                          // test #10
            new Result(Modifier.SYNCHRONIZED | Modifier.TRANSIENT | Modifier.VOLATILE, Modifier.TRANSIENT | Modifier.VOLATILE, "transient volatile"),
            new Result(Modifier.SYNCHRONIZED | Modifier.TRANSIENT | Modifier.VOLATILE, Modifier.SYNCHRONIZED,                  "synchronized      "),
            new Result(Modifier.FINAL | Modifier.VOLATILE, Modifier.VOLATILE, "volatile"),
            new Result(Modifier.FINAL | Modifier.VOLATILE, Modifier.FINAL,    "final   "),
            new Result(Modifier.FINAL | Modifier.TRANSIENT, Modifier.TRANSIENT, "      transient"),                                                 // test #15
            new Result(Modifier.FINAL | Modifier.TRANSIENT, Modifier.FINAL,     "final          "),
            new Result(Modifier.VOLATILE | Modifier.TRANSIENT, Modifier.VOLATILE,                        "          volatile"),
            new Result(Modifier.VOLATILE | Modifier.TRANSIENT, Modifier.TRANSIENT,                       "transient         "),
            new Result(Modifier.VOLATILE | Modifier.TRANSIENT, (Modifier.VOLATILE | Modifier.TRANSIENT), "transient volatile"),
        };
        for (int i = 0; i < results.length; i++)
        {
            final Result r = results[i];
            final String s = new ModifierUtils().generateModifierString(r.completeMask, r.lineMask);
            assertEquals("test " + i + ": modifier string mismatch", r.result, s);
        }
    }

    public final void testGenerateModifierStringWithTabs() throws Exception
    {
        final class Result
        {
            final int completeMask;
            final int lineMask;
            final String result;

            private Result(final int completeMask, final int lineMask, final String result)
            {
                this.completeMask = completeMask;
                this.lineMask = lineMask;
                this.result = result;
            }
        }

        final Result[] results = {
            new Result(Modifier.PUBLIC, Modifier.PUBLIC, "public"),
            new Result(Modifier.PUBLIC, 0, "\t  "),
            new Result(Modifier.PROTECTED, Modifier.PROTECTED, "protected"),
            new Result(Modifier.PROTECTED | Modifier.PUBLIC, Modifier.PROTECTED, "protected"),
            new Result(Modifier.PROTECTED | Modifier.PUBLIC, Modifier.PUBLIC, "public\t "),
            new Result(Modifier.PUBLIC | Modifier.FINAL, Modifier.PUBLIC, "public\t\t"),
            new Result(Modifier.PUBLIC | Modifier.FINAL, Modifier.FINAL, "\t   final"),
            new Result(Modifier.PUBLIC | Modifier.FINAL, Modifier.PUBLIC | Modifier.FINAL, "public final"),
            new Result(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.FINAL,
                    Modifier.PUBLIC | Modifier.FINAL, "public\t  final"),
            new Result(Modifier.SYNCHRONIZED | Modifier.TRANSIENT, Modifier.SYNCHRONIZED, "synchronized"),
            new Result(Modifier.SYNCHRONIZED | Modifier.TRANSIENT, Modifier.TRANSIENT, "transient\t"),
            new Result(Modifier.SYNCHRONIZED | Modifier.TRANSIENT | Modifier.VOLATILE,
                    Modifier.TRANSIENT | Modifier.VOLATILE, "transient volatile"),
            new Result(Modifier.SYNCHRONIZED | Modifier.TRANSIENT | Modifier.VOLATILE,
                    Modifier.SYNCHRONIZED, "synchronized\t  "),
        };
        for (int i = 0; i < results.length; i++)
        {
            final Result r = results[i];
            String s = new ModifierUtils().generateModifierString(r.completeMask, r.lineMask); // , 0, 4, true);
            s = LineFormatter.tabify(s, 0, 4);
            assertEquals("test " + i + ": modifier string mismatch", r.result, s);
        }
    }


}
