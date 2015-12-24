/*
 * $Id: $
 */
package effect;

/**
 * $Id: $
 */
public class EAPackage
{
    public void setName(String s)
    {
        name = s;
    }
    
    public String getName()
    {
        return name;
    }
    
    public boolean isNull()
    {
        return (name == null || name.trim().equals(""));
    }
    
    public String toString()
    {
        return name; 
    }
    
    private String name;
}
