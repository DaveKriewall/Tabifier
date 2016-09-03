public class ModifierTest
{
    public int x1;
    private int x2;
    protected int x3;
    int x4;

    public final int x5 = 0;
    protected final int x6 = 0;
    volatile int x7; // volatile and final may occupy the same column if transient is not present

    final int x8 = 0;
    volatile int x9 = 0;
    transient int x10 = 0;
    volatile transient int x11;
    final transient int x12;
}