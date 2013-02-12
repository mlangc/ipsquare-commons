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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import ch.qos.logback.core.OutputStreamAppender;

import com.google.common.collect.Maps;

/**
 * Tests for {@link PerformanceLogFilter}.
 * 
 * @author Matthias Langer
 */
public class TestPerformanceLogFilter
{
    private static final Random rand = new Random();
    
    public static class TestAppender<E> extends OutputStreamAppender<E>
    {
        private static final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        @Override
        public void start()
        {
            setOutputStream(stream);
            super.start();
        }
    }
    
    private static class SleepyChain extends UnitTestFilterChain
    {
        final long millis;
        
        SleepyChain(long millis)
        {
            this.millis = millis;
        }
        
        @Override
        protected void performDoFilter(ServletRequest req, ServletResponse res)
        {
            try
            {
                Thread.sleep(millis);
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Test
    public void testDoFilter() throws IOException, ServletException
    {
        final String PREFIX = "!--";
        
        final String[] IN_LOG = new String[] {
            "/in1.test",
            "in2=test",
            "/in/test/3",
            "here4.do",
            PREFIX
        };
        
        final String[] NOT_IN_LOG = new String[] {
                "/missing1.do",
                "/skip/this.do"
        };
        
        final long THRESHOLD = 10;
        
        UnitTestFilterChain chain = new SleepyChain(1);
        PerformanceLogFilter filter = new PerformanceLogFilter();
        filter.init(filterConfig(null));
        filter.doFilter(req(null,  null, null), null, chain);
        filter.doFilter(req(IN_LOG[0], null, null), null, chain);
        filter.doFilter(req("/hansi/hintenrum", null, IN_LOG[1]), null, chain);
        filter.doFilter(req("/foo", IN_LOG[2], ""), null, chain);
        
        filter = new PerformanceLogFilter();
        filter.init(filterConfig(THRESHOLD, "^.*do$", ".*skip.*"));
        filter.doFilter(req(NOT_IN_LOG[0], null, null), null, chain);
        
        chain = new SleepyChain(THRESHOLD + 1);
        filter.doFilter(req(IN_LOG[3], null, "what=the&dj=hell"), null, chain);
        filter.doFilter(req("/you/should", NOT_IN_LOG[1], "right=now"), null, chain);
        
        filter = new PerformanceLogFilter();
        filter.init(filterConfig(THRESHOLD, null, null, PREFIX));
        filter.doFilter(req("/whatever/you/want", "/is/already/here", "with=you"), null, chain);
        
        String logString = TestAppender.stream.toString("UTF-8");
        for(String inLog : IN_LOG)
            assertThat(logString, containsString(inLog));
        for(String notInLog : NOT_IN_LOG)
            assertThat(logString, not(containsString(notInLog)));
    }
    
    private static HttpServletRequest req(String servletPath, String pathInfo, String queryString)
    {
        final String[] HTTP_METHODS = new String[] {
          "GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE"    
        };
        
        MockHttpServletRequest ret = new MockHttpServletRequest();
        ret.setMethod(HTTP_METHODS[rand.nextInt(HTTP_METHODS.length)]);
        ret.setServletPath(servletPath);
        ret.setPathInfo(pathInfo);
        ret.setQueryString(queryString);
        return ret;
    }
    
    private static FilterConfig filterConfig(Long threshold, String incPtn, String exPtn, String prefix)
    {
        Map<String, String> props = Maps.newHashMapWithExpectedSize(4);
        if(threshold != null)
            props.put(PerformanceLogFilter.INIT_PARAM_THRESHOLD, "" + threshold);
        if(incPtn != null)
            props.put(InitParameterNames.INCLUDE_PATH_PATTERN, incPtn);
        if(exPtn != null)
            props.put(InitParameterNames.EXCLUDE_PATH_PATTERN, exPtn);
        if(prefix != null)
            props.put(PerformanceLogFilter.INIT_PARAM_PREFIX, prefix);
        return new UnitTestFilterConfig(props);
    }
    
    private static FilterConfig filterConfig(Long threshold, String incPtn, String exPtn)
    {
        return filterConfig(threshold, incPtn, exPtn, null);
    }
    
    private static FilterConfig filterConfig(Long threshold)
    {
        return filterConfig(threshold, null, null);
    }
}
