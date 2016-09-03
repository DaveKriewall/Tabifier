public class MethodCallAlignmentTest1
{
    void skipWhiteSpace() {}
    void markAndAdvance(String s) {}
    void parseExpressions(String s) {}

    void method()
    {
        skipWhiteSpace  (       );
        markAndAdvance  ("12345"); // align "if" and "else" keywords
        parseExpressions("abc"  );
    }
}
