package fileloc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;

/**
 * 지정한 폴더 하위 폴더의 모듈개수와 Line 수
 * 지정한 폴더 하위에 존재하는 파일확장자별 모듈개수와 Line 수
 */
public class FileLoc {
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
	static long SYS02PgmCnt = 0;
	static long SYS02LineCnt = 0;
	static long SYS03PgmCnt = 0;
	static long SYS03LineCnt = 0;
	
	public static void listModules(File fi, int level) throws Exception 
	{
		File[] fo = fi.listFiles(new FileFilter()
        {
            public boolean accept(File file)
            {
                boolean result = false;
                for (int i = 0; i < TYPES.length; i++ )
                {
                	if (file.isDirectory()  ||
                	(file.isFile() && file.getName().toLowerCase().endsWith('.' + TYPES[i])))                			
                	{	
                		result = true;
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
    
	
	private static long calculateLOC(File fi) throws Exception
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
		finally
		{
			if (in != null) in.close();
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
			System.out.println("Dir    Files     Lines");
			String MODULE_ROOT = args[0];
			File f1 = new File(MODULE_ROOT);
			listModules(f1, 1);
			System.out.println("-----------------------------");
			System.out.println("TYPE    module_count");
			for (int i = 0; i < TYPES.length; i++ ) 
			{	
				System.out.println(TYPES[i] + ":" + TYPES_COUNT[i]);
			}
			System.out.println("-----------------------------");
			System.out.println("TYPE    module_step_count");
			for (int i = 0; i < TYPES.length; i++ ) 
			{	
				System.out.println(TYPES[i] + ":" + TYPES_STEP_COUNT[i]);
			}					
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
