public final class ClassParser
{
    private String fieldSequence;
    private short LBrace;
    private long RBrace;
    private byte trailingComments;
    private int  columnIndex;

   /*
    *     parent
    *       |
    *     LBrace
    *       |
    *     classSequence: --> class --> trailing comments
    *       |                  |
    *       |                field --> ...
    *       |
    *     RBrace
    */
    public ClassParser()
    {
    }
}
