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

import java.util.regex.Pattern;

import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A matcher that matches (relative) path of the request against regular expressions.
 * 
 * <h4>About the path being matched:</h4>
 *  It is important to note that this filter only considers the path of the request relative to
 *  the context root of the web application. So, if the request is {@literal http://server/web-app/some/path?p0=1&p1=b},
 *  the path that is used for matching is exactly {@literal /some/path}.
 *  
 * @author Matthias Langer
 */
@ThreadSafe
public class PathPatternRequestMatcher implements RequestMatcher
{
    private static final Logger log = LoggerFactory.getLogger(PathPatternRequestMatcher.class);
    
    private final Pattern includePattern;
    private final Pattern excludePattern;
    
    /**
     * Builds a {@link PathPatternRequestMatcher} from the given filter configuration.
     * 
     * @see InitParameterNames#INCLUDE_PATH_PATTERN
     * @see InitParameterNames#EXCLUDE_PATH_PATTERN
     */
    public static RequestMatcher fromFilterConfig(FilterConfig config)
    {
        String inc = config.getInitParameter(InitParameterNames.INCLUDE_PATH_PATTERN);
        String exc = config.getInitParameter(InitParameterNames.EXCLUDE_PATH_PATTERN);
        
        if(inc == null)
            inc = config.getServletContext().getInitParameter(InitParameterNames.INCLUDE_PATH_PATTERN);
        if(exc == null)
            exc = config.getServletContext().getInitParameter(InitParameterNames.EXCLUDE_PATH_PATTERN);
        
        if(inc == null && exc == null)
            return TrivialRequestMatcher.ANYTHING;
        return new PathPatternRequestMatcher(inc, exc);
    }
    
    /**
     * Convenience constructor (see {@link #PathPatternRequestMatcher(Pattern, Pattern)}}.
     */
    public PathPatternRequestMatcher(String includePattern, String excludePattern)
    {
        this(compilePattern(includePattern), compilePattern(excludePattern));
    }
 
    /**
     * Constructor.
     * 
     * <h4>Notes:</h4>
     *  <ul>
     *    <li>Include patterns are considered before exclude patterns.</li>
     *    <li>The exact nature of the paths that the given patterns are applied to are explained in the documentation of this class.</li>
     *    <li>The ease regular expression debugging, {@link #matches(ServletRequest)} logs every path that is matched or rejected, if the log level is DEBUG or lower.</li>
     *  </ul>
     * 
     * @param includePattern a pattern for relative paths to include (pass NULL to include all paths). 
     * @param excludePattern a pattern for relative paths to exclude (pass NULL if you don't want to exclude anything).
     */
    public PathPatternRequestMatcher(Pattern includePattern, Pattern excludePattern)
    {
        this.includePattern = includePattern;
        this.excludePattern = excludePattern;
    }
    
    @Override
    public boolean matches(ServletRequest req)
    {
        if(!(req instanceof HttpServletRequest))
            return false;
        
        if(includePattern == null && excludePattern == null)
            return true;
        
        Boolean ret = null;
        String path = exctractPath((HttpServletRequest) req);
        
        try
        {
            if(includePattern != null && !includePattern.matcher(path).matches())
                return (ret = false);
            if(excludePattern != null && excludePattern.matcher(path).matches())
                return (ret = false);
            return (ret = true);
        }
        finally
        {
            if(ret == null)
                throw new AssertionError("Implementation error: ret not set, please fix!");
            
            if(log.isDebugEnabled())
                log.debug(this + " " + (ret ? "matched" : "rejected") + " path '" + path + "'."); 
        }
    }
    
    private static String exctractPath(HttpServletRequest req)
    {
        return req.getServletPath().concat(StringUtils.defaultString(req.getPathInfo()));
    }

    private static Pattern compilePattern(String pattern)
    {
        return (pattern != null ? Pattern.compile(pattern) : null);
    }
    
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[" + patternString(includePattern) + ", " + patternString(excludePattern) + "]";
    }
    
    private static String patternString(Pattern pattern)
    {
        if(pattern == null)
            return "null";
        return "'" + pattern + "'";
    }
}
