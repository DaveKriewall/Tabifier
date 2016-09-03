public class NewAlignmentTest5
{
    void method()
    {
        int i = 0;
	MyInteger result;

        switch (i)
        {
            case 1:
                result = new MyInteger(
                    3,
                    new MyInteger(
                    5,
                    12,
                    new Integer(5).intValue()),
                    2342345);
                break;
        }
    }

    class MyInteger {
        int param1;
        int param2;
        int param3;

        MyInteger(int i1, int i2, int i3)
        {
            param1 = i1;
            param2 = i2;
            param3 = i3;
        }
    }
}
