public class TabifierBug {

    private void bug() {
        if (false) {
            return;
        } else if (false) {
            return;
        } else {
            return;
        }
    }
}