import java.util.List;
import java.util.ListIterator;

public class MethodCallAlignmentTest2
{
    class Line {
        List getTokens()
        {
            return null;
        }
    }
    Line         line;
    ListIterator iterator;
    Object       currentToken;
    int          index;

    public LineIteratorAndCurrentToken(Line line)
    {
        this.line    = line;
        index        = 0;
        iterator     = line.getTokens().listIterator();
        currentToken = null;
    }
}
