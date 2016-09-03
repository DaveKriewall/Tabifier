public class ArrayIndexTest1
{

    public static void main(String[] args)
    {
        int   [] ia     = new int   [20];
        String[] sarray = new String[ 4];

        ia    [3 ] = 10;
        ia    [10] = 200   + ia[ 3];
        sarray[2]  = ""    + ia[10];
        sarray[0]  = "abc" + ia[ 3];
    }
}