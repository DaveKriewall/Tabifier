public class ClassAlignmentTest2
{
    void method()
    {
        Integer i = new Integer(2);
        int j, k, l;
        try
        {
            int m;
            String str;
            j = i.intVal();
            str = "value=" + Integer.parseInt("234");
        }
        catch (NumberFormatException nfe)
        {
            int abc;
            String s;
        }
    }
}
