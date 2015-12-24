package effect;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * $Id: $
 */
/**
 * $Id: $
 */
public class EAController
{
    private static Map controllers = new HashMap();
    public static int controllerFindError = 0;
    public static int xfmParseError = 0;
    
    public EAController(String file) 
    {
        super();
        this.file = file;
    }
    
    public static void add(EAController job)
    {
        controllers.put(job.name, job);
    }
    
    public static EAController find(String key)
    {
        EAController controller = (EAController)controllers.get(key);
        if (controller == null) controllerFindError++;
        return controller;    
    }

    public static int countControllers()
    {
        return controllers.size();
    }
    
    public static EAController getController(Object key)
    {
        return (EAController)controllers.get(key);
    }
    
    public static Iterator getControllerIterator()
    {
        return controllers.keySet().iterator();    
    }
    
    public String file;
    public String name;
    public String type;
    
    public List xfms = new ArrayList();

    public void addXfm(String xfm)
    {
        xfms.add(xfm);    
    }
}
