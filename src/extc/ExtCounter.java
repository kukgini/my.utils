/*
 * $Id: $
 */
package extc;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * $Id: $
 */
public class ExtCounter
{
    private Map extmap = new HashMap();
    
    private static void printUsage()
    {
        System.out.println("프로그램 사용법");
        System.out.println("java -jar extc.jar <dir>");
    }

    public static void main(String[] argv)
    {
        if (argv.length < 1)
        {
            printUsage();
            return;
        }
        try 
        {
            ExtCounter counter = new ExtCounter();
            counter.countOn(argv[0]);
            counter.reportOn(System.out);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }    
    
    public void countOn(String root)
    {
        count(new File(root));
    }
    
    public void reportOn(PrintStream out)
    {
        Iterator keys = extmap.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String)keys.next();
            out.println(key + "\t" + extmap.get(key));
        }
    }
    
    private void count(File f)
    {
        File[] fs = f.listFiles();
        for (int i = 0; i < fs.length; i++)
        {
            if (fs[i].isDirectory())
            {
                if (".svn".equals(fs[i].getName())) continue;
                if ("CVS".equals(fs[i].getName())) continue;

                count(fs[i]);
            }
            else
                increaseCountOn(extractExt(fs[i]));
        }
    }
    
    private void increaseCountOn(String ext)
    {
        Integer i = (Integer)extmap.get(ext);
        if (i == null)
            extmap.put(ext, new Integer(1));
        else
            extmap.put(ext, new Integer(i.intValue() + 1));
    }
    
    private String extractExt(File f)
    {
        int index = f.getName().lastIndexOf('.');
        if (index < 0) 
            return f.getName();
        else
            return f.getName().substring(index);
    }
}
