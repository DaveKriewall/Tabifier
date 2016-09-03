import java.lang.reflect.Modifier;

public class ExpressionAlignmentTest3
{
    boolean plPublic;
    boolean plPrivate;
    boolean plProtected;
    boolean plPackage;
    boolean result;

    void method(int modifiers)
    {
            result =
                (plPublic    && Modifier.isPublic(modifiers)     ) ||
                (plPrivate   && Modifier.isPrivate  (modifiers)  ) ||
                (plProtected && Modifier.isProtected(modifiers)  ) ||
                (plPackage   && !(Modifier.isPublic(modifiers) ||
                                Modifier.isPrivate(modifiers) ||
                                Modifier.isProtected(modifiers)) );
    }
}
