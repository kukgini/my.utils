package effect;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFilter;
import org.xml.sax.SAXException;

import effect.bparser.BPCompilationUnit;
import effect.bparser.BParser;
import effect.jparser.JPCompilationUnit;
import effect.jparser.JParser;

/*
 * $Id: $
 */

/**
 * $Id: $
 */
public class EAMain
{
    public static JParser jparser = null; 
    public static BParser bparser = null;
    
    public static void main(String args[]) throws Exception
    {
        try 
        {
            EAMain m = new EAMain();
            EAReport report = new EAReport();
            
            m.listOn("/aros13/PRIME/module/repository/SCRS/Web/WAS/scrs2/wbsv1/config", 
                    report, new String[] {"*.xml"}, 
                    EAMain.class.getMethod("scanConfig", new Class[] {
                    File.class, EAReport.class }));
            m.listOn("/aros13/PRIME/module/repository/SCRS/Web/WEB", 
                    report, new String[] {"*.xfm"}, 
                    EAMain.class.getMethod("scanXfm", new Class[] {
                    File.class, EAReport.class }));
            m.listOn("/aros13/PRIME/module/repository/SCRS/Web/WAS", 
                    report, new String[] {"*.java"}, 
                    EAMain.class.getMethod("scanJ", new Class[] {
                    File.class, EAReport.class }));
            m.listOn("/aros13/PRIME/module/repository/SCRS/Web/WAS", 
                    report, new String[] {"*.java"}, 
                    EAMain.class.getMethod("scanB", new Class[] {
                    File.class, EAReport.class }));
            m.listOn("/aros13/PRIME/module/repository/SCRS/Server", 
                    report, new String[] {"jrepository"}, 
                    EAMain.class.getMethod("scanJolt", new Class[] {
                    File.class, EAReport.class }));

            report.write();
        } 
        catch (Throwable e) 
        {
            e.printStackTrace();
        }
    }
    
    private static void close(PrintStream ps)
    {
        try { if (ps != null) ps.close(); } catch (Exception e) {}
    }
    
    private void listOn(String root, EAReport report, String[] filter, Method m) throws Exception
    {
        File f = new File(root);
        if (f.isFile())
            m.invoke(this, new Object[] {f, report});
        else
            list(f, report, filter, m);
    }
    
    private void list(File f, EAReport report, String[] filter, Method m) throws Exception
    {
        OrFileFilter filters = new OrFileFilter();
        filters.addFileFilter(DirectoryFileFilter.INSTANCE);
        filters.addFileFilter(new WildcardFilter(filter));
        
        File[] fs = f.listFiles((FileFilter)filters);
        for (int i = 0; i < fs.length; i++)
        {
            if (fs[i].isDirectory())
                list(fs[i], report, filter, m);
            else
                m.invoke(this, new Object[] {fs[i], report});
        }
    } 
    
    public void scanB(File f, EAReport report) throws FileNotFoundException
    {
        String name = getName(f);
        EAJava cls = EAJava.find(name);
        FileInputStream in = new FileInputStream(f);
        try
        {
            bparser = new BParser(in, "euc-kr");
            BPCompilationUnit n = bparser.parse();
            EAAnalyzerForBD v = new EAAnalyzerForBD();
            n.jjtAccept(v, cls);
            //n.dump("dump>");
        }
        catch (Exception e)
        {
            cls.setParseFailed() ;
            report.writeError("JOLT 호출 분석 오류, " + e.getMessage() + " : " + f.getAbsolutePath());
        }
        catch (Error e)
        {
            report.writeError("JOLT 호출 분석 오류, " + e.getMessage() + " : " + f.getAbsolutePath());
            throw e;            
        }
        finally
        {
            try { in.close(); } catch (IOException ioe) {}
        }
    }
    
    public void scanConfig(File f, EAReport report) throws ParserConfigurationException, IOException
    {
        try
        {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(f, new EAAnalyzerForJob(f));
        }
        catch (SAXException e)
        {
            report.writeError("Job Config 분석 오류 , " + e.getMessage() + " : " + f.getAbsolutePath());
        }
    }
    
    public void scanXfm(File f, EAReport report)  throws ParserConfigurationException, IOException 
    {
        EAAnalyzerForXfm h = new EAAnalyzerForXfm(f, report);
        try
        {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(f, h);
        }
        catch (SAXException e)
        {
            EAController.xfmParseError++;
            report.writeError("Xfm 분석 오류, " + e.getMessage() + " : " + h.getPath());
        }
    }
    
    /**
     * @param f
     * @throws FileNotFoundException
     */
    public void scanJ(File f, EAReport report) throws Exception
    {
        String name = getName(f);
        EAJava cls = EAJava.find(name);
        FileInputStream in = new FileInputStream(f);
        try
        {
            jparser = new JParser(in, "euc-kr");
            JPCompilationUnit n = jparser.parse();
            EAAnalyzerForJava v = new EAAnalyzerForJava();
            n.jjtAccept(v, cls);
            //n.dump("dump>");
        }
        catch (Exception e)
        {
            cls.setParseFailed() ;
            report.writeError("연관성 분석 오류, " + e.getMessage() + " : " + f.getAbsolutePath());
        }
        catch (Error e)
        {
            report.writeError("연관성 분석 오류, " + e.getMessage() + " : " + f.getAbsolutePath());
            throw e;            
        }
        finally
        {
            try { in.close(); } catch (IOException ioe) {}
        }
    }
    
    /**
     * @param f
     * @throws FileNotFoundException
     */
    public void scanJolt(File f, EAReport report) throws Exception
    {
        BufferedReader in = new BufferedReader(new FileReader(f));
        try
        {
            String line = null;
            while ((line = in.readLine()) != null)
            {
                if (line.startsWith("add SVC/"))
                {
                    EAJolt.add(line.substring("add SVC/".length(), line.indexOf(':')));
                }
            }
            
        }
        catch (Throwable th)
        {
            report.writeError("Jolt 리파지터리 분석 오류 " + th.getMessage() + " : " + f.getAbsolutePath());
        }
        finally
        {
            try { in.close(); } catch (IOException ioe) {}
        }
    }
    
    private String getName(File f)
    {
        return f.getName().substring(0, f.getName().lastIndexOf('.'));
    }
}
