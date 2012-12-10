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

import java.io.IOException;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import at.ipsquare.hibernate.AbstractUnitOfWork;
import at.ipsquare.hibernate.HibernateRepository;
import at.ipsquare.hibernate.UnitOfWork;

import com.google.inject.Provider;

/**
 * This class implements a {@link Filter} that wraps entire web requests in {@link UnitOfWork} instances using the configured {@link HibernateRepository} instances.
 * 
 * <p/>
 * This servlet filter is an implementation of the session-per-request pattern. By default, you also get one transaction per request, but nobody hinders
 * you from doing more fine grained transaction management if you need it.
 * 
 * @author Matthias Langer
 */
public class UnitOfWorkFilter implements Filter
{
    private volatile Map<String, HibernateRepository> repoMap;
    private volatile RequestMatcher requestMatcher;
    
    @Override
    public void destroy()
    {
        
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        if(requestMatcher.matches(req))
            recurseThroughRepos((repoMap != null ? repoMap.entrySet().iterator() : null), req, res, chain);
        else
            chain.doFilter(req, res);
    }
    
    private static void  recurseThroughRepos(final Iterator<Map.Entry<String, HibernateRepository>> iter, final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException
    {
        if(iter == null || !iter.hasNext())
        {
            chain.doFilter(req, res);
            return;
        }
        
        Map.Entry<String, HibernateRepository> entry = iter.next();
        final String name = entry.getKey();
        HibernateRepository repo = entry.getValue();
        
        repo.executeUnitOfWork(new AbstractUnitOfWork<Void>()
        {
            @Override
            public Void execute() throws Exception
            {
                recurseThroughRepos(iter, req, res, chain);
                return null;
            }
            
            @Override
            public String getName()
            {
                return name;
            }
        });
    }

    @Override
    public void init(FilterConfig cfg) throws ServletException
    {
        String includePattern = null;
        String excludePattern = null;
        
        Map<String, HibernateRepository> newRepoMap = new LinkedHashMap<String, HibernateRepository>();
        Enumeration<?> paramNames = cfg.getInitParameterNames();
        if(paramNames != null)
        {
            while(paramNames.hasMoreElements())
            {
                Object elem = paramNames.nextElement();
                if(elem != null)
                {
                    String name = elem.toString();
                    String value = cfg.getInitParameter(name);
                    
                    if(InitParameterNames.INCLUDE_PATH_PATTERN.equals(name))
                        includePattern = value;
                    else if(InitParameterNames.EXCLUDE_PATH_PATTERN.equals(name))
                        excludePattern = value;
                    else if(value != null)
                    {
                      HibernateRepository repo = loadHibernateRepository(value);
                      newRepoMap.put(name, repo);
                    }
                }
            }
        }
        
        if(includePattern != null || excludePattern != null)
            requestMatcher = new PathPatternRequestMatcher(includePattern, excludePattern);
        else
            requestMatcher = TrivialRequestMatcher.ANYTHING;
        
        checkForIdenticalRepos(newRepoMap);
        repoMap = newRepoMap;
    }
    
    private static void checkForIdenticalRepos(Map<String, HibernateRepository> repoMap)
    {
        Map<HibernateRepository, String> reverseRepoMap = new IdentityHashMap<HibernateRepository, String>();
        for(Map.Entry<String, HibernateRepository> entry : repoMap.entrySet())
        {
          String oldKey = reverseRepoMap.put(entry.getValue(), entry.getKey());
          if(oldKey != null)
          {
            throw new ServletConfigurationError(
                "Attempting to register the identical repositories ('" + entry.getValue() + "') with different names ('" + oldKey + "', '" + entry.getKey() + "').");
          }
        }
    }

    private static Provider<?> loadProvider(String className)
    {
        Class<?> clazz = loadClass(className);
        if(!Provider.class.isAssignableFrom(clazz))
            throw new ServletConfigurationError("'" + clazz.getCanonicalName() + "' does not implement '" + Provider.class.getCanonicalName() + "'.");

        try
        {
            Provider<?> provider = (Provider<?>) clazz.newInstance();
            return provider;
        }
        catch(InstantiationException e)
        {
            throw new ServletConfigurationError(unableToInstantineErrorString(clazz), e);
        }
        catch(IllegalAccessException e)
        {
          throw new ServletConfigurationError(unableToInstantineErrorString(clazz), e);
        }
    }
    
    private static HibernateRepository loadHibernateRepository(String providerClassName)
    {
      Provider<?> provider = loadProvider(providerClassName);
      Object provided = provider.get();
      
      if(!(provided instanceof HibernateRepository))
      {
        throw new ServletConfigurationError(
            "Expected '"  + provider.getClass().getCanonicalName() + ".get()' to return an instance of HibernateRepository but got '" + provided + "');");
      }
      
      return (HibernateRepository) provided;
    }
    
    private static String unableToInstantineErrorString(Class<?> clazz)
    {
        return "Unable to instantine '" + clazz.getCanonicalName() + "'.";
    }
    
    private static ClassLoader[] classLoaders()
    {
        ClassLoader contextLoader =  Thread.currentThread().getContextClassLoader();
        ClassLoader myLoader =  UnitOfWorkFilter.class.getClassLoader();
        
        if(contextLoader != null && contextLoader != myLoader)
            return new ClassLoader[] { contextLoader, myLoader };
        return new ClassLoader[] { myLoader };
    }
    
    private static Class<?> loadClass(String className)
    {
        for(ClassLoader loader : classLoaders())
        {
            try
            {
                return loader.loadClass(className);
            }
            catch(ClassNotFoundException e)
            {
                // OK, try next class loader!
            }
        }
        
        throw new ServletConfigurationError("Could not load class '" + className + "'.");
    }
}
