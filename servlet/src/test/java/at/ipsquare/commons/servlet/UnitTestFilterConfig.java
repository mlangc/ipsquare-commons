package at.ipsquare.commons.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.apache.commons.collections.IteratorUtils;

/**
 * {@link FilterConfig} implementation for unit tests.
 * 
 * @author Matthias Langer
 */
public class UnitTestFilterConfig implements FilterConfig
{
    private final Map<String, String> properties;
    
    /**
     * Constructs a new {@link FilterConfig} from the given properties.
     */
    public UnitTestFilterConfig(Map<String, String> properties)
    {
        if(properties == null)
            throw new NullPointerException();
        
        this.properties = new HashMap<String, String>(properties);
    }
    
    @Override
    public String getFilterName()
    {
        return "test";
    }
    
    @Override
    public String getInitParameter(String name)
    {
        return properties.get(name);
    }
    
    @Override
    public Enumeration<?> getInitParameterNames()
    {
        return IteratorUtils.asEnumeration(properties.keySet().iterator());
    }
    
    @Override
    public ServletContext getServletContext()
    {
        return null;
    }
}
