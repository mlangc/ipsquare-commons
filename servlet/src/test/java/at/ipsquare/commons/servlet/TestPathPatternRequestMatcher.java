package at.ipsquare.commons.servlet;


import static org.testng.Assert.assertEquals;

import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.testng.annotations.Test;

/**
 * Tests for {@link PathPatternRequestMatcher}.
 * 
 * @author Matthias Langer
 */
public class TestPathPatternRequestMatcher
{
    /**
     * Verifies that a trivial matcher, without explicit includes or excludes indeed matches everything.
     */
    @Test
    public void testWithTrivialMatcher()
    {
        testMatcher(true, null, null, "/egs/javax.servlet", "/GetReqUrl.html");
        testMatcher(true, null, null, "/egs/javax.servlet", "/GetReqUrl.html;c=123");
    }
    
    /**
     * Verifies that matchers without an exclude pattern behave as expected.
     */
    @Test
    public void testWithIncludeOnly()
    {
        testMatcher(true, "/foo.*", null, "/foo", null);
        testMatcher(false, "/foo.*", null, "", "/bar");
    }
    
    /**
     * Verifies that matchers without an include pattern behave as expected.
     */
    @Test
    public void testWithExcludeOnly()
    {
        testMatcher(false, null, "/.*\\.(css|gif)", "/foo/bar/styles.css", null);
        testMatcher(true, null, "/.*\\.(css|gif)", "/foo/bar", "/action.do");
    }
    
    /**
     * Verifies that matchers with an include as well as an exclude pattern behave as expected.
     */
    @Test
    public void testWithExcludeAndInclude()
    {
        testMatcher(true, "/foo/.*\\.do.*", ".*error.*", "/foo", "/test.do;q=3");
        testMatcher(false, "/foo/.*\\.do.*", ".*error.*", "/foo/error", null);
    }

    private static void testMatcher(boolean expected, String inc, String exc, String servletPath, String pathInfo)
    {
        RequestMatcher[] matchers = new RequestMatcher[] {
                new PathPatternRequestMatcher(inc, exc),
                PathPatternRequestMatcher.fromFilterConfig(filterConfig(inc, exc)),
                PathPatternRequestMatcher.fromFilterConfig(filterConfigWithContextParams(inc, exc))
        };
        
        for(RequestMatcher matcher : matchers)
        {
            testMatcher(expected, matcher, servletPath, pathInfo);
            testMatcher(expected, matcher, servletPath + StringUtils.defaultString(pathInfo), null);
            testMatcher(expected, matcher, "", servletPath + StringUtils.defaultString(pathInfo));
        }
    }

    private static void testMatcher(boolean expected, RequestMatcher matcher, String servletPath, String pathInfo)
    {
        assertEquals(expected, matcher.matches(servletRequest(servletPath, pathInfo)));
    }

    private static ServletRequest servletRequest(String servletPath, String pathInfo)
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath(servletPath);
        req.setPathInfo(pathInfo);
        return req;
    }
    
    private static FilterConfig filterConfig(String inc, String exc)
    {
        MockFilterConfig config = new MockFilterConfig();
        config.addInitParameter(InitParameterNames.INCLUDE_PATH_PATTERN, inc);
        config.addInitParameter(InitParameterNames.EXCLUDE_PATH_PATTERN, exc);
        return config;
    }
    
    private static FilterConfig filterConfigWithContextParams(String inc, String exc)
    {
        MockServletContext context = new MockServletContext();
        context.addInitParameter(InitParameterNames.INCLUDE_PATH_PATTERN, inc);
        context.addInitParameter(InitParameterNames.EXCLUDE_PATH_PATTERN, exc);
        return new MockFilterConfig(context);
    }
}
