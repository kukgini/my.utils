package listdir;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;

public class ListDir
{
    private static boolean DEBUG = false;
    private static int MAX_DEPTH = -1;
    
    private File root = null;
    private List list = new ArrayList();
    private boolean[] endOfTree = new boolean[256];
    {
        for(int i = 0; i < endOfTree.length; i++) { endOfTree[i] = true; }
    }

    private int depth = 0; 

    private static void printUsage()
    {
        System.out.println("프로그램 사용법");
        System.out.println("java -jar listdir.jar <dir> [depth]");
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
            if (argv.length > 1)
            {
                MAX_DEPTH = NumberUtils.toInt(argv[1]);    
            }
            ListDir counter = new ListDir();
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
        System.out.println("DIR : " + root.getAbsolutePath());
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
        try 
        {
            depth++; 
            if ((MAX_DEPTH > 0) && (depth > MAX_DEPTH)) { return; }
            
            IOFileFilter filters = DirectoryFileFilter.INSTANCE;
            filters = FileFilterUtils.makeSVNAware(filters);
            filters = FileFilterUtils.makeCVSAware(filters);
    
            File[] fs = f.listFiles((FileFilter)filters);
            if (fs == null) { return; }
            Arrays.sort(fs);

            for (int i = 0; i < fs.length; i++)
            {
                if (i < (fs.length - 1)) 
                {
                    endOfTree[depth] = false;            
                    list.add(treePrefix() + fs[i].getName());
                }
                else
                {
                    endOfTree[depth] = true;            
                    list.add(treePrefix() + fs[i].getName());
                }
                count(fs[i]);
            }
        }
        catch (Exception e)
        {
            System.err.println("[ERROR]" + f.getAbsolutePath() + ':' + e.getMessage() + '(' + e.getClass().getName() + ')');
            e.printStackTrace();
        }         
        finally
        {
            depth--; 
        }
    }

    private String treePrefix()
    {
        if (DEBUG)
        {
            String s = "(" + depth + ")";
    
            for (int i = 0; i < depth; i++)
            {
                if (endOfTree[i]) { s = s + i + "....."; }
                else              { s = s + i + "│   "; }
            }
    
            if (endOfTree[depth]) { s = s + depth + "└─ "; }
            else                  { s = s + depth + "├─ "; }
    
            return s + ArrayUtils.toString(endOfTree).substring(0, 40) + "...";
        }
        else
        {
            String s = "";
    
            for (int i = 0; i < depth; i++)
            {
                if (endOfTree[i]) { s = s + "     "; }
                else              { s = s + "│   "; }
            }
    
            if (endOfTree[depth]) { s = s + "└─ "; }
            else                  { s = s + "├─ "; }
    
            return s;
        }
    }
}
