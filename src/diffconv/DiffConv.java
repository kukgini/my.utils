/*
 * $Id: $
 */
package diffconv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFilter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * $Id: $
 */
public class DiffConv
{
    public static void main(String[] argv)
    {
        try 
        {
            DiffConv m = new DiffConv();
            m.listOn("../diff", new String[] {"*.html"}, 
                    m.getClass().getMethod("execute", new Class[] {
                    File.class}));
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }    
    
    private void listOn(String root, String[] filter, Method m) throws Exception
    {
        File f = new File(root);
        if (f.isFile())
            m.invoke(this, new Object[] {f});
        else
            list(f, filter, m);
    }
    
    private void list(File f, String[] filter, Method m) throws Exception
    {
        OrFileFilter filters = new OrFileFilter();
        filters.addFileFilter(DirectoryFileFilter.INSTANCE);
        filters.addFileFilter(new WildcardFilter(filter));
        
        File[] fs = f.listFiles((FileFilter)filters);
        for (int i = 0; i < fs.length; i++)
        {
            if (fs[i].isDirectory())
                list(fs[i], filter, m);
            else
                m.invoke(this, new Object[] {fs[i]});
        }
    } 
    
    public void execute(File f) throws Exception
    {
        //System.out.println(f.getAbsolutePath());
        BufferedReader r = null;
        BufferedWriter w = null;
        try
        {
            r = new BufferedReader(new FileReader(f));
            
            skipLine(r, 31); //HTML ��� �ǳʶ��

            String temp = r.readLine();
            String fileName = temp.substring(63, temp.lastIndexOf(" ������ DIFF ����"));
            
            skipLine(r, 4);  //������μ����� �ǳʶ��
            
            temp =  r.readLine();
            String add = temp.substring(12, temp.lastIndexOf(" Lines"));
            temp =  r.readLine();
            String edit = temp.substring(12, temp.lastIndexOf(" Lines"));
            temp =  r.readLine();
            String del = temp.substring(12, temp.lastIndexOf(" Lines"));
            
            w = new BufferedWriter(new FileWriter(diffFileName(f)));
            writeLine(w, "���� : ", fileName);
            writeLine(w, "�߰� : ", add);
            writeLine(w, "���� : ", edit);
            writeLine(w, "���� : ", del);
            
            skipLine(r, 8); //�������� ������� �ǳʶ��
            writeLine(w, "������ : ", decodeDiff(r.readLine()));

            skipLine(r, 9); //�������� ������� �ǳʶ��
            writeLine(w, "������ : ", decodeDiff(r.readLine()));
        }
        finally
        {
            if (w != null) try {w.close();} catch (IOException ioe) {}
            if (r != null) try {r.close();} catch (IOException ioe) {}
        }
    }
    
    /**
     * �պκп� <pre> �ױװ� ���ԵǾ� �ִ�. (17 char)
     * <br> �ױ״� ȭ�鿡 �Ѹ��� ���ѰŴϱ� diff ���信�� �����Ѵ�.
     * 100���ڸ��� <br> �ױװ� �ϳ��� ����ִ�. �̰� ��� ��ġ��? --;
     * 100���� ���� 128 �̻��� ���ڴ� 2���ڷ� ó���ߴ�.
     * 
     * @param str
     * @return
     */
    private String decodeDiff(String str)
    {
        String[] sa = str.substring(17).split("<br>");
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < sa.length; i++)
        {
            if (sa[i].matches("^[0-9]+ :.*"))
                sb.append("\n\t" + StringEscapeUtils.unescapeHtml(sa[i]));
            else
                sb.append(StringEscapeUtils.unescapeHtml(sa[i]));
        }
        return sb.toString();
    }
    
    private void writeLine(BufferedWriter w, String prefix, String contents) throws Exception
    {
        w.write(prefix + contents + "\n");
    }
    
    private void skipLine(BufferedReader r, int line) throws Exception
    {
        for(int i = 1; i < line; i++ )
        {
            r.readLine();
        }
    }
    
    private String diffFileName(File f)
    {
        return f.getParent() + "/" + 
            FilenameUtils.removeExtension(f.getName()) + ".diff";
    }
}
