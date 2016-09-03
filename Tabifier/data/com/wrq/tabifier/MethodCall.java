abstract public class MethodCallAlignmentTest4
{
    void method(int p1, String p2, int p3, int p4, String p5, int p6) {}

    void caller()
    {
        method(1,      "abc", 3,   4,   "5",    6      );
        method(100,    "2",   003, 0x4, "five", 60 / 10);
        method(1,      "abc", 3,
               4,      "5",   6                        );
        method(1,      "abc",
               3,      4,
               "five", 6                               );
    }

    /* test of method call grouping by name: threshold = N common leading characters. */
    abstract void m001A(int p, String s);
    abstract void m001B(int p, String s, int p3);
    abstract void m001CCC(int p, String s);
    abstract void m002A(String s, int p);
    abstract void m002B(int p, String s);

    void methodcalls()
    {
        m001A(3, "abc");
        m002A("abc", 3);
        m001B(23, "d", 23);
        m001CCC(100, "defg");
        m002B(25, "hij");
    }
}
