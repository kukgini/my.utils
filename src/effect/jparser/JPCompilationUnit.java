/* Generated By:JJTree: Do not edit this line. ASTCompilationUnit.java */

package effect.jparser;


public class JPCompilationUnit extends SimpleNode {
  public JPCompilationUnit(int id) {
    super(id);
  }

  public JPCompilationUnit(JParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}