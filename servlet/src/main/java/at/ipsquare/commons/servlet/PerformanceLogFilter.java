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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ipsquare.commons.core.util.Classes;
import at.ipsquare.commons.core.util.PerformanceLogFormatter;
import at.ipsquare.commons.core.util.PerformanceLogger;

/**
 * This filter logs the execution time of incoming web requests using a {@link PerformanceLogger}.
 * 
 * @since 2.1.0
 * @author Matthias Langer
 */
public class PerformanceLogFilter implements Filter
{
    private static final Logger log = LoggerFactory.getLogger(PerformanceLogFilter.class);
    private static final PerformanceLogFilterMessageFormatter DEFAULT_LOG_FILTER_MESSAGE_FORMATTER = new DefaultPerformanceLogFilterMessageFormatter();
    
    private enum DefaultLogFormatter implements PerformanceLogFormatter
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
    public final static String INIT_PARAM_THRESHOLD = "threshold";
    
    /**
     * Init parameter name for an optional prefix to use.
     */
    public final static String INIT_PARAM_PREFIX = "prefix";
    
    /**
     * Init parameter name for the {@link PerformanceLogFormatter} to use.
     */
    public final static String INIT_PARAM_PERFORMANCE_LOG_FORMATTER = "performanceLogFormatter";
    
    /**
     * Init parameter name for the {@link PerformanceLogFilterMessageFormatter} to use.
     */
    public final static String INIT_PARAM_PERFORMANCE_LOG_FILTER_MESSAGE_FORMATTER = "performanceLogFilterMessageFormatter";
    
    private long threshold;
    private RequestMatcher requestMatcher;
    private String prefix;
    private Class<? extends PerformanceLogFormatter> logFormatterClass;
    private Class<? extends PerformanceLogFilterMessageFormatter> logFilterMessageFormatterClass;
    
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
        logFormatterClass = logFormatterClassFromConfig(filterConfig);
        logFilterMessageFormatterClass = logFilterMessageFormatterClassFromConfig(filterConfig);
    }
    
    private Class<? extends PerformanceLogFilterMessageFormatter> logFilterMessageFormatterClassFromConfig(FilterConfig filterConfig)
    {
        return typeFromConfig(filterConfig, INIT_PARAM_PERFORMANCE_LOG_FILTER_MESSAGE_FORMATTER, PerformanceLogFilterMessageFormatter.class);
    }
    
    private static Class<? extends PerformanceLogFormatter> logFormatterClassFromConfig(FilterConfig filterConfig)
    {
        return typeFromConfig(filterConfig, INIT_PARAM_PERFORMANCE_LOG_FORMATTER, PerformanceLogFormatter.class);
    }

    private static <T> Class<? extends T> typeFromConfig(FilterConfig filterConfig, String parameterName, Class<T> type)
    {
        String className = filterConfig.getInitParameter(parameterName);
        if(StringUtils.isEmpty(className))
            return null;
        
        try
        {
            return Classes.forName(className, type);
        }
        catch(ClassCastException e)
        {
            throw new ServletConfigurationError(e.getMessage());
        }
        catch(ClassNotFoundException e)
        {
            throw new ServletConfigurationError("Cannot load formatter class '" + className + "'.");
        }
    }
    
    private PerformanceLogFormatter performanceLogFormatter()
    {
        if(logFormatterClass == null)
            return DefaultLogFormatter.INSTANCE;
        
        try
        {
            return logFormatterClass.newInstance();
        }
        catch(Exception e)
        {
            log.error("Could not create an instance of " + logFormatterClass.getName() + ".", e);
            return DefaultLogFormatter.INSTANCE;
        }
    }
    
    private PerformanceLogFilterMessageFormatter filterMessageFormatter()
    {
        if(logFilterMessageFormatterClass == null)
            return DEFAULT_LOG_FILTER_MESSAGE_FORMATTER;
        
        try
        {
            return logFilterMessageFormatterClass.newInstance();
        }
        catch(Exception e)
        {
            log.error("Could not create an instance of " + logFilterMessageFormatterClass.getName() + ".", e);
            return DEFAULT_LOG_FILTER_MESSAGE_FORMATTER;
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        if(!requestMatcher.matches(req))
            chain.doFilter(req, res);
        else
        {
            Throwable th = null;
            PerformanceLogger plog = new PerformanceLogger(threshold, performanceLogFormatter());
            try
            {
                chain.doFilter(req, res);
            }
            catch(Throwable e)
            {
                th = e;
            }
            finally
            {
                plog.logElapsed(toLogString(req, res, th));
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
    
    private String toLogString(ServletRequest req, ServletResponse res, Throwable th)
    {
        return prefix + filterMessageFormatter().format(req, res, th);
    }
    
    @Override
    public void destroy()
    {
        
    }
}
