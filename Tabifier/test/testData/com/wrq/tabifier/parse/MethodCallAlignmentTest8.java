import org.apache.log4j.*;

public class MethodCallAlignmentTest8
{
    private static Logger  logger = Logger.getLogger("com.wrq.tabifier");

    abstract boolean get();

    void method()
    {
        logger.debug("no_selection_behavior setting: " +
                    (get() ? "tabify entire file"
                    : "tabify only selection (or line if no selection)"));
    }
}
