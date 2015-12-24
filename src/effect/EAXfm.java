/*
 * $Id: $
 */
package effect;

import java.util.ArrayList;
import java.util.List;

public class EAXfm
{
    private static List list = new ArrayList();

    public static void add(String xfm)
    {
        if(!list.contains(xfm))
        {
            list.add(xfm);
        }
    }
    
    public static int count()
    {
        return list.size();
    }

}
