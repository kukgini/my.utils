/* Generated By:JJTree: Do not edit this line. BPStatementExpression.java */

package effect.bparser;

public class BPStatementExpression extends SimpleNode {
  public BPStatementExpression(int id) {
    super(id);
  }

  public BPStatementExpression(BParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(BParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}