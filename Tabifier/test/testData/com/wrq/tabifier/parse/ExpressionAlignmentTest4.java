public class ExpressionAlignmentTest4
{
    class Logger
    {
        void debug(String s)
        {
        }
    }

    void method()
    {
        Logger logger = new Logger();
        logger.debug("handle token '" +
                "tokenName" +
                "', append to " +
                (logger == null ? "start of column" : "'" + "previousTokenName" + "'"));
    }
}
