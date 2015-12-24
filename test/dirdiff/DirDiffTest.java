/*
 * $Id: $
 */
package dirdiff;

import java.io.File;

import junit.framework.TestCase;

/**
 * $Id: $
 */
public class DirDiffTest extends TestCase
{
    public void testDirDiff()
    {
        DirDiff.main(new String[]{"testdata/dirdiff/a","testdata/dirdiff/b"});
    }
}
