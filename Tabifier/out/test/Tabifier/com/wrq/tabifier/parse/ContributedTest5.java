public class ContributedTest5
{
    class TM {
        Object createTransaction() {return null;}
        Object spOPS_GET_SESSION(int n, String s, TM transaction) { return null; }
    }

    void method()
    {
        TM OpsTransactionManager = new TM();
        TM OpsMapStoreProcs = new TM();
        Object transaction, map;

        transaction = OpsTransactionManager.createTransaction();
        map         = OpsMapStoreProcs.spOPS_GET_SESSION(sessionNumber,
                                                                      Integer.toString(SESSION_TIME_OUT),
                                                               transaction);
    }
}
