public class ClassAlignmentTest1
{
           int                 field1;
    static int                 field2;
    static ClassAlignmentTest1 singleton;

    void method1()
    {
        field1 = 2;
    }

    static
    {
        field2    = 3;
        singleton = new ClassAlignmentTest1();
    }
}
