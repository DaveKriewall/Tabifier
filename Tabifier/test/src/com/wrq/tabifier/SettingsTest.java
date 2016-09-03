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

import com.wrq.tabifier.settings.TabifierSettings;
import junit.framework.TestCase;

/**
 * tests for Settings and TabifierSettings classes.
 */
public final class SettingsTest
        extends TestCase
{

    protected final void setUp() throws Exception
    {
    }

    public final void testCloning() throws Exception
    {
        final TabifierSettings s = new TabifierSettings();
        final TabifierSettings clone = (TabifierSettings) s.deepCopy();
        final boolean equals = s.equals(clone);
        assertTrue("cloned settings mismatch", equals);
    }

    public final void testBoolean() throws Exception
    {
        final TabifierSettings s = new TabifierSettings();
        final TabifierSettings clone = (TabifierSettings) s.deepCopy();
        clone.align_assignment_operators.setAligned(!s.align_assignment_operators.isAligned());
        final boolean equals = s.equals(clone);
        assertFalse("cloned settings mismatch", equals);
    }

    public final void testInteger() throws Exception
    {
        final TabifierSettings s = new TabifierSettings();
        final TabifierSettings clone = (TabifierSettings) s.deepCopy();
        clone.expression_parse_nesting_level.set(s.expression_parse_nesting_level.get() + 1);
        final boolean equals = s.equals(clone);
        assertFalse("cloned settings mismatch", equals);
    }

    public final void testString() throws Exception
    {
        final TabifierSettings s = new TabifierSettings();
        final TabifierSettings clone = (TabifierSettings) s.deepCopy();
        clone.debug_output.set(s.debug_output.get() + "xx");
        final boolean equals = s.equals(clone);
        assertFalse("cloned settings mismatch", equals);
    }

    public final void testTabOrSpace1() throws Exception
    {
        final TabifierSettings s = new TabifierSettings();
        final TabifierSettings clone = (TabifierSettings) s.deepCopy();
        clone.align_assignment_operators.setCharacters(
                s.align_assignment_operators.getCharacters() + 1);
        final boolean equals = s.equals(clone);
        assertFalse("cloned settings mismatch", equals);
    }

    public final void testTabOrSpace2() throws Exception
    {
        final TabifierSettings s = new TabifierSettings();
        final TabifierSettings clone = (TabifierSettings) s.deepCopy();
        clone.align_assignment_operators.setTabs(
                !s.align_assignment_operators.isTabs());
        final boolean equals = s.equals(clone);
        assertFalse("cloned settings mismatch", equals);
    }
}
