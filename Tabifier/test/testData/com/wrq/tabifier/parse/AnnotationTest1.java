import org.jetbrains.annotations.NonNls;

public class AnnotationsClobbered {

    @NonNls
    private String annotatedField;
    @NonNls
    private String anotherAnnotatedField;

    public String getAnnotatedField() {
        return annotatedField;
    }

    public void setAnnotatedField(final String annotatedField) {
        this.annotatedField = annotatedField;
    }

    public String getAnotherAnnotatedField() {
        return anotherAnnotatedField;
    }

    public void setAnotherAnnotatedField(final String anotherAnnotatedField) {
        this.anotherAnnotatedField = anotherAnnotatedField;
    }

}
