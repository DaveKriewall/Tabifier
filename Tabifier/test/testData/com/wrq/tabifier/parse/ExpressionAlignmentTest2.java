public class ExpressionAlignmentTest2
{
    boolean b1, b2, b3;
    void setSelected(int i) {};
    void method()
    {
        setSelected(b1 ? (b2? 1:10)
                    : (b3 ?100:0));
    }
}
