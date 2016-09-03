public class ContributedTest6
{
    class OpsSession
    {
        Object getUser() { return null; }
        Object getSession() { return null; }
        Object getExchange() { return null; }
        Object getProperties() { return null; }
        Object getAccessRight() { return null; }
        Object isAdministrator() { return null; }
        Object getRootEntityOID() { return null; }
        Object getGroupOID() { return null; }
        void initialize(Object u, Object s, Object en, Object ex,
                        Object p, Object a, Object is, Object r,
                        Object g) {}
    }
    void method()
    {
        OpsSession opsSession = new OpsSession();
        OpsSession context = new OpsSession();
        context.initialize(opsSession.getUser(),
                                                opsSession.getSession(),
                                                opsSession.getEntity(),
                                                opsSession.getExchange(),
                                                opsSession.getProperties(),
                                                opsSession.getAccessRight(),
                                                opsSession.isAdministrator(),
                                                opsSession.getRootEntityOID(),
                                                opsSession.getGroupOID());
    }
}
