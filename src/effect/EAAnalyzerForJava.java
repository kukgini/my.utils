/*
 * $Id: $
 */
package effect;

import effect.jparser.*;

/**
 * $Id: $
 */
public class EAAnalyzerForJava implements JParserVisitor
{
    private int indent = 0;
    
    public EAAnalyzerForJava()
    {
        super();
    }

    private String prefix() 
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indent; ++i) 
        {
          sb.append(" ");
        }
        return sb.toString();
    }
    
    private Object childrenAccept(SimpleNode node, Object data)
    {
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }
    
    public Object visit(SimpleNode node, Object data)
    {
        log(node, data);
        
        return childrenAccept(node, data);
    }

    public Object visit(JPCompilationUnit node, Object data)
    {
        log(node, data);

        return childrenAccept(node, data);
    }

    public Object visit(JPFieldDeclaration node, Object data)
    {
        log(node, data);
        data = childrenAccept(node, data);

        if (node.jjtGetNumChildren() == 3) 
        {
            boolean b = isLiteralFieldDeclaration(node);
            
            if (b && !((JPType)node.jjtGetChild(0)).isPrimitive()) 
            {
                EAVariable v = new EAVariable();
                v.setAccessModifiers(node.getAccessModifier());
                if (v.isStatic())
                {
                    v.setType(((JPName)node.jjtGetChild(0).jjtGetChild(0)).getName());
                    v.setName(((JPVariableDeclaratorId)node.jjtGetChild(1)).getID());
                    v.setValue(((JPLiteral)node.jjtGetChild(2)).getContent());
                    ((EAJava)data).addField(v);
                }
            }
        }
        return data;
    }
    
    /**
     * @param node
     * @return
     */
    public boolean isLiteralFieldDeclaration(JPFieldDeclaration node)
    {
        boolean b = true;
        b = b && (node.jjtGetChild(0) instanceof JPType);
        b = b && (node.jjtGetChild(1) instanceof JPVariableDeclaratorId);
        b = b && (node.jjtGetChild(2) instanceof JPLiteral);
        return b;
    }

    public Object visit(JPPackageDeclaration node, Object data)
    {
        log(node, data);
        
        childrenAccept(node, data);
        
        JPName nameNode = (JPName)node.jjtGetChild(0);
        ((EAJava)data).getPackage().setName(nameNode.getName());
        return data;
    }

    public Object visit(JPType node, Object data) 
    { 
        log(node, data);

        data = childrenAccept(node, data);
        if (!node.isPrimitive()) {
            ((EAJava)data).addDependancy(node.getName());
        }
        return data; 
    }
    
    public Object visit(JPName node, Object data) 
    { 
        log(node, data);
        String name = node.getName();
        if (name.matches("^[A-Z].*"))
        {
            if (name.indexOf('.') > 0) 
            {
                name = name.substring(0, name.indexOf('.'));
            }
            ((EAJava)data).addDependancy(name);
        }   
        data = childrenAccept(node, data);
        return data; 
    }
    
    public Object visit(JPVariableDeclaratorId node, Object data)
    {
        log(node, data);

        if (data instanceof EAVariable)
        {
            ((EAVariable)data).setName(node.getID());
        }
        data = childrenAccept(node, data);
        return data; 
    }
    
    public Object visit(JPLiteral node, Object data)
    {
        log(node, data);

        data = childrenAccept(node, data);
        return data; 
    }
    
    private void log(Node node, Object data)
    {
        //System.out.println(prefix() + node.toString() + ":" + ((SimpleNode)node).getContent());
    }
}
