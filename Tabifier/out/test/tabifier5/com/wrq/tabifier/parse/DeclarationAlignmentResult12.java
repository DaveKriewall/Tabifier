public class DeclarationAlignmentTest12
{
    class BooleanSetting
    {
        boolean value;
    }

    class TabOrSpaceSetting extends BooleanSetting
    {
        int morestuff;
    }

    abstract BooleanSetting find(String s);
    
    final String ALIGN_ASSIGNMENT_OPERATORS         = "align_assignment_operators";
    final String ALGIN_ASSIGNMENT_OPERATORS_SPACING = "align_assignment_operators_spacing";

    void method()
    {
        BooleanSetting    align_assignment_operators_mbr;
        TabOrSpaceSetting align_assignment_operators_spacing_mbr;
        
        align_assignment_operators_mbr         = (BooleanSetting   ) find(ALIGN_ASSIGNMENT_OPERATORS        );
        align_assignment_operators_spacing_mbr = (TabOrSpaceSetting) find(ALIGN_ASSIGNMENT_OPERATORS_SPACING);
    }
}
