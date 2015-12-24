package JLibDiff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

/**
  * The <code>diff3</code> class compares three files or three BufferedReaders.
  * after comparison, the vector will represente a list of Hunk3 
  * corresponding with blocks of difference.
  * <p>
  * to generate Hunk3, one can instanciate as follows the class <code>diff3</code>:
  * <p><blockquote><pre>
  *     diff3 d = new diff3(file1,file2,file3);
  * </pre></blockquote><p>
  * which is equivalent to:
  * <p><blockquote><pre>
  *     diff3 d = new diff3();
  *     d.diffFile(file1,file2,file3);
  * </pre></blockquote><p>
  * To compaire three BufferedReader we have to instanciate as follows:
  * <p><blockquote><pre>
  *     diff3 d = new diff3();
  *     d.diffBuffer(BufferedReader1,BufferedReader2,BufferedReader3);
  * </pre></blockquote><p>
  * The class <code>diff3</code> includes methods for examining, printing
  * or saveing  blocks of difference:(Hunk3).
  * Here are some more examples of how <code>diff3</code> can be used:
  * <p><blockquote><pre>
  *	diff3 d = new diff3(file1,file2,file3);
  *	d.print();
  *     d.save("diff.txt");
  * </pre></blockquote><p>
  * Example using BufferedReader and ED_format:
  * <p><blockquote><pre> 
  *	BufferedReader in0=new BufferedReader(new FileReader(file1));
  *	BufferedReader in1=new BufferedReader(new FileReader(file2));
  *     BufferedReader in2=new BufferedReader(new FileReader(file3)); 
  *	diff3 d = new diff3();
  *	d.diffBuffer(in0,in1,in2);
  *	d.print_ED();
  *	d.save_ED("diff.txt");
  * </pre></blockquote><p>
  *
  *
  *
  * @see diff.diff3#getHunk3()
  * @see diff.diff3#hunk3At()
  * @see diff.diff3#numberOfHunk3()
  * @see diff.diff3#print()
  * @see diff.diff3#save()
  */
public class diff3{

  Vector v=new Vector();
 
  /**
    *  Allocates a new <code>diff3</code> containing no Hunks.
    */ 
  public diff3(){}

  /**
    * Allocates a new <code>diff3</code> which contains Hunk3 corresponding 
    * to the differences between the three files passed in arguments.
    *
    * @param s0  first file.
    * @param s1  second file.
    * @param s2  therd file.
    */
  public diff3(String s0,String s1,String s2)throws IOException{
    diffFile(s0,s1,s2);
  }
 
  /**
    * Compaires three files and updates the vector of Hunk3.
    *
    * @param s0  first file.
    * @param s1  second file.
    * @param s2  therd file.
    */ 
  public void diffFile(String s0,String s1,String s2)throws IOException{

    BufferedReader in0=new BufferedReader(new FileReader(s0));
    BufferedReader in1=new BufferedReader(new FileReader(s1));
    BufferedReader in2=new BufferedReader(new FileReader(s2));
    // BufferedReader in22=new BufferedReader(new FileReader(s2));
    diffBuffer(in0,in1,in2);
    in0.close();
    in1.close();
    in2.close();
  }

  /**
    * Compaires three BufferedReaders and updates the vector of Hunk3.
    *
    * @param in0  first Buffer.
    * @param in1  second Buffer.
    * @param in2  therd Buffer.
    */
  public void diffBuffer(BufferedReader in0,BufferedReader in1,BufferedReader in2)throws IOException{
    
    diff d0=new diff();
    diff d1=new diff();
    //BufferedReader in22=new BufferedReader(new BufferedReader(in2));
    //in22=in2;
    // if(in2.markSupported())
    //System.out.println("-----ssssss--------");
    //in2.mark(0);
    d0.diffBuffer(in0,in2);
    //in2.reset();
    d1.diffBuffer(in1,in2);
d1.print();
d0.print();
    Vector v0,v1;
    v0=d0.getHunk();
    v1=d1.getHunk();
    v=getHunk3(v0,v1);
  }

  private Vector getHunk3(Vector v0,Vector v1){

    Hunk using[]=new Hunk[2];
    Hunk last_using[]=new Hunk[2];
    Hunk current[]=new Hunk[2];

    int high_water_mark;
    int high_water_thread,
        base_water_thread,
        other_thread;

    Hunk high_water_diff,
         other_diff;

    Hunk3 result;
    Hunk3 tmpblock;
    Hunk3 last_diff3;
    Hunk3 zero_diff3=null;

    Vector v=new Vector();
    
    result=null;
   
    last_diff3=zero_diff3;

    if(v0.size()!=0)
      current[0]=(Hunk)v0.elementAt(0);
    else
      current[0]=null;
    if(v1.size()!=0)
      current[1]=(Hunk)v1.elementAt(0);
    else
      current[1]=null;

    while(current[0]!=null||current[1]!=null)
      {
	using[0]=null;
	using[1]=null;
	last_using[0]=null;
	last_using[1]=null;

	if(current[0]==null)
	  base_water_thread=1;
	else
	  if(current[1]==null)
	    base_water_thread=0;
	  else
	    if(current[0].lowLine(1) > current[1].lowLine(1))
	      base_water_thread=1;
	    else
	      base_water_thread=0;
	high_water_thread=base_water_thread;
	high_water_diff=current[high_water_thread];
	
	high_water_mark=high_water_diff.highLine(1);
	last_using[high_water_thread]=high_water_diff;
	using[high_water_thread]=high_water_diff;
	current[high_water_thread]=high_water_diff.next;
	(last_using[high_water_thread]).next =null;
	other_thread=high_water_thread ^ 0x1 ;
	other_diff=current[other_thread];
	
	while(other_diff!=null 
	      && other_diff.lowLine(1) <= high_water_mark + 1)
	  {
	    if(using[other_thread]!=null)
	      (last_using[other_thread]).next =other_diff;
	    else
	      using[other_thread]=other_diff;
	    last_using[other_thread]=other_diff;
	    current[other_thread]=(current[other_thread]).next;
	    other_diff.next=null;
	    if(high_water_mark<other_diff.highLine(1))
	      {
		high_water_thread ^=1;
		high_water_diff=other_diff;
		high_water_mark=other_diff.highLine(1);
	      }
	    other_thread=high_water_thread ^ 0x1;
	    other_diff=current[other_thread];
	  }
	tmpblock=using_to_diff3_block(using, last_using,
			      base_water_thread,high_water_thread,last_diff3);
	if(tmpblock!=null)
	  {
	    v.addElement(tmpblock);
	    last_diff3=tmpblock;
	  }
      }
    return v;
  }

  private Hunk3 using_to_diff3_block(Hunk using[],Hunk last_using[],
			      int low_thread,int high_thread,Hunk3 last_diff3){
    
    int d,i;
    int low[]=new int[2];
    int high[]=new int[2];
    Hunk3 result=new Hunk3();
    
    int lowc=using[low_thread].lowLine(1);
    int highc=last_using[high_thread].highLine(1);

    for(d=0;d<2;d++)
      if(using[d]!=null)
	{
	  low[d]=lowc-using[d].lowLine(1)+using[d].lowLine(0);
	  high[d]=highc-last_using[d].highLine(1)+last_using[d].highLine(0);
	}
      else
	{
	  if(last_diff3==null)
	    {
	      low[d]=lowc;
	      high[d]=highc;
	    }
	  else
	    {
	      low[d]=lowc-last_diff3.highLine(2)+last_diff3.highLine(d);
	      high[d]=highc-last_diff3.highLine(2)+last_diff3.highLine(d);
	    }
	}
    result.setRange(low[0],high[0],low[1],high[1],lowc,highc);
    for(d=0;d<2;d++)
      {
	
      }
    for(d=0;d<2;d++)
      {
	Hunk u=using[d];
	int lo=low[d];
	int hi=high[d];
	for(i=0;i+lo<((u!=null) ? u.lowLine(1) : hi+1) ;i++)
	  {
	    
	  }
	//for()
	  {
	    
	  }

      }
    if(using[0]==null)
      result.diff=DiffType.DIFF_2ND;
    else
      if(using[1]==null)
	result.diff=DiffType.DIFF_1ST;
      else
	{
	  int nl0=result.numLines(0);
	  int nl1=result.numLines(1);
	  if(nl0 != nl1 )/////
	    result.diff=DiffType.DIFF_ALL;
	  else
	    result.diff=DiffType.DIFF_3RD;
	}
    return result;
  }

  /**
    * Returns a vector containing Hunk3.
    */
  public Vector getHunk3(){
    return v;
  }

  /**
    * Return the hunk3 at the specified index.
    *
    * @param i  index of the Hunk that will be returned.
    */
  public Hunk3 hunk3At(int i){
    return (Hunk3)v.elementAt(i);
  }

  /**
    * Returns the number of hunk3 in the vector.
    */
  public int numberOfHunk3(){
    return v.size();
  }

  /**
    * Print Hunk3 with normal format.
    */
  public void print(){
    for (Enumeration e = v.elements() ; e.hasMoreElements() ;)
      System.out.print(((Hunk3)e.nextElement()).convert()); 
  }

  /**
    * Save Hunk3 with normal format at the specified file.
    *
    * @param file   file in which Hunks will be saved.
    */
  public void save(String file)throws IOException{
    PrintWriter out
      = new PrintWriter(new BufferedWriter(new FileWriter(file))); 
    for (Enumeration e = v.elements() ; e.hasMoreElements() ;)
      out.write(((Hunk3)e.nextElement()).convert()); 
    out.flush();
    out.close();
  }

}
