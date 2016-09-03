public class ContributedTest1
{
    public static final     String                  JOB_NAME  = "OpsCheckOrderStatusBatchJob";
    private static final    String                  PADDING   = "000000000000000";
    private static volatile boolean                 isRunning = false;
    private static final    Object                  lock      = new Object();
    private static final    opsx.log.OpsLogCategory logCat    = (opsx.log.OpsLogCategory) opsx.log.OpsLogCategory.getInstance(
            OpsCheckOrderStatusBatchJob.class);
}
