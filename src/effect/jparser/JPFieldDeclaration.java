/* Generated By:JJTree: Do not edit this line. ASTFieldDeclaration.java */

package effect.jparser;

import java.util.List;
import java.util.Vector;


public class JPFieldDeclaration extends SimpleNode {
  public JPFieldDeclaration(int id) {
    super(id);
  }

  public JPFieldDeclaration(JParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public void addAccessModifier(String s) 
  {
      list.add(s);
  }
  
  public List getAccessModifier()
  {
      return list;
  }
  
  private List list = new Vector();
}
