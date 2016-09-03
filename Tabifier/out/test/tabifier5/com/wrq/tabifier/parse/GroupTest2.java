public class GroupTest2
{
    abstract int method1(int i, int j);
    abstract int method2(int i, int j);

    void method()
    {
        method1(5, method2(20, 30));
        method1(100, method2(3, 400));
        method2(50, method1(400, 5));
    }
}
