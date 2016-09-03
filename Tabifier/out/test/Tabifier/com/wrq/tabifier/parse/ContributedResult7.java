public class ContributedTest7
{
    String method(String s)
    {
        if ( s == null )
        {
            return s;
        }

        StringBuffer sb    = new StringBuffer();
        int          index = 0;
        int          i     = 0;

        while ( ( index < s.length() ) && ( ( i = s.indexOf( "'", index ) ) != -1 ) )
        {
            sb.append( s.substring( index, i ) );
            sb.append( "''"                    );
            index = i + 1;
        }

        sb.append( s.substring( index ) );

        return sb.toString();
    }
}