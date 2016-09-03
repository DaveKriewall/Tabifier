public class DeclarationAlignmentTest17
{
    /*
     * dummy modifier values -- chosen to not conflict with other Java modifiers
     */
    public static final int OVERRIDDEN         =   0x2000;
    public static final int CONSTRUCTOR        =   0x4000;
    public static final int GETTER             =   0x8000;
    public static final int SETTER             = 0x100000;
    public static final int OTHER_METHOD       =  0x10000;
    public static final int OVERRIDING         =  0x20000;
    public static final int INIT_TO_ANON_CLASS =  0x40000;
    public static final int STATIC_INITIALIZER =  0x80000;

    // test of leading comment
    public void method() // trailing comment here
    {
        int a; /* non-trailing comment */ int b; /* trailing comment */
          // continuation trailing comment
        int c1; /* non-trailing comment */ int d2; /* trailing comment */
        // non-continuation comment
    }
}
