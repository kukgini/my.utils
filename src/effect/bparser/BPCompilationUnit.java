/* Generated By:JJTree: Do not edit this line. BPCompilationUnit.java */

package effect.bparser;

public class BPCompilationUnit extends SimpleNode {
  public BPCompilationUnit(int id) {
    super(id);
  }

  public BPCompilationUnit(BParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(BParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}