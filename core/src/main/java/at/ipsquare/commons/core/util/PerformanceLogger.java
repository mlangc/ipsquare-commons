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
package at.ipsquare.commons.core.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import net.jcip.annotations.NotThreadSafe;

/**
 * A simple performance logger.
 * 
 * <p>
 * This class is meant to be used like this:
 * <pre>
 * //...
 * PerformanceLogger plog = new PerformanceLogger();
 * doWork();
 * plog.logElapsedAndRestart();
 * doSomeMoreWork();
 * plog.logElapsed();
 * //...
 * </pre>
 * or
 * <pre>
 * //...
 * PerformanceLogger.timedExec(() -&gt; {
 *     doSomeWork();
 *     return doSomeMoreWork();
 * }
 * //...
 * </pre>
 *
 * For logging SLF4J with log level DEBUG is used; you can therefore disable
 * performance logging by simply setting the log level for this class accordingly.
 *
 * @since 2.0.0
 * @author Matthias Langer
 */
@NotThreadSafe
public class PerformanceLogger
{
    /**
     * Path to an XML properties file that can be used to override the default settings of this class.
     * 
     * <p>
     *  This file loaded using {@link LocalResources}. Consult the users manual for details.
     * </p>
     * 
     * @since 2.1.0
     */
    public static final String DEFAULT_SETTINGS_PATH = "at/ipsquare/commons/core/util/performanceLogger.xml";
    
    /**
     * The property key that specifies the default {@link PerformanceLogFormatter} implementation to use.
     * 
     * @see #DEFAULT_SETTINGS_PATH
     * @since 2.1.0
     */
    public static final String DEFAULT_PERFORMANCE_LOG_FORMATTER_KEY = "defaultPerformanceLogFormatter";
    
    /**
     * The property key that specifies the default threshold to use.
     * 
     * @see #DEFAULT_SETTINGS_PATH
     * @since 2.1.0
     */
    public static final String DEFAULT_THRESHOLD_KEY = "defaultThreshold";
    
    private static final class DefaultSettings
    {
        final Class<? extends PerformanceLogFormatter> formatterClass;
        final long threshold;
        
        DefaultSettings(Class<? extends PerformanceLogFormatter> formatterClass, long threshold)
        {
            this.formatterClass = formatterClass;
            this.threshold = threshold;
        }
    }
    
    private static volatile DefaultSettings defaultSettings;

    private static final Logger log = LoggerFactory.getLogger(PerformanceLogger.class);
    
    private final Stopwatch stopwatch;
    private final long threshold;
    private final PerformanceLogFormatter logFormatter;
    private StackTraceElement from;
    
    static
    {
       reloadDefaults();
    }
    
    /**
     * This method reloads the default settings from {@link #DEFAULT_SETTINGS_PATH}.
     */
    static void reloadDefaults()
    {
        defaultSettings = loadDefaultSettings();
    }
    
    private static DefaultSettings loadDefaultSettings()
    {
        Class<? extends PerformanceLogFormatter> logFormatterClass = null;
        long threshold = 0;
        
        try
        {
            InputStream in = LocalResources.getStream(DEFAULT_SETTINGS_PATH);
            try
            {
                Properties props = new Properties();
                props.loadFromXML(in);
                String formatterName = props.getProperty(DEFAULT_PERFORMANCE_LOG_FORMATTER_KEY);
                logFormatterClass = loadFormatterClass(formatterName);
                
                String thresholdString = props.getProperty(DEFAULT_THRESHOLD_KEY);
                if(StringUtils.isNotEmpty(thresholdString))
                {
                    try
                    {
                        threshold = Long.parseLong(thresholdString);
                    }
                    catch(NumberFormatException e)
                    {
                        log.warn("Not a legal default threshold value: '" + thresholdString + "'.", e);
                    }
                }
            }
            finally
            {
                IOUtils.closeQuietly(in);
            }
        }
        catch(FileNotFoundException e)
        {
            // OK!
        }
        catch(IOException e)
        {
            log.warn("Error reading " + DEFAULT_SETTINGS_PATH + ".", e);
        }
        
        if(logFormatterClass == null)
            logFormatterClass = DefaultPerformanceLogFormatter.class;
        
        return new DefaultSettings(logFormatterClass, threshold);
    }

    private static Class<? extends PerformanceLogFormatter> loadFormatterClass(String name)
    {
        if(StringUtils.isEmpty(name))
            return DefaultPerformanceLogFormatter.class;
        
        Class<? extends PerformanceLogFormatter> ret;
        try
        {
            ret = Classes.forName(name, PerformanceLogFormatter.class);
        }
        catch(ClassCastException e)
        {
            log.warn(e.getMessage());
            ret = DefaultPerformanceLogFormatter.class;
        }
        catch(ClassNotFoundException e)
        {
            log.warn("Could not load default performance log formatter '" + name + "'.");
            ret = DefaultPerformanceLogFormatter.class;
        }
        
        return ret;
    }
    
    /**
     * Constructs a new {@link PerformanceLogger} with default settings.
     */
    public PerformanceLogger()
    {
        this(defaultSettings.threshold);
    }
    
    /**
     * Constructs a new {@link PerformanceLogger} a default {@link PerformanceLogFormatter} implementation.
     * 
     * @param threshold the threshold in ms for which the logger should generate any output.
     */
    public PerformanceLogger(long threshold)
    {
       this(threshold, getLogFormatter(null));
    }
    
    /**
     * Constructs a new {@link PerformanceLogger} with a default threshold.
     * 
     * @param logFormatter optional {@link PerformanceLogFormatter}
     * @since 2.1.0
     */
    public PerformanceLogger(PerformanceLogFormatter logFormatter)
    {
        this(defaultSettings.threshold, logFormatter);
    }
    
    /**
     * Constructs a new {@link PerformanceLogger}.
     * 
     * @param threshold the threshold in ms for which the logger should generate any output.
     * @param logFormatter optional {@link PerformanceLogFormatter}.
     * @since 2.1.0
     */
    public PerformanceLogger(long threshold, PerformanceLogFormatter logFormatter)
    {
        this.threshold = threshold;
        this.logFormatter = getLogFormatter(logFormatter);

        if (log.isDebugEnabled())
        {
            this.from = StackTrace.firstElementBelowClass();
            this.stopwatch = Stopwatch.createStarted();
        }
        else
        {
            this.from = null;
            this.stopwatch = Stopwatch.createUnstarted();
        }
    }
    
    private static PerformanceLogFormatter getLogFormatter(PerformanceLogFormatter logFormatter)
    {
        if(logFormatter != null)
            return logFormatter;
        return defaultPerformanceLogFormatter();
    }

    private static PerformanceLogFormatter defaultPerformanceLogFormatter()
    {
        Class<? extends PerformanceLogFormatter> clazz = defaultSettings.formatterClass;
        try
        {
            return clazz.newInstance();
        }
        catch(Exception e)
        {
            log.warn("Cannot create a new instance of " + clazz.getName() + ".", e);
            return new DefaultPerformanceLogFormatter();
        }
    }

    /**
     * See {@link #logElapsed(String)}.
     */
    public void logElapsed()
    {
        logElapsed(null);
    }
    
    /**
     * See {@link #logElapsedAndRestart(String)}.
     */
    public void logElapsedAndRestart()
    {
        logElapsedAndRestart(null);
    }
    
    /**
     * Logs the elapsed time if it is above the threshold.
     * 
     * @param msg an optional message.
     */
    public void logElapsed(String msg)
    {
        if(!log.isDebugEnabled() || !stopwatch.isRunning())
            return;
        
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        if(elapsed >= threshold)
        {
            StackTraceElement to = StackTrace.firstElementBelowClass();
            log.debug(logFormatter.format(from, to, elapsed, msg));
        }
    }
    
    /**
     * Logs the elapsed time if it is above the threshold and restarts the internal timer.
     * 
     * @param msg an optional message.
     */
    public void logElapsedAndRestart(String msg)
    {
        logElapsed(msg);
        restart();
    }
    
    /**
     * Restarts the internal timer.
     */
    public void restart()
    {
        if(!log.isDebugEnabled())
            return;
        
        from = StackTrace.firstElementBelowClass();
        stopwatch.reset().start();
    }

    /**
     * Executes the given block and logs its execution time.
     * 
     * @param threshold the threshold in ms for with the logger should generate any output.
     * @param msg an optional message.
     * @param block the block to execute.
     * @return the return value of the block.
     */
    public static <T> T timedExec(long threshold, String msg, Supplier<T> block)
    {
    	PerformanceLogger plog = new PerformanceLogger(threshold);
    	T res = block.get();
    	plog.logElapsed(msg);
    	return res;
    }


    /**
     * See {@link #timedExec(long, String, Runnable)} 
     */
    public static <T> T timedExec(String msg, Supplier<T> block)
    {
    	return timedExec(defaultSettings.threshold, msg, block);
    }

    /**
     * See {@link #timedExec(long, String, Runnable)} 
     */
    public static <T> T timedExec(long threshold, Supplier<T> block)
    {
    	return timedExec(threshold, null, block);
    }

    /**
     * See {@link #timedExec(long, String, Runnable)} 
     */
    public static <T> T timedExec(Supplier<T> block)
    {
    	return timedExec(defaultSettings.threshold, null, block);
    }
    
    /**
     * See {@link #timedExec(long, String, Runnable)} 
     */
    public static void timedExec(long threshold, String msg, Runnable block)
    {
    	PerformanceLogger plog = new PerformanceLogger(threshold);
    	block.run();
    	plog.logElapsed(msg);
    }

    /**
     * See {@link #timedExec(long, String, Runnable)} 
     */
    public static void timedExec(long threshold, Runnable block)
    {
    	timedExec(threshold, null, block);
    }

    /**
     * See {@link #timedExec(long, String, Runnable)} 
     */
    public static void timedExec(String msg, Runnable block)
    {
    	timedExec(defaultSettings.threshold, msg, block);
    }
    
    /**
     * See {@link #timedExec(long, String, Runnable)} 
     */
    public static void timedExec(Runnable block)
    {
    	timedExec(null, block);
    }
}
