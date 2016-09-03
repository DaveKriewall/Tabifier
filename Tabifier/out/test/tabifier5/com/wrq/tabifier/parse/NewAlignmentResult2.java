package testData.com.wrq.tabifier.parse;

abstract public class NewAlignmentTest2
{
    private static final String ALIGN_COMMAS                = "align_commas";
    private static final String ALIGN_EXPRESSION_STATEMENTS = "align_expression_statements";
    private static final String ALIGN_IF_KEYWORDS           = "align_if_keywords";
    private static final String ALIGN_IF_STMT_CONDITIONALS  = "align_if_stmt_conditionals";
    private static final String ALIGN_IF_STMT_CLOSE_PAREND  = "align_if_stmt_close_parend";
    private static final String ALIGN_IF_STMT_NEXTLINES     = "align_if_stmt_nextlines";

    class ColumnSetting
    {
        boolean align;
        String  name;
        int     nCharacters;
        int     indent;

        ColumnSetting(boolean align, String name)
        {
            this.align       = align;
            this.name        = name;
            this.nCharacters =  5;
            this.indent      = 10;
        }

        ColumnSetting(boolean align, int nCharacters, String name)
        {
            this(align, name);
            this.nCharacters = nCharacters;
        }

        ColumnSetting(boolean align, int nCharacters, int indent, String name)
        {
            this(align, nCharacters, name);
            this.indent = indent;
        }
    }

    abstract void addSetting(ColumnSetting cs);

    void method()
    {
        addSetting(new ColumnSetting(false, ALIGN_COMMAS                                                                    ));
        addSetting(new ColumnSetting(true , ALIGN_EXPRESSION_STATEMENTS                                                     ));
        addSetting(new ColumnSetting(true , ALIGN_IF_KEYWORDS                                                               ));
        addSetting(new ColumnSetting(true , 15                         , ALIGN_IF_STMT_CONDITIONALS                         ));
        addSetting(new ColumnSetting(true , ALIGN_IF_STMT_CLOSE_PAREND                                                      ));
        addSetting(new ColumnSetting(true ,  5                         , 1                         , ALIGN_IF_STMT_NEXTLINES));
    }
}
