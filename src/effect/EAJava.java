
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
public class EAJava
{
    private static HashMap map = new HashMap();
    
    public static EAJava find(String name)
    {
        name = name.substring(name.lastIndexOf('.') + 1);
        EAJava o = (EAJava)map.get(name);
        if (o == null) {
             o = new EAJava(name);
             map.put(name, o);
        }
        return o;
    }
    
    public static Iterator getIterator()
    {
        return map.values().iterator();
    }
    
    private EAJava(String name)
    {
        super();
        this.name = name;
    }
    
    public void setPackage(EAPackage p)
    {
        _package = p;
    }
    
    public EAPackage getPackage()
    {
        return _package;
    }
    
    public void addDependancy(String name)
    {
        EAJava src = EAJava.find(name);
        if (!dependancies.contains(src)) 
        {
            dependancies.add(src);
        }
    }
    
    public List listDependancies()
    {
        return dependancies;    
    }
    
    public List listJoltCalls()
    {
        return joltCalls;
    }
    
    /**
     * Jolt 서비스 이름을 분석해 내지 못할경우 Excel 의 비고란에
     * 변수 혹은 프로시저명을 보여주기 위해 앞에는 \t 을 넣어둔다.
     * 따라서 \t 로 시작하는 Jolt 호출은 서비스명 분석에 실패한
     * 것이다.
     * @return
     */
    public int countJoltCallAnalyzeSuccessed()
    {
        int c = 0;
        for (int i = 0; i < joltCalls.size(); i++)
        {
            if (!((String)joltCalls.get(i)).startsWith("\t"))
            {
                c++;
            }
        }
        return c;
    }
    /**
     * 패키지명이 분석되지 않았다면 JDK 나 타 라이브러리에서 선언된 클래스라고 판단된다.
     * @return
     */
    public boolean isUserDefined()
    {
        return !_package.isNull();
    }
    
    /**
     * 파라미터로 넘어온 값이 문자열(")로 시작한다면 그 값이 service name 이라고 판단하고
     * 그렇지 않을 경우엔 상수라고 판단하여 상수값 찾기를 시도한다. 상수라면 <클래스명>.<상수명>
     * 의 형태를 띄고 있을것이라 예상한다.
     * 
     * @param service
     */
    public void addJoltCall(String service)
    {
        if (isBD())
        {
            if (service.startsWith("\""))
            {
                joltCalls.add(service);
            }
            else
            {
                int index = service.indexOf('.');
                if ((index > 0)) 
                {
                    String cname = service.substring(0, index);
                    String fname = service.substring(index + 1);
                    
                    EAJava cls = EAJava.find(cname);  
                    EAVariable v = cls.findField(fname);
                    if (v != null) 
                    {
                        joltCalls.add(v.getValue() + "\t" + service);
                    }
                    else
                    {
                        joltCalls.add("\t" + service);
                    }
                }
                else 
                {
                    joltCalls.add("\t" + service);
                }                
            }
        }
    }
    
    public void addField(EAVariable v)
    {
        fields.put(v.getName(), v);   
    }
    
    public String getName()
    {
        if (_package.isNull())
            return name;
        else
            return _package + "." + name;
    }
    
    public EAVariable findField(String name)
    {
        return (EAVariable)fields.get(name);
    }
    
    public boolean hasInvalidJoltCall()
    {
        return (!isBD() && hasActualJoltCall());    
    }
    
    public boolean isController()
    {
        return isUserDefined() && name.endsWith("C");
    }
    
    public boolean isBD()
    {
        return isUserDefined() && name.endsWith("B");   
    }

    public boolean isDAO()
    {
        return isUserDefined() && name.endsWith("D");   
    }

    public boolean equals(Object o)
    {
        return name.equals(((EAJava)o).name);    
    }
    
    public String toString()
    {
        if (error)
            return name + "[x]";
        else
            return name;
    }
    
    public void increaseJoltCallCount()
    {
        joltCallCount++;
    }
    
    public boolean hasActualJoltCall()
    {
        return joltCallCount > 0;
    }

    public void setParseFailed()
    {
        error = true;    
    }
    
    public boolean isParseFailed()
    {
        return error;
    }
    
    private boolean error = false;
    private int joltCallCount = 0;
    private EAPackage _package = new EAPackage();
    private String name;
    private Map fields = new HashMap();
    private List dependancies = new ArrayList();
    private List joltCalls = new ArrayList();
}
