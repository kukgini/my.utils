/* Generated By:JJTree: Do not edit this line. JPPackageDeclaration.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=JP,NODE_EXTENDS=,NODE_FACTORY= */
package effect.jparser;

public class JPPackageDeclaration extends SimpleNode {
  public JPPackageDeclaration(int id) {
    super(id);
  }

  public JPPackageDeclaration(JParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=ff418b39f1abb8ce116bf13067785bf3 (do not edit this line) */
