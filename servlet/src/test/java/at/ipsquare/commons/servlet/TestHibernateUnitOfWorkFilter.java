/**
 * Copyright (C) 2013 Matthias Langer
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.Session;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import at.ipsquare.commons.core.interfaces.ExecutionError;
import at.ipsquare.commons.core.interfaces.UnitOfWork;
import at.ipsquare.commons.hibernate.HibernateRepository;
import at.ipsquare.commons.hibernate.HibernateRepositoryProvider;


/**
 * Tests for {@link HibernateUnitOfWorkFilter}.
 * 
 * @author Matthias Langer
 */
public class TestHibernateUnitOfWorkFilter
{
    public interface TestRepository1 extends HibernateRepository
    {
        
    }
    
    public interface TestRepository2 extends HibernateRepository
    {
        
    }
    
    private static class TestHibernateRepository implements TestRepository1, TestRepository2
    {
        static final AtomicInteger unitsProcessedOk = new AtomicInteger();
        static final AtomicInteger unitsProcessedError = new AtomicInteger();
        
        @Override
        public Session currentSession()
        {
            return null;
        }
        
        @Override
        public <T> T executeUnitOfWork(UnitOfWork<T> work)
        {
            boolean ok = false;
            try
            {
                work.execute();
                ok = true;
                return null;
            }
            catch(Exception e)
            {
                throw new ExecutionError(e);
            }
            finally
            {
                (ok ? unitsProcessedOk : unitsProcessedError).incrementAndGet();
            }
        }
        
        @Override
        public void close()
        {
            
        }
        
        public boolean isClosed() 
        {
            return false;
        }
    }
    
    public static class TestRepositoryProvider1 implements HibernateRepositoryProvider
    {
        static final TestRepository1 repo = new TestHibernateRepository();
        
        @Override
        public TestRepository1 get()
        {
            return repo;
        }
    }
    
    public static class TestRepositoryProvider2 implements  HibernateRepositoryProvider
    {
        static final TestRepository2 repo = new TestHibernateRepository();
        
        @Override
        public TestRepository2 get()
        {
            return repo;
        }
    }
    
    private static class TestFilterChainOk implements FilterChain
    {
        @Override
        public void doFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException
        {
            
        }
    }
    
    private static class TestFilterChainSevletException implements FilterChain
    {
        @Override
        public void doFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException
        {
            throw new ServletException("Ups!");
        }
    }
    
    private static class TestFilterChainIoException implements FilterChain
    {
        @Override
        public void doFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException
        {
            throw new IOException("Ups!");
        }
    }
    
    private static class TestFilterChainRuntimeException implements FilterChain
    {
        @Override
        public void doFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException
        {
            throw new RuntimeException("Ups!");
        }
    }
    
    /**
     * Tests {@link HibernateUnitOfWorkFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}.
     */
    @Test
    public void testDoFilter() throws ServletException
    {
        HibernateUnitOfWorkFilter filter = new HibernateUnitOfWorkFilter();
        filter.init(validTestFilterConfig());
        try
        {
            doFilter(filter, "/foo", new TestFilterChainOk(), null);
            doFilter(filter, "/foo.jpg", new TestFilterChainOk(), null);
            doFilter(filter, "/foo", new TestFilterChainSevletException(), RuntimeException.class);
            doFilter(filter, "/foo", new TestFilterChainIoException(), RuntimeException.class);
            doFilter(filter, "/foo", new TestFilterChainRuntimeException(), RuntimeException.class);
        }
        finally
        {
            filter.destroy();
        }
        
        assertEquals(2, TestHibernateRepository.unitsProcessedOk.get());
        assertEquals(6, TestHibernateRepository.unitsProcessedError.get());
    }
    
    private static FilterConfig validTestFilterConfig()
    {
        return new UnitTestFilterConfig(validTestFilterConfigProperties());
    }
    
    private static Map<String, String> validTestFilterConfigProperties()
    {
        Map<String, String> props = new LinkedHashMap<String, String>();
        props.put("repo1", TestRepositoryProvider1.class.getName());
        props.put("repo2", TestRepositoryProvider2.class.getName());
        props.put(InitParameterNames.INCLUDE_PATH_PATTERN, ".*");
        props.put(InitParameterNames.EXCLUDE_PATH_PATTERN, ".*\\.jpg");
        return props;
    }
    
    /**
     * Tests for {@link HibernateUnitOfWorkFilter#init(FilterConfig)}.
     */
    @Test
    public void testInit()
    {
        Map<String, String> props = new HashMap<String, String>();
        testInit(props, null);
        
        props = validTestFilterConfigProperties();
        testInit(props, null);
        
        props.put("", "");
        testInit(props, RuntimeException.class);
        
        props.remove("");
        props.put("duplicate", TestRepositoryProvider1.class.getName());
        testInit(props, ServletConfigurationError.class);
    }
    
    private static void  testInit(Map<String, String> properties, Class<? extends Throwable> expectedException)
    {
        try
        {
            Filter filter = new HibernateUnitOfWorkFilter();
            filter.init(new UnitTestFilterConfig(properties));
            if(expectedException != null)
                fail("Exception expected.");
        }
        catch(Throwable th)
        {
            expectException(th, expectedException);
        }
    }
    
    
    private static void doFilter(Filter filter, String path, FilterChain chain, Class<? extends Throwable> expectedException)
    {
        try
        {
            filter.doFilter(requestFromPath(path), null, chain);
            if(expectedException != null)
                fail("Exception expected.");
        }
        catch(Throwable th)
        {
           expectException(th, expectedException);
        }
    }
    
    private static ServletRequest requestFromPath(String path)
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setPathInfo(path);
        req.setServletPath("");
        return req;
    }

    private static void expectException(Throwable th, Class<? extends Throwable> expectedException)
    {
        if(expectedException == null)
            throw new RuntimeException(th);
        if(!expectedException.isInstance(th))
            fail("Expected " + expectedException.getSimpleName() + " but got  :" + th);
    }
}
