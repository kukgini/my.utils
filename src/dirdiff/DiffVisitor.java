package dirdiff;

import java.io.PrintStream;

import JLibDiff.HunkAdd;
import JLibDiff.HunkChange;
import JLibDiff.HunkDel;
import JLibDiff.HunkVisitor;

public class DiffVisitor extends HunkVisitor 
{
    private PrintStream out;
    
    public DiffVisitor(PrintStream out) 
    {
        super();
        this.out = out;
    }

    public void visitHunkAdd(HunkAdd hunk) 
    {
	    out.print(hunk.convert());
    }

    public void visitHunkChange(HunkChange hunk) 
    {
	    out.print(hunk.convert());
    }

    public void visitHunkDel(HunkDel hunk) 
    {
	    out.print(hunk.convert());
    }
    
}
