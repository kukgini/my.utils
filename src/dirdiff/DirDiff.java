/*
 * $Id: $
 */
package dirdiff;



/**
 * �ΰ��� ���� �ٸ� ���͸��� ���ϵ��� ���ϴ� ���α׷�.
 * �������͸��� ���� ���̸� ���ϰų�, 
 * �������͸��� � ���̸� ���ϴµ� ����ϸ� ������.
 * 
 * �� ��Ʈ�� ���̰� ���� ���� �ٸ��ٰ� �Ǵ��ϰ� �Ǿ��ִ�.
 * $Id: $
 */
public class DirDiff
{
    private static void printUsage()
    {
        System.out.println("���α׷� ����");
        System.out.println("java -jar dirdiff.jar <dir1> <dir2>");
    }
    
    public static void main(String[] argv)
    {
        try 
        {
            if (argv.length < 2)
            {
                printUsage();
                return;
            }
            Files dir1 = Files.listAll(argv[0]);
            Files dir2 = Files.listAll(argv[1]);
            if (dir1.size() == 0) 
            {
            	for (int i = 0; i < dir2.size(); i++) 
            	{
            		FileInfo f = dir2.getItem(i);
            		f.setAddRemoveFlag("+");
            		dir1.add(f);
            	}
            }
            else
            {
	            for(int i = 0; i < dir1.size(); i++)
	            {
	            	FileInfo f1 = dir1.getItem(i);
	            	if (dir2.size() > 0) 
	            	{
	            	    FileInfo f2 = dir2.getItem(0);

	                    int compareResult = f1.compareTo(f2);
	                    if (compareResult == 0) 
	                    {
	                    	f1.diffWith(f2);
	                    	dir2.remove(0);
	                    }
	                    else if(compareResult < 0) 
	                    {
	                    	f1.setAddRemoveFlag("-");
	                    }
	                    else if(compareResult > 0) {
	                    	f2.setAddRemoveFlag("+");
	                    	dir1.add(i, f2);
	                    	dir2.remove(0);
	                    }
                    }
	            	else
	            	{
	            		f1.setAddRemoveFlag("-");
	            	}
	            }
            }
            if (dir2.size() > 0) 
            {
            	for (int i = 0; i < dir2.size(); i++) 
            	{
            		FileInfo f = dir2.getItem(i);
            		f.setAddRemoveFlag("+");
            		dir1.add(f);
            	}
            }
            dir1.print(System.out);
            //dir1.printDiff(dir1, dir2)
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }
    
//    public static void main(String[] argv)
//    {
//        try 
//        {
//            if (argv.length < 2)
//            {
//                printUsage();
//                return;
//            }
//            Files dir1 = Files.listAll(argv[0]);
//            Files dir2 = Files.listAll(argv[1]);
//            Files changed  = new Files();
//            for(int i = dir1.size() - 1; i > -1; i--)
//            {//������Ʈ�� �����ϱ� ������ �Ųٷ� ������ ����.
//                if (dir2.contains(dir1.getItem(i)))
//                {
//                    FileInfo f1 = dir1.cut(i);
//                    FileInfo f2 = dir2.cut(dir2.indexOf(f1));
//                    if (f1.diffWith(f2))
//                        changed.add(f1);
//                }
//            }
//            dir1.print(argv[0] + " ���� �ִ� ���");
//            dir2.print(argv[1] + " ���� �ִ� ���");
//            changed.print("������ �ٸ� ���");
//            changed.printDiff(argv[0], argv[1]);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace(System.err);
//        }
//    }
}
