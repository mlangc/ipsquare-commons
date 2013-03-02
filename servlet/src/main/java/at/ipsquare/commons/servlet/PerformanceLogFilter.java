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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ipsquare.commons.core.util.Classes;
import at.ipsquare.commons.core.util.PerformanceLogFormatter;
import at.ipsquare.commons.core.util.PerformanceLogger;

/**
 * This filter logs the execution time of incoming web requests using a {@link PerformanceLogger}.
 * 
 * @author Matthias Langer
 */
public class PerformanceLogFilter implements Filter
{
    private static final Logger log = LoggerFactory.getLogger(PerformanceLogFilter.class);
    
    private enum MessageFormatter implements PerformanceLogFormatter
    {
        INSTANCE;
        
        @Override
        public String format(StackTraceElement from, StackTraceElement to, long millis, String message)
        {
            return new StringBuilder()
              .append(millis)
              .append("ms <<")
              .append(message)
              .append(">>")
              .toString();
        }
    }
    
    /**
     * Init parameter name for the threshold to use (see {@link PerformanceLogger#PerformanceLogger(long)}).
     */
    public static String INIT_PARAM_THRESHOLD = "threshold";
    
    /**
     * Init parameter name for an optional prefix to use.
     */
    public static String INIT_PARAM_PREFIX = "prefix";
    
    /**
     * Init parameter name for the {@link PerformanceLogFormatter} to use.
     */
    public static String PERFORMANCE_LOG_FORMATTER = "performanceLogFormatter";
    
    private long threshold;
    private RequestMatcher requestMatcher;
    private String prefix;
    private Class<? extends PerformanceLogFormatter> formatterClass;
    
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
        formatterClass = formatterClassFromConfig(filterConfig);
    }
    
    private static Class<? extends PerformanceLogFormatter> formatterClassFromConfig(FilterConfig filterConfig)
    {
        String className = filterConfig.getInitParameter(PERFORMANCE_LOG_FORMATTER);
        if(StringUtils.isEmpty(className))
            return null;
        
        try
        {
            Class<?> clazz = Classes.forName(className);
            if(!PerformanceLogFormatter.class.isAssignableFrom(clazz))
                throw new ServletConfigurationError(className + " does not implement " + PerformanceLogFormatter.class.getName() + ".");
            
            @SuppressWarnings("unchecked")
            Class<? extends PerformanceLogFormatter> ret = (Class<? extends PerformanceLogFormatter>) clazz;
            return ret;
        }
        catch(ClassNotFoundException e)
        {
            throw new ServletConfigurationError("Cannot load formatter class '" + className + "'.");
        }
    }
    
    private PerformanceLogFormatter performanceLogFormatter()
    {
        if(formatterClass == null)
            return MessageFormatter.INSTANCE;
        
        try
        {
            return formatterClass.newInstance();
        }
        catch(Exception e)
        {
            log.error("Could not create an instance of " + formatterClass.getName() + ".", e);
            return MessageFormatter.INSTANCE;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if(!requestMatcher.matches(request))
            chain.doFilter(request, response);
        else
        {
            Throwable th = null;
            PerformanceLogger plog = new PerformanceLogger(threshold, performanceLogFormatter());
            try
            {
                chain.doFilter(request, response);
            }
            catch(Throwable e)
            {
                th = e;
            }
            finally
            {
                plog.logElapsed(toLogString(request, th));
            }
            
            if(th != null)
            {
                if(th instanceof IOException)
                    throw (IOException) th;
                
                if(th instanceof ServletException)
                    throw (ServletException) th;
                
                if(th instanceof RuntimeException)
                    throw (RuntimeException) th;
                
                if(th instanceof Error)
                    throw (Error) th;
                
                throw new RuntimeException(th);
            }
        }
    }
    
    private String toLogString(ServletRequest request, Throwable th)
    {
        if(!(request instanceof HttpServletRequest))
            return "" + request;
        
        HttpServletRequest req = (HttpServletRequest) request;
        StringBuilder sb = new StringBuilder()
            .append(prefix)
            .append(req.getMethod())
            .append(errorString(th))
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
    
    private static String errorString(Throwable th)
    {
        if(th == null)
            return "";
        
        return new StringBuilder()
            .append("(!")
            .append(th.getClass().getSimpleName())
            .append("!)")
            .toString();
    }

    @Override
    public void destroy()
    {
        
    }
}
