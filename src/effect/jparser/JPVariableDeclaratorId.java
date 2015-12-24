/* Generated By:JJTree: Do not edit this line. ASTVariableDeclaratorId.java */

package effect.jparser;


public class JPVariableDeclaratorId extends SimpleNode {
  public JPVariableDeclaratorId(int id) {
    super(id);
  }

  public JPVariableDeclaratorId(JParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
  
  public String toString()
  {
      return super.toString() + ":" + id    ;
  }
  
  public void setID(String s)
  {
      id = s;
  }
  
  public String getID()
  {
      return id;
  }
    
  private String id;  
}
