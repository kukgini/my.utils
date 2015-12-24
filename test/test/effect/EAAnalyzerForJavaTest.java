/*
 * $Id: $
 */
package test.effect;

import junit.framework.TestCase;
import effect.EAAnalyzerForJava;
import effect.jparser.JPFieldDeclaration;
import effect.jparser.JPLiteral;
import effect.jparser.JPType;
import effect.jparser.JPVariableDeclaratorId;

/**
 * $Id: $
 */
public class EAAnalyzerForJavaTest extends TestCase
{
    public void testConstantAnalyze()
    {
        JPFieldDeclaration node = new JPFieldDeclaration(0);
        node.jjtAddChild(new JPType(1), 0);
        node.jjtAddChild(new JPVariableDeclaratorId(2), 1);
        node.jjtAddChild(new JPLiteral(3), 2);
        
        EAAnalyzerForJava analyzer = new EAAnalyzerForJava();
        assertTrue(analyzer.isLiteralFieldDeclaration(node));
    }
}
