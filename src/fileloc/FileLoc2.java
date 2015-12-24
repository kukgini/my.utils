package fileloc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * ������ ���� ���� ������ ��ⰳ���� Line ��
 * ������ ���� ������ �����ϴ� ����Ȯ���ں� ��ⰳ���� Line ��
 * �ŵ��(�ε���)�� �ŵ��(����)�� ����� ���� �����ϹǷ�, �и��ϱ� ���Ͽ� DB ������ �̿���.
 */
public class FileLoc2 {
    
    static Connection conn = null; 
    static final String[] TYPES = {
            "java"  
           ,"xfm"  
           ,"xfr"  
           ,"pc"  
           ,"jsp"  
           ,"xml"  
           ,"sql"  
           ,"html"  
           ,"xsl"  
           ,"cpp"  
           ,"htm"  
           ,"h"
           ,"dtd"
           ,"php" 
        };
    static final int[] TYPES_COUNT =new int[TYPES.length];
    static final long[] TYPES_STEP_COUNT =new long[TYPES.length];
    
    static final String[] COMMANTS = {
       "/*"
      ,"//"
      ,"<%-"
      ,"<!-"
      ,"*/"
    };
    
    static long subSum[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    static long pgmCnt[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    static long totalSum = 0;
    static long totalCnt = 0;
    static long subTotal = 0;
    static long subCount = 0;
   
    static long SYS01PgmCnt = 0;
    static long SYS01LineCnt = 0;    
    static long SYS02PgmCnt = 0;
    static long SYS02LineCnt = 0;
    static long SYS03PgmCnt = 0;
    static long SYS03LineCnt = 0;
    static long SYS05PgmCnt = 0;
    static long SYS05LineCnt = 0;
    static long SYS06PgmCnt = 0;
    static long SYS06LineCnt = 0;    
    static long SYS08PgmCnt = 0;
    static long SYS08LineCnt = 0;
    static long SYS09PgmCnt = 0;
    static long SYS09LineCnt = 0;
    static long SYS0APgmCnt = 0;
    static long SYS0ALineCnt = 0;
    static long SYS0GPgmCnt = 0;
    static long SYS0GLineCnt = 0;
    static long P0030PgmCnt = 0;
    static long P0030LineCnt = 0;
    static long P0032PgmCnt = 0;
    static long P0032LineCnt = 0;    
    static long P0033PgmCnt = 0;
    static long P0033LineCnt = 0;      
    
    static Vector vCditem22 = new Vector();
    static Vector vPath = new Vector();
    
    public static void listModules(File fi, int level) throws Exception 
    {
        File[] fo = fi.listFiles(new FileFilter() 
        {
            public boolean accept(File file)
            {
                boolean result = false;
                for (int i = 0; i < TYPES.length; i++ )
                {
                    if ((file.isDirectory() && contains(file, "SCRS")) ||
                            (file.isDirectory() && contains(file, "POS1")) ||
                            (file.isDirectory() && contains(file, "IRIS")) ||
                            (file.isDirectory() && contains(file, "ICIS")) ||   
                            (file.isDirectory() && contains(file, "RE1"))  ||
                            (file.isDirectory() && contains(file, "UHD"))  || 
                            (file.isDirectory() && contains(file, "EDMS"))  ||                      
                            (file.isFile() && file.getName().toLowerCase().endsWith('.' + TYPES[i])))                     
                    {
                        if (contains(file, "IRIS-G4C") 
                                || contains(file, "IRIS-LAW")
                                || contains(file, "IRIS-UHD") 
                                || contains(file, "IRIS-DIG"))
                        return false;
                        if (file.isFile() && ( contains(file, "SCRS") || contains(file, "POS1") || contains(file, "IRIS") || contains(file, "ICIS") || contains(file, "RE1") || contains(file, "UHD") || contains(file, "EDMS")) )                        
                        {   
                            if ( vPath.contains(file.getAbsolutePath())) 
                            {   
                                int index = vPath.indexOf(file.getAbsolutePath());
                                String cditem22 = (String)vCditem22.get(index);

                                if ("SYS01".equals(cditem22))
                                {   
                                    SYS01PgmCnt++;
                                    SYS01LineCnt += calculateLOC(file);
                                }                                
                                if ("SYS02".equals(cditem22))
                                {   
                                    SYS02PgmCnt++;
                                    SYS02LineCnt += calculateLOC(file);
                                }
                                if ("SYS03".equals(cditem22))
                                {   
                                    SYS03PgmCnt++;
                                    SYS03LineCnt += calculateLOC(file);
                                }
                                if ("SYS05".equals(cditem22))
                                {   
                                    SYS05PgmCnt++;
                                    SYS05LineCnt += calculateLOC(file);
                                }  
                                if ("SYS06".equals(cditem22))
                                {   
                                    SYS06PgmCnt++;
                                    SYS06LineCnt += calculateLOC(file);
                                } 
                                if ("SYS08".equals(cditem22))
                                {   
                                    SYS08PgmCnt++;
                                    SYS08LineCnt += calculateLOC(file);
                                } 
                                if ("SYS09".equals(cditem22))
                                {   
                                    SYS09PgmCnt++;
                                    SYS09LineCnt += calculateLOC(file);
                                } 
                                if ("SYS0A".equals(cditem22))
                                {   
                                    SYS0APgmCnt++;
                                    SYS0ALineCnt += calculateLOC(file);
                                }
                                if ("SYS0G".equals(cditem22))
                                {   
                                    SYS0GPgmCnt++;
                                    SYS0GLineCnt += calculateLOC(file);
                                }  
                                if ("P0030".equals(cditem22))
                                {   
                                    P0030PgmCnt++;
                                    P0030LineCnt += calculateLOC(file);
                                }
                                if ("P0032".equals(cditem22))
                                {   
                                    P0032PgmCnt++;
                                    P0032LineCnt += calculateLOC(file);
                                }
                                if ("P0033".equals(cditem22))
                                {   
                                    P0033PgmCnt++;
                                    P0033LineCnt += calculateLOC(file);
                                }                                
                                result = true;
                                
                            }   
                            else 
                                result = false;
                        }
                        else 
                        {   
                            result = true;
                            
                        }   
                    }   
                }
                return result;
            }
        });

        for ( int i = 0 ; i < fo.length ; i++ ) 
        {               
            if ( fo[i].isFile() ) 
            {
                    long loc = calculateLOC(fo[i]);
                    for (int j = 0; j < TYPES.length; j++)
                    {
                        if (fo[i].getName().toLowerCase().endsWith('.' + TYPES[j]))
                        {
                            TYPES_STEP_COUNT[j] += loc;
                            TYPES_COUNT[j]++;
                        }
                    }
                    
                    subSum[level] = loc;
                    pgmCnt[level] = 1;
                    if ( level > 0 ) 
                    {
                        subSum[level-1] += subSum[level];
                        pgmCnt[level-1] += pgmCnt[level];
                        totalSum += subSum[level];
                        subTotal += subSum[level];
                        totalCnt += pgmCnt[level];
                        subCount += pgmCnt[level];
                        subSum[level] = 0;
                        pgmCnt[level] = 0;
                    }
                    

                    
            } 
            else 
            {
                if (level == 1)
                {
                    System.out.print(fo[i].getName());
                    listModules(fo[i], level+1 );
                    System.out.print(": " + pgmCnt[level] + " Files");
                    System.out.println(": " + subSum[level] + " Lines");
                }
                else
                {
                    listModules(fo[i], level+1 );
                }
                
                if ( level > 0 ) 
                {
                    subSum[level-1] += subSum[level];
                    pgmCnt[level-1] += pgmCnt[level];
                    subSum[level] = 0;
                    pgmCnt[level] = 0;
                }
            }
        }
    }
    
    private static boolean contains(File f, String s)
    {
        return (f.getAbsolutePath().indexOf(s) > -1) ? true : false;
    }   
    
    
    private static long calculateLOC(File fi) 
    {
        long loc = 0;
        BufferedReader in = null;
        try 
        {
            in = new BufferedReader(new FileReader(fi));
            String str = null;
            while ( (str = in.readLine()) != null )
            {
                if ( ! isCommantLine(str) )
                    loc++;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {   
            try
            {
                if (in != null) in.close();
            }
            catch(Exception e){}
            
        }
        return loc;
    }
    
    private static boolean isCommantLine(String str)
    {
        boolean result = false;
        str = str.trim();
        for(int i = 0; i < COMMANTS.length; i++)
        {
            result = result || str.startsWith(COMMANTS[i]) || str.length() == 0;
        }
        return result;
    }
    
    public static void main(String[] args)
    {
        try
        {
            /**  �ý��� �̸�
            SYS01(�����Ż����)
            SYS02(�ε���)
            SYS03(��    ��) 
            SYS05(�����) 
            SYS06(����ڵ��)
            SYS08(���������)
            SYS09(�����)
            SYS0A(����ǥ�ؾ��(e-Form))
            SYS0G(���ڹ���)
            P0030(�ε������ͳݹ߱�)
            P0032(�����Ż�ܺ�)
            P0033(�������ͳݹ߱�)
            */             
            conn = getConnection("scrjqmso");
            getModuleList();
            System.out.println("Dir    Files     Lines");
            String MODULE_ROOT = args[0];
            File f1 = new File(MODULE_ROOT);
            listModules(f1, 1);
            System.out.println("---------SCRS---------");
            System.out.println("Dir    Files     Lines");
            System.out.println("�����Ż����               : "+ SYS01PgmCnt  + " : " + SYS01LineCnt);            
            System.out.println("�ε���                         : "+ SYS02PgmCnt  + " : " + SYS02LineCnt);
            System.out.println("��    ��                        : "+ SYS03PgmCnt  + " : " + SYS03LineCnt);
            System.out.println("�����                         : "+ SYS05PgmCnt  + " : " + SYS05LineCnt);
            System.out.println("����ڵ��                   : "+ SYS06PgmCnt  + " : " + SYS06LineCnt);
            System.out.println("���������                    : "+ SYS08PgmCnt  + " : " + SYS08LineCnt);
            System.out.println("�����                           : "+ SYS09PgmCnt  + " : " + SYS09LineCnt);
            System.out.println("����ǥ�ؾ��(e-Form) : "+ SYS0APgmCnt  + " : " + SYS0ALineCnt);
            System.out.println("���ڹ���                        : "+ SYS0GPgmCnt  + " : " + SYS0GLineCnt);
            System.out.println("�ε������ͳݹ߱�           : "+ P0030PgmCnt  + " : " + P0030LineCnt);
            System.out.println("�����Ż�ܺ�                 : "+ P0032PgmCnt  + " : " + P0032LineCnt);            
            System.out.println("�������ͳݹ߱�              : "+ P0033PgmCnt  + " : " + P0033LineCnt);              
            System.out.println("");
            System.out.println("type    count");
            for (int i = 0; i < TYPES.length; i++ ) 
            {   
                System.out.println(TYPES[i] + ":" + TYPES_COUNT[i]);
            }
            System.out.println("type    step_count");
            for (int i = 0; i < TYPES.length; i++ ) 
            {   
                System.out.println(TYPES[i] + ":" + TYPES_STEP_COUNT[i]);
            }           
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try 
            {               
                if (conn != null)
                conn.close(); 
            }
            catch(SQLException e){}
        }
    }
    
    private static String getModuleList()
    {
        String result = null;
        
        Statement stmt = null;
        try
        {   
            stmt = conn.createStatement();
        
            //DB �����Ͽ� SYS02, SYS03 �� ����� ����̸� �߰��Ѵ�.
            StringBuffer query = new StringBuffer();
            query.append("SELECT d1.cditem_22,'/aros13/PRIME/module/repository/' || c22.cdetc4 || '/' || c23.cdname || path||decode(substr(path,length(path)), '/', '', '/')||prt AS module ");
            query.append("  FROM tb_cmd01n d1                                                                                                   ");
            query.append("     , tb_cms03n c22                                                                                                  ");
            query.append("     , tb_cms03n c23                                                                                                  ");
            query.append(" WHERE c22.cdtype = '022'                                                                                             ");
            query.append("   AND c22.cditem = d1.cditem_22                                                                                      ");
            query.append("   AND c23.cdtype = '023'                                                                                             ");
            query.append("   AND c23.cditem = d1.cditem_23                                                                                      ");
            query.append("   AND d1.cditem_23 <> '00002'                                                                                        ");
            query.append("   AND d1.delete_flag='N'                                                                                        ");
            query.append("   AND d1.cditem_22 IN ('SYS01','SYS02','SYS03','SYS05','SYS06','SYS08','SYS09','SYS0A','SYS0G','P0030','P0032','P0033' )   ");
            
            query.append(" ORDER BY c22.cdetc4, c23.cdname, d1.path, d1.prt ");
                
            ResultSet rs = stmt.executeQuery(query.toString());                  
            
            while (rs.next())
            {   
                vCditem22.add(rs.getString("cditem_22"));
                vPath.add(rs.getString("module"));
            }   

            rs.close();
        }
        catch(Exception e)
        {   
            e.printStackTrace();
        }
        finally
        {
            try 
            { 
                if (stmt != null)
                    stmt.close(); 
            } 
            catch(SQLException sqle){ }
        }   
        return result;     
    }    
    
    public static Connection getRealDbConnection() throws SQLException
    {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@165.244.8.80:1621:dbcms1";
        String user = "scrjqmso";
        String password = "scrjqmso";
        return getConn(driver, url, user, password);
    }
    
    public static Connection getTestDbConnection() throws SQLException
    {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@165.244.8.80:1921:dbcms";
        String user = "scrjqmst";
        String password = "scrjqmst";
        return getConn(driver, url, user, password);
    }
    
    public static Connection getConn( String driver, String server, String username, String password ) throws SQLException
    {
        Connection conn = null;
        try
        {
            Class.forName( driver );
        }
        catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("JDBC Ŭ������ ã�� ���� java.lang.ClassNotFoundException �߻�");
        }
        
        try
        {
            conn = DriverManager.getConnection( server, username, password );
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw e;
        }
        
        return conn;
    }
    
    public static Connection getConnection( String account ) throws SQLException
    {
        if (account.equals("scrjqmso"))
        {
            return getRealDbConnection();
        }
        else if (account.equals("scrjqmst"))
        {
            return getTestDbConnection();   
        }
        
        else
            throw new SQLException("������ ã�� �� �����ϴ�.");
            
    }     
}
