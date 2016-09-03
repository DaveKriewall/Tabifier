/** basic 'if' statement alignment test. */
public class IfStmtTest1
{
    void method1(int param1, int p3)
    {
        if (param1 == p3) p3 = 6;
        else if (param1 == 2) method1(param1, 3);
            else if (p3 == 2) param1 = 10;
        else param1 = 7;
    }
}
