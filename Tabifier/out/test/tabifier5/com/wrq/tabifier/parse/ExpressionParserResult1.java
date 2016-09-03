public class TabifierBug {
    private String thisWillNot;
    private Long   alignCorrectlyAndThrowANullPointerInTheIDE;

    private String method1() {
        return "a" + "b" + "c";
    }

    private String method2() {
        return "a" + "b";
    }
}