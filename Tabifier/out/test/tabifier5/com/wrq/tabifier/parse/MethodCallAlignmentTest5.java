public class MethodCallAlignmentTest5
{
    void method(int p1, String p2, int p3, int p4, String p5, int p6) {}

    void caller()
    {
        method(1, "abc", 3, 4, "5", 6);
        method(100, "2", 003, 0x4, "five", 60/10);
        method(1, "abc", 3,
               4, "5", 6);
        method(1, "abc",
               3, 4,
               "five", 6);
    }
}
