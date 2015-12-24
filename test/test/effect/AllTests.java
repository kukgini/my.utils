/*
 * $Id: $
 */
package test.effect;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * $Id: $
 */
public class AllTests
{

    public static void main(String[] args)
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for test.effect");
        //$JUnit-BEGIN$
        suite.addTestSuite(EAAnalyzerForJavaTest.class);
        suite.addTestSuite(EAAnalyzerForXfmTest.class);
        //$JUnit-END$
        return suite;
    }
}
