public abstract class Spacing
{
    private void method1(int param1,
                 int p3)
    {
        expressions(); // space between empty parentheses
        String[] sa = new String[] {}; // space between empty braces
        j = 8;   // eliminate space before assignment operators -- note:
        p3 += 6; // requires that assignment operators not be aligned, or have 0 spaces padding

        /**
         * Force space before array initializer left brace; and
         * force spaces within non-empty array initializer braces.
         */
        String[] sa2 = new String[]{"abc", "def"};
    }
}