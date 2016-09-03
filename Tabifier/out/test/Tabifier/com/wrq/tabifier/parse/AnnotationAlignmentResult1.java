import com.jniwrapper.Char;

public class AnnotationAlignmentTest1
{
    @Ann1             public    int     i;
    @Annotation2      private   String  s2;
    @A3               protected Boolean b;
    @A3()                       Char    c;
    @A3("test value")           Char    c2;

    @A3 @Ann1 public          int i2;
    @Annotation2 @Ann1 public int i3;

    @A3("abc")
    @Ann1 public int i4;
    @A3("abcd") @Ann1
    @Annotation2 public int i5;
}

@interface Ann1
{
}

@interface Annotation2
{
}

@interface A3
{
    String value() default "";
}
