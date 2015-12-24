/*
 * $Id: $
 */
package effect;

import java.io.File;
import java.io.PrintStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * $Id: $
 */
public class EAAnalyzerForXfm  extends DefaultHandler
{
    private File f;
    private EAReport report;
    
    public EAAnalyzerForXfm(File f, EAReport report)
    {
        super();
        this.f = f;
        this.report = report;
        EAXfm.add(getPath());
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if ("submitInfo".equals(qName)) {
            String cmd = stribCommand(attributes.getValue("action"));
            EAController job = EAController.find(cmd);
            if (job == null)
                report.writeError("Job 찾기 실패 [" + cmd + "] " + getPath());
            else
                job.addXfm(getPath());
        }
    }
    
    public String stribCommand(String action)
    {
        String result = action.trim();
        int startIndex = result.indexOf("cmd=") + 4;
        result = result.substring(startIndex);

        int endIndex = result.indexOf('&');
        if (endIndex > 0)
            result = result.substring(0, endIndex);
        return result;
    }
    
    public String getPath()
    {
        return f.getAbsolutePath().substring(f.getAbsolutePath().indexOf("xfm/") + 4);
    }
}
