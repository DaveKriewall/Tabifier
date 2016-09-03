import java.util.ArrayList;

public class MethodCallAlignmentTest6
{
    ArrayList children = new ArrayList();

    ArrayList getChildren() 
    { 
        return children;
    }

    void addContent(Object o)
    {
        children.add(o);
    }

    void method()
    {
        MethodCallAlignmentTest6 element = new MethodCallAlignmentTest6();
        MethodCallAlignmentTest6 element2 = new MethodCallAlignmentTest6();

        String our_element = "";

        element.getChildren().clear();
        element.addContent(our_element);

        element.addContent("abc");
        element2.addContent(our_element);
    }
}
