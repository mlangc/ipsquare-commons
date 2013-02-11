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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.Test;

/**
 * Tests for {@link RequestEncodingFilter}.
 * 
 * @author Matthias Langer
 */
public class TestRequestEncodingFilter
{
    private static class TestFilterChain implements FilterChain
    {
        boolean called;
        
        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException
        {
            called = true;
        }
    }
    
    private static class EncodingFilterConfig
    {
        String characterEncoding;
        boolean force;
        String includePattern;
        String excludePattern;
    }
    
    private static class ReqConfig
    {
        String reqEncoding;
        String path;
    }
    
    
    @Test
    public void testDoFilter() throws ServletException, IOException
    {
        testDoFilter(
                filterConfig(), 
                reqCfg(null, null), 
                "UTF-8");
        
        testDoFilter(
                filterConfig(), 
                reqCfg("US-ASCII", null), 
                "US-ASCII");
                
        testDoFilter(
                filterConfig("UTF-8", true),
                reqCfg("US-ASCII", null),
                "UTF-8");
        
        testDoFilter(
                filterConfig("UTF-8", false, ".*do$", ".*test.*"),
                reqCfg(null, "mirkas.do"),
                "UTF-8");
        
        testDoFilter(
                filterConfig("UTF-8", true, ".*do$", ".*test.*"),
                reqCfg(null, "test"),
                null);
    }
    
    private static void testDoFilter(EncodingFilterConfig filterConfig, ReqConfig reqResCfg, String exp) throws ServletException, IOException
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(reqResCfg.reqEncoding);
        req.setServletPath(reqResCfg.path);
        
        RequestEncodingFilter filter = new RequestEncodingFilter();
        filter.init(toServletFilterConfig(filterConfig));
        try
        {
            TestFilterChain chain = new TestFilterChain();
            filter.doFilter(req, new MockHttpServletResponse(), chain);
            assertTrue(chain.called);
            assertThat(req.getCharacterEncoding(), equalTo(exp));
        }
        finally
        {
            filter.destroy();
        }
    }

    private static FilterConfig toServletFilterConfig(EncodingFilterConfig cfg)
    {
        if(cfg == null)
            return new UnitTestFilterConfig();
        
        Map<String, String> props = new HashMap<String, String>();
        props.put(RequestEncodingFilter.INIT_PARAM_NAME_FORCE, "" + cfg.force);
        if(cfg.characterEncoding != null)
            props.put(RequestEncodingFilter.INIT_PARAM_NAME_ENCODING, cfg.characterEncoding);

        if(cfg.includePattern != null)
            props.put(InitParameterNames.INCLUDE_PATH_PATTERN, cfg.includePattern);
        
        if(cfg.excludePattern != null)
            props.put(InitParameterNames.EXCLUDE_PATH_PATTERN, cfg.excludePattern);
        
        return new UnitTestFilterConfig(props);
    }

    private static EncodingFilterConfig filterConfig()
    {
        return null;
    }
    
    private static EncodingFilterConfig filterConfig(String encoding, boolean force)
    {
        EncodingFilterConfig ret = new EncodingFilterConfig();
        ret.characterEncoding = encoding;
        ret.force = force;
        return ret;
    }
    
    private static EncodingFilterConfig filterConfig(String encoding, boolean force, String includePtn, String excludePtn)
    {
        EncodingFilterConfig ret = filterConfig(encoding, force);
        ret.includePattern = includePtn;
        ret.excludePattern = excludePtn;
        return ret;
    }
    
    private static ReqConfig reqCfg(String reqEnc, String path)
    {
        ReqConfig ret = new ReqConfig();
        ret.reqEncoding = reqEnc;
        ret.path = path;
        return ret;
    }
}
