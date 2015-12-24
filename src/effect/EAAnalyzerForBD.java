/*
 * $Id: $
 */
package effect;

import effect.bparser.BPArguments;
import effect.bparser.BPCompilationUnit;
import effect.bparser.BPLiteral;
import effect.bparser.BPLocalVariableDeclaration;
import effect.bparser.BPName;
import effect.bparser.BPStatementExpression;
import effect.bparser.BParserVisitor;
import effect.bparser.Node;
import effect.bparser.SimpleNode;

/**
 * $Id: $
 */
public class EAAnalyzerForBD implements BParserVisitor
{
    private int indent = 0;
    
    public EAAnalyzerForBD()
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


    private void log(Node node, Object data)
    {
        //System.out.println(prefix() + node.getClass().getName().substring(15) + ":" + ((SimpleNode)node).getContent());
    }

    public Object visit(SimpleNode node, Object data)
    {        
        log(node, data);
        return childrenAccept(node, data);
    }
    
    public Object visit(BPCompilationUnit node, Object data)
    {        
        log(node, data);
        return childrenAccept(node, data);
    }
    
    public Object visit(BPName node, Object data)
    {        
        log(node, data);
        
        if (node.getContent().matches(".*[.]\\s*callTuxedoService$"))
        {
            ((EAJava)data).increaseJoltCallCount();    
        }
        return childrenAccept(node, data);
    }

    public Object visit(BPLiteral node, Object data)
    {        
        log(node, data);
        return childrenAccept(node, data);
    }

    public Object visit(BPArguments node, Object data)
    {        
        log(node, data);
        data = childrenAccept(node, data);
        
        if (isJoltCall(node))
        {
            int index = getJoltCallBeginIndex(node);
            if (isJoltCallDirect(node))
            {
                String service = ((SimpleNode)node.jjtGetChild(index + 1).jjtGetChild(1)).getContent();
                ((EAJava)data).addJoltCall(service);
            }
            else if (isJoltCallIndirect(node))
            {
                SimpleNode secondParameter = (SimpleNode)node.jjtGetChild(index + 1).jjtGetChild(1);
                String service = secondParameter.getContent();
                ((EAJava)data).addJoltCall(service);
            }
        }
        return data;
    }

    public Object visit(BPLocalVariableDeclaration node, Object data)
    {        
        log(node, data);
        return childrenAccept(node, data);
    }

    public Object visit(BPStatementExpression node, Object data)
    {        
        log(node, data);
        data = childrenAccept(node, data);
        
        if (isJoltCall(node))
        {
            int index = getJoltCallBeginIndex(node);
            if (isJoltCallDirect(node))
            {
                String service = ((SimpleNode)node.jjtGetChild(index + 1).jjtGetChild(1)).getContent();
                ((EAJava)data).addJoltCall(service);
            }
            else if (isJoltCallIndirect(node))
            {
                SimpleNode secondParameter = (SimpleNode)node.jjtGetChild(index + 1).jjtGetChild(1);
                String service = secondParameter.getContent();
                ((EAJava)data).addJoltCall(service);
            }
        }
        return data;
    }
    
    private boolean isJoltCall(Node node)
    {
        boolean b = true;
        if (node.jjtGetNumChildren() == 2)
        {
            b = b && (node.jjtGetChild(0) instanceof BPName);
            b = b && (node.jjtGetChild(1) instanceof BPArguments);
            b = b && (node.jjtGetChild(1).jjtGetNumChildren() == 2);
        }
        else if (node.jjtGetNumChildren() == 3)
        {
            b = b && (node.jjtGetChild(0) instanceof BPName);
            b = b && (node.jjtGetChild(1) instanceof BPName);
            b = b && (node.jjtGetChild(2) instanceof BPArguments);
            b = b && (node.jjtGetChild(2).jjtGetNumChildren() == 2);
        }
        else
        {
            b = false;
        }
        return b;
    }
    
    private int getJoltCallBeginIndex(Node node)
    {
        if (node.jjtGetNumChildren() == 2)
        {
            return 0;
        }
        else if (node.jjtGetNumChildren() == 3)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
    private boolean isJoltCallDirect(Node node)
    {
        boolean b = true;
        int index = getJoltCallBeginIndex(node);
        BPName name = (BPName)node.jjtGetChild(index);
        b = b && name.getContent().matches(".*[.]put");
        b = b && (node.jjtGetChild(index + 1).jjtGetChild(0) instanceof BPLiteral);
        if (b == true)
        {
            BPLiteral firstArgument = (BPLiteral)node.jjtGetChild(index + 1).jjtGetChild(0);
            b = b && "\"SRVCNM\"".equals(firstArgument.getContent());
        }
        return b;
    }

    private boolean isJoltCallIndirect(Node node)
    {
        boolean b = true;
        int index = getJoltCallBeginIndex(node);
        BPName name = (BPName)node.jjtGetChild(index);
        b = b && name.getContent().matches(".*[.]setServiceCommItem");
        b = b && (node.jjtGetChild(index + 1).jjtGetChild(0) instanceof BPName);
        Node secondParameter = node.jjtGetChild(index + 1).jjtGetChild(1);
        if (b == true)
        {
            if (secondParameter instanceof BPName)
            {
                b = b && ((BPName)secondParameter).getContent().matches(".*[.].*");
            }
            else if (secondParameter instanceof BPLiteral) 
            {
                b = true;
            }
            else
            {
                b = false;
            }
        }
        return b;
    }
}
