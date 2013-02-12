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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import at.ipsquare.commons.core.util.PerformanceLogger;

/**
 * This filter logs the execution time of incoming web requests using a {@link PerformanceLogger}.
 * 
 * @author Matthias Langer
 */
public class PerformanceLogFilter implements Filter
{
    /**
     * Init parameter name for the threshold to use (see {@link PerformanceLogger#PerformanceLogger(long)}).
     */
    public static String INIT_PARAM_THRESHOLD = "threshold";
    
    /**
     * Init parameter name for an optional prefix to use.
     */
    public static String INIT_PARAM_PREFIX = "prefix";
    
    private long threshold;
    private RequestMatcher requestMatcher;
    private String prefix;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        String thresholdString = filterConfig.getInitParameter(INIT_PARAM_THRESHOLD);
        if(thresholdString != null)
        {
            try
            {
                threshold = Long.parseLong(thresholdString);
            }
            catch(NumberFormatException e)
            {
                throw new ServletConfigurationError(
                        "Not a legal value for " + INIT_PARAM_THRESHOLD + ": '" + thresholdString + "'", e); 
            }
        }
        
        requestMatcher = PathPatternRequestMatcher.fromFilterConfig(filterConfig);
        prefix = StringUtils.defaultString(filterConfig.getInitParameter(INIT_PARAM_PREFIX));
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if(!requestMatcher.matches(request))
            chain.doFilter(request, response);
        else
        {
            PerformanceLogger plog = new PerformanceLogger(threshold);
            try
            {
                chain.doFilter(request, response);
            }
            finally
            {
                plog.logElapsed(toLogString(request));
            }
        }
    }
    
    private String toLogString(ServletRequest request)
    {
        if(!(request instanceof HttpServletRequest))
            return "" + request;
        
        HttpServletRequest req = (HttpServletRequest) request;
        StringBuilder sb = new StringBuilder()
            .append(prefix)
            .append(req.getMethod())
            .append(" ");
        int lenWithMethodAndSpace = sb.length();
        
        if(req.getServletPath() != null)
            sb.append(req.getServletPath());
        if(req.getPathInfo() != null)
            sb.append(req.getPathInfo());
        if(StringUtils.isNotEmpty(req.getQueryString()))
            sb.append("?").append(req.getQueryString());
        
        if(sb.length() == lenWithMethodAndSpace)
            sb.setLength(lenWithMethodAndSpace - 1);
        return sb.toString();
    }

    @Override
    public void destroy()
    {
        
    }
}
