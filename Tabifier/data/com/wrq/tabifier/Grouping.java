public class Sample
{
    // Spacing options come into play when blank lines or dissimilar statement types separate groups of statements
    // that could be aligned together.
    //
    private void method()
    {
        int i;
        String str;

        boolean b;
        /**
         * non-blank lines.
         */
        float f;

        i = 3;

        str = "string";
        b = true;
        if (i == 4)
        {
            str = "four";
            int jj;
            b = false;
            String s2;
            jj = 4;
        }
    }
}