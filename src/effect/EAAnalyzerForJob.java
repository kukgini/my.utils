/*
 * $Id: $
 */
package effect;

import java.io.File;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * $Id: $
 */
public class EAAnalyzerForJob extends DefaultHandler
{
    private String file = null;
    private String status = null;
    private EAController job = null;
    private int flag = 0;
    
    public EAAnalyzerForJob(File f) 
    {
        super();
        file = f.getName();
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        status = qName;
        if ("job".equals(qName)) {
            job = new EAController(file);
        }
    }
        
    public void characters(char[] ch, int start, int len) 
    {
        if ("job-name".equals(status)) {
            job.name = new String(ch, start, len);
            
            //디버그정보. XML 파서 오류인거 같은데 원인이 뭔지 잘 모르겠음. len 이 잘못 리포트된다.
//            if (file.endsWith("WCOMJobRA.xml"))
//            {
//                flag++;
//                if (flag == 15) {
//                    System.out.println(new String(ch));
//                }
//                System.out.println(start + "\t" + len + "\t" + job.name);
//            }
        }
        else
        if ("job-class".equals(status)) {
            job.type = new String(ch, start, len);
        }
        status = null;
     }
    
    public void endElement (String uri, String localName, String qName) throws SAXException
    {
        if ("job".equals(qName)) 
        {
            EAController.add(job);
            job = null;
        }
        status = null;
    }    
}
