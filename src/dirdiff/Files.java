/*
 * $Id: $
 */
package dirdiff;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 * $Id: $
 */
public class Files extends LinkedList
{
    public Files()
    {
        super();
    }
    
    public static Files listAll(String root) throws Exception
    {
        File f = new File(root);
        if (!f.exists())
            throw new Exception("존재하지 않은 디렉터리:" + root);
        Files instance = new Files();
        instance.list(f, f);
        return instance;
    }
    
    public void list(File root, File f)
    {
        if (".svn".equals(f.getName())) return;
        if (".subversion".equals(f.getName())) return;

        File[] files = f.listFiles();
        if (files == null)
        {
            System.err.println("읽기오류:" + f.getPath());
            return;
        }

        for (int i = 0; i < files.length; ++i)
        {
            if (files[i].isDirectory())
                list(root, files[i]);
            else
                add(new FileInfo(root, files[i]));
        }
    }
    
    public FileInfo getItem(int index)
    {
        return (FileInfo)get(index);
    }
    
    public FileInfo cut(int index)
    {
        FileInfo file = (FileInfo)get(index);
        remove(index);
        return file;
    }
    
    public void printHeader(PrintStream out)
    {
    	out.println("ST,CP,TYPE,PATH");
    }
    
    public void print(PrintStream out)
    {
    	printHeader(out);
        if (size() == 0) return;
        for(int i = 0; i < size(); i++)
        {
            getItem(i).printOn(out);
        }
    }    
    
    public void printDiff(String dir1, String dir2) throws IOException
    {
        if (size() == 0) return;
        for(int i = 0; i < size(); i++) {
            getItem(i).printDiff(System.out, dir1, dir2);
        }
    }
}
