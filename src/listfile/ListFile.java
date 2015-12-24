/*
 * $Id: $
 */
package listfile;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.*;

/**
 * $Id: $
 */
public class ListFile
{
    private File root = null;
    private List list = new ArrayList();
    
    private static void printUsage()
    {
        System.out.println("프로그램 사용법");
        System.out.println("java -jar listfile.jar <dir>");
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
            ListFile counter = new ListFile();
            counter.countOn(argv[0]);
            counter.reportOn(System.out);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }    
    
    public void countOn(String path)
    {
        root = new File(path);
        count(root);
    }
    
    public void reportOn(PrintStream out)
    {
        Iterator keys = list.iterator();
        while (keys.hasNext())
        {
            out.println(keys.next());
        }
    }
    
    private void count(File f)
    {
        File[] fs = f.listFiles();
        for (int i = 0; i < fs.length; i++)
        {
            if (fs[i].isDirectory())
                count(fs[i]);
            else {
                String name = fs[i].getName();
                String path = fs[i].getParentFile().getAbsolutePath();
                if (path.length() > root.getAbsolutePath().length()) {
                    path = path.substring(root.getAbsolutePath().length() + 1);
                }
                list.add(path + '\t' + name);
            }
        }
    }
}
