public class MethodCallAlignmentTest3
{
    void foo(Object x, Object y) {}
    Object bar() { 
        return new Object(); 
    }
    void method()
    {
        foo(new Object(), bar());
    }
}
