public class DeclarationAlignmentTest13
{
    class NbDialogOperator
    {
        NbDialogOperator(String x)
        {
        }
    }
    class BundleClass
    {
        String getString(int bundleLocation, String title)
        {
           return title;
        }
    }

    void method()
    {
        BundleClass Bundle = new BundleClass();
        int bundleLocation = 3;

        String createNewProjectTitle = Bundle.getString (bundleLocation , "CTL_NewProjectTitle"); 
        NbDialogOperator createNewProjectDialog = new NbDialogOperator (createNewProjectTitle); 
    }
}
