/**
 * test class for indentation.   Test is based on line position so don't insert or remove lines.
 */
package testData.com.wrq.tabifier.parse.DocumentParserTest;

public class TestIndent
{
    int indent1;

    private void method1()
    {
        int indent2 = 0;

        switch (indent2)
        {
            case 0:
                indent2 = 1; // indent level 4 if 'case' is indented from 'switch'

            default:
                indent2 = 2;
        }
    }
}
