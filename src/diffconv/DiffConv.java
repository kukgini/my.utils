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
            
            skipLine(r, 31); //HTML 상단 건너띄기

            String temp = r.readLine();
            String fileName = temp.substring(63, temp.lastIndexOf(" 파일의 DIFF 정보"));
            
            skipLine(r, 4);  //변경라인수까지 건너띄기
            
            temp =  r.readLine();
            String add = temp.substring(12, temp.lastIndexOf(" Lines"));
            temp =  r.readLine();
            String edit = temp.substring(12, temp.lastIndexOf(" Lines"));
            temp =  r.readLine();
            String del = temp.substring(12, temp.lastIndexOf(" Lines"));
            
            w = new BufferedWriter(new FileWriter(diffFileName(f)));
            writeLine(w, "파일 : ", fileName);
            writeLine(w, "추가 : ", add);
            writeLine(w, "변경 : ", edit);
            writeLine(w, "삭제 : ", del);
            
            skipLine(r, 8); //원본파일 내용까지 건너띄기
            writeLine(w, "변경전 : ", decodeDiff(r.readLine()));

            skipLine(r, 9); //변경파일 내용까지 건너띄기
            writeLine(w, "변경후 : ", decodeDiff(r.readLine()));
        }
        finally
        {
            if (w != null) try {w.close();} catch (IOException ioe) {}
            if (r != null) try {r.close();} catch (IOException ioe) {}
        }
    }
    
    /**
     * 앞부분에 <pre> 테그가 포함되어 있다. (17 char)
     * <br> 테그는 화면에 뿌리기 위한거니까 diff 포멧에선 제거한다.
     * 100글자마다 <br> 테그가 하나씩 들어있다. 이걸 어떻게 고치지? --;
     * 100글자 셀때 128 이상의 글자는 2글자로 처리했다.
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
