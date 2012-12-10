/**
 * Copyright (C) 2012 IP SQUARE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
