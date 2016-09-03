public class Sample
{
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
}