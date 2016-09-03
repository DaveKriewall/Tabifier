public class MethodCallAlignmentTest9
{
    abstract int longFunctionName(int param1, int param2, int param3);

    void method()
    {
        int integer = longFunctionName(
                4,
                longFunctionName(
                        5,
                        6,
                        7),
                8);
    }
}
