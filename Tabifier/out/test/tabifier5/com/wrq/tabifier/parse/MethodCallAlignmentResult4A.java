/* test of method call grouping by name: threshold = N common leading characters. */
abstract public class MethodCallAlignmentTest4
{
    abstract void m001A  (int    p, String s        );
    abstract void m001B  (int    p, String s, int p3);
    abstract void m001CCC(int    p, String s        );
    abstract void m002A  (String s, int    p        );
    abstract void m002B  (int    p, String s        );

    void methodcalls()
    {
        m001A  (3,     "abc"   );
        m002A  ("abc", 3       );
        m001B  (23,    "d",  23);
        m001CCC(100,   "defg"  );
        m002B  (25,    "hij"   );
    }
}
