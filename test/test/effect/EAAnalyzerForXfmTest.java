/*
 * $Id: $
 */
package test.effect;

import java.io.File;

import junit.framework.TestCase;
import effect.EAAnalyzerForXfm;

/**
 * $Id: $
 */
public class EAAnalyzerForXfmTest extends TestCase
{
    public void testStribCommand()
    {
        EAAnalyzerForXfm h = new EAAnalyzerForXfm(new File("dummy"), null); //¿¬½À
        assertEquals("TargetCommand", h.stribCommand("  frontservlet?cmd=TargetCommand&parma...  "));
    }
}