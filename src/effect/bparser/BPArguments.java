/* Generated By:JJTree: Do not edit this line. BPArguments.java */

package effect.bparser;

public class BPArguments extends SimpleNode {
  public BPArguments(int id) {
    super(id);
  }

  public BPArguments(BParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(BParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}