public class NoSpaceBeforeAssignmentOperator
{
    void method()
    {
        int i= 3;
        i+= 5;
        if ((i-= 3) > 5) i*= 2;
    }
}
