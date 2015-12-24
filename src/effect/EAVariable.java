/*
 * $Id: $
 */
package effect;

import java.util.List;

/**
 * $Id: $
 */
public class EAVariable
{

    public String getName()
    {
        return name;
    }
    
    public void setName(String s)
    {
        name = s;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String s)
    {
        value = s;
    }
    
    public void setType(String s)
    {
        type = EAJava.find(s);
    }
    
    public EAJava getType()
    {
        return type;
    }
    
    public void setAccessModifiers(List accessModifiers)
    {
        this.accessModifiers = accessModifiers;
    }
    
    public boolean isStatic()
    {
        return accessModifiers.contains("static");
    }    
    
    public String toString()
    {
        return name + " (" + value + ")";
    }
    
    private List accessModifiers = null;
    private String name;
    private EAJava type;
    private String value;
}
