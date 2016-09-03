abstract public class Sample
{
    /**
     * Field / Variable Declarations and Assignment Statements
     */

    /**
     * Modifiers are reserved words like "public", "private", "final", "static", etc.
     * "Align modifiers" causes the beginnings of each string of modifiers to be aligned.  Since
     * modifiers usually appear at the beginning of the line anyway, this option rarely
     * affects output visibly.
     *
     * "Rearrange and align modifiers" causes the string of modifiers to be rearranged in
     * the order specified by java.lang.reflect.Modifier and aligned with each other.
     * Mutually exclusive modifiers (like "public" and "private") occupy the same vertical
     * space.
     */
    final public String court = "s";
    final String veryLong = "abcdefghijklmnopqrstuvwxyz";// trailing comment 1
    private boolean isOn = true; // comment 2

    boolean notAssigned;
    final boolean isConnectedOrWillBeSoon = true; // trailing comment 3
    final char[] digit = {'0', '1', '2'}; // trailing comment 4

    int i, index;
    short s1, s;
}