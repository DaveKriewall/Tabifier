public class MethodDecl
{
   /**
     * Method Declarations.  Alignment of parameter modifier, type and name is
     * controlled by the corresponding options for declaration statements above.
     * First parameter of each line can be aligned independently of subsequent
     * parameters.
     *
     * If first parameters are aligned, these parameters align to the position
     * of the first parameter, which is either immediately to the right of the
     * open parenthesis or on a continuation line.  If first parameters are not
     * aligned, all other lines of parameters appear on a continuation line.
     * (Continuation lines are indented two tabstops from the current left margin.)
     *
     * Parameters on continuation lines (if any) are aligned separately from those
     * at the normal indent level.
     */

    abstract void lotsOfParamsMethod(int param1, float floatingPointParam,
                                    final String p, Object o,
                                    int param6, boolean isItTrue);
    abstract boolean getSetting();
    abstract void setSetting(boolean value);
    abstract Icon getIcon();
    abstract String getToolTipText();
    abstract int getShortcut();
}