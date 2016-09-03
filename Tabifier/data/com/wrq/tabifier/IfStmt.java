public class Sample
{
    private void method1(int param1,
                         final boolean param2,
                         int p3)
    {
        int j = 5;

        if (param1 == p3)
            p3 = 6;
        else if (param1 == 2)
            method1(param1, true, 3);
        else
            param1 = 7;

        if (param1 == p3 &&
                j > 5)
            p3 = 7;

        if (param1 == p3) p3 = 6;
        else if (j == param1) param1 = p3;
    }
}