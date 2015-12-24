/*
 * $Id: $
 */
package effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EAJolt
{
    private static List list = new ArrayList();
    /**
     * @param args
     */
    public static void add(String service)
    {
        if (!list.contains(service)) 
        {
            list.add(service);
        }
    }
    
    public static int count()
    {
        return list.size();
    }
    
    public static Iterator getIterator()
    {
        return list.iterator();
    }
}
