package dirdiff;

import java.io.*;
import java.security.*;
import java.util.*;
import org.apache.commons.io.*;
import org.apache.commons.lang.*;
import gnuzip.*;

import JLibDiff.diff;

public class FileInfo implements Comparable
{
    private static int READ_BUFFER_SIZE = 8 * 1024;
    private static MessageDigest digest;
    {
        try 
        {
            digest = createMessageDigest();
        }
        catch (NoSuchAlgorithmException e)
        {
            System.err.println("시스템이 MD5 체크섬 알고리즘을 지원하지 않습니다");
            System.exit(1);
        }
    }
    private File root;
    private File file;
    private String addRemoveFlag = " ";
    private String compareResult = "  ";
    
    private static final List ascTypes = new ArrayList();
    {
        ascTypes.add("xml");
        ascTypes.add("sh");
        ascTypes.add("php");
        ascTypes.add("txt");
        ascTypes.add("js");   
        ascTypes.add("css");   
        ascTypes.add("html");   
        ascTypes.add("htm");   
        ascTypes.add("jsp");
        ascTypes.add("jspf");
        ascTypes.add("js");   
        ascTypes.add("html");   
        ascTypes.add("java");   
        ascTypes.add("xfm");
        ascTypes.add("xfr");   
        ascTypes.add("pc");
        ascTypes.add("sql");   
    }
    private static final List zipTypes = new ArrayList();
    {
        zipTypes.add("zip");
        zipTypes.add("jar");
        zipTypes.add("war"); 
        zipTypes.add("ear"); 
    }
    private static final List clsTypes = new ArrayList();
    {
        clsTypes.add("class");
    }

    
    public FileInfo(File root, File file)
    {
        super();
        this.root = root;
        this.file = file;        
    }
    
    public int compareTo(Object comp)
    {
        if (comp == null)
            return 1;
        else
            return this.pathButFirst().compareTo(((FileInfo) comp).pathButFirst());
    }
    
    public boolean diffWith(FileInfo other)
    {
        boolean b = true;

        InputStream in1 = null;
        InputStream in2 = null;
        try 
        {
            in1 = new FileInputStream(file);
            in2 = new FileInputStream(other.file);
            if ("zip".equals(getType())) 
            {
            	b = diffBin(in1, in2);
            	if(b) {
                    b = diffZip(other); 
            	}
            }
            else 
            { 
                b = diffBetween(getType(), in1, in2); 
            }
        } 
        catch (Exception e)
        {
            System.err.println("[ERROR] " + e.getMessage());
            b = true;
        }
        finally
        {
            IOUtils.closeQuietly(in1);
            IOUtils.closeQuietly(in2);
        }
        System.err.print('.');

        setCompareResult(b);
        other.setCompareResult(b);
        
        return b;
    }

    public void setCompareResult(boolean result) 
    {
    	compareResult = (result ? "!=" : "==");
    }
    
    public void setAddRemoveFlag(String newAddRemoveFlag) 
    {
    	addRemoveFlag = newAddRemoveFlag;
    }
    
    private boolean diffBetween(String type, InputStream in1, InputStream in2) throws Exception
    {
        boolean b = false;
        if ("asc".equals(getType()))
        {       
            b = diffAsc(in1, in2);
        }       
        else    
        if ("cls".equals(getType()))
        {       
            b = diffBin(in1, in2, 8); //JVM 스팩에서 처음 4바이트는 magic number, 다음 4 바이트는 minor.major 버전이다
        }       
        else    
        {       
            b = diffBin(in1, in2);
        }       
        return b;
    }

    private String getType()
    {
        return getType(FilenameUtils.getExtension(file.getName()));
    }

    private String getType(String ext)
    {
             if (ascTypes.contains(ext)) { return "asc"; }
        else if (zipTypes.contains(ext)) { return "zip"; }
        else if (clsTypes.contains(ext)) { return "cls"; }
        else                             { return "bin"; }
    }

    private boolean diffZip(FileInfo other) throws Exception
    {
        boolean b = false;

        ZipFile zip1 = openZipFile();    
        ZipFile zip2 = other.openZipFile();
        try
        {
            Enumeration entries1 = zip1.entries();
            Enumeration entries2 = zip2.entries();
    
            while (entries1.hasMoreElements())
            {
                boolean bEntry = false;
                if (!entries2.hasMoreElements()) { b = true; break; }
                else
                {
                    ZipEntry entry1 = (ZipEntry)entries1.nextElement();
                    ZipEntry entry2 = (ZipEntry)entries2.nextElement();
        
                    if (!entry1.getName().equals(entry2.getName())) { b = true; break; }

                    InputStream in1 = null;
                    InputStream in2 = null;
                    try
                    {
                        in1 = zip1.getInputStream(entry1);
                        in2 = zip2.getInputStream(entry2);
                        String ext = FilenameUtils.getExtension(entry1.getName());
                        bEntry = diffBetween(ext, in1, in2);
                        b = b || bEntry;

                        /*\
                        System.err.println((bEntry ? "!=" : "==") + "\t" + 
                            "zip:" + getType(ext) +"\t"+ 
                            StringUtils.rightPad("... ", pathButFirst().length()) +"\t"+ 
                            entry1.getName());
                        */
                    }
                    finally
                    {
                        IOUtils.closeQuietly(in1);
                        IOUtils.closeQuietly(in2);
                    }
                }
            } 
            b = entries2.hasMoreElements();
        } 
        finally
        {
            closeZipFile(zip1);
            closeZipFile(zip2); 
        }
        return b;
    }

    public ZipFile openZipFile() throws Exception
    {
        return new ZipFile(file, ZipFile.OPEN_READ);
    }

    public void closeZipFile(ZipFile zip)
    {
        try { if (zip != null) zip.close(); } catch (Exception e) {}
    }

    private boolean diffBin(InputStream in1, InputStream in2) throws Exception
    {
        return diffBin(in1, in2, 0);
    }

    private boolean diffBin(InputStream in1, InputStream in2, long skip) throws Exception
    {
        return !checksum(in1, skip).equals(checksum(in2, skip));
    }
 
    private boolean diffAsc(InputStream in1, InputStream in2) throws Exception
    {
        boolean result = false;

        LineIterator i1 = IOUtils.lineIterator(in1, "euc-kr");
        LineIterator i2 = IOUtils.lineIterator(in2, "euc-kr");
        while (i1.hasNext())
        {
            if (!i2.hasNext()) { result = true; break; }

            String line1 = StringUtils.stripToEmpty(i1.nextLine());
            String line2 = StringUtils.stripToEmpty(i2.nextLine());

            if (!line1.equals(line2)) { result = true; break; }
        }

        return result;
    }
    
    public boolean equals(Object obj)
    {
        return (compareTo(obj) == 0) ? true : false;
    }
    
    private String pathButFirst()
    {
        return file.getAbsolutePath().substring(root.getAbsolutePath().length() + 1);
    }

    public String toString()
    {
        return pathButFirst();
    }
    
    public void printOn(PrintStream out)
    {
        out.println(addRemoveFlag + ',' + compareResult + ',' + getType() + ',' + pathButFirst());
    }
    
    public void printDiff(PrintStream out, String dir1, String dir2) throws IOException
    {
        if ("asc".equals(getType())) 
        {
            out.println("*********************************************");
            out.println("* DIFF:" + pathButFirst());
            out.println("*********************************************");
            diff d = new diff(dir1 + '/' + pathButFirst(), dir2 + '/' + pathButFirst());
            DiffVisitor v = new DiffVisitor(out);
            d.accept(v);
        }
    }
    
    private static MessageDigest createMessageDigest() throws NoSuchAlgorithmException
    {
        return MessageDigest.getInstance("MD5");
    }
    
    public String checksum(InputStream is, long skip) throws Exception 
    {
        digest.reset();

        String checksum = null;
        byte[] buf = new byte[READ_BUFFER_SIZE];
   
        if (skip > 0) {is.skip(skip);}

        DigestInputStream dis = new DigestInputStream(is, digest);
        while (dis.read(buf, 0, READ_BUFFER_SIZE) != -1) { ; }
        IOUtils.closeQuietly(dis);

        byte[] fileDigest = digest.digest();
        StringBuffer checksumSb = new StringBuffer();
        for (int i = 0; i < fileDigest.length; i++) {
            String hexStr = Integer.toHexString(0x00ff & fileDigest[i]);
            if (hexStr.length() < 2) {
                checksumSb.append("0");
            }
            checksumSb.append(hexStr);
        }
        checksum = checksumSb.toString();
        return checksum;
    }   
}
