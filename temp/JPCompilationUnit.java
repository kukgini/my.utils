/* Generated By:JJTree: Do not edit this line. JPCompilationUnit.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=JP,NODE_EXTENDS=,NODE_FACTORY= */
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
/* JavaCC - OriginalChecksum=00b9eb1bd051b0219eee806010f786b5 (do not edit this line) */
