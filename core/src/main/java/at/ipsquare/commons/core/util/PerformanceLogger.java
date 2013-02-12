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
package at.ipsquare.commons.core.util;

import java.util.concurrent.TimeUnit;

import net.jcip.annotations.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

/**
 * A simple performance logger.
 * 
 * <p>
 * This class is meant to be used like this:
 * <pre>
 * {@code
 * //...
 * PerformanceLogger plog = new PerformanceLogger();
 * doWork();
 * plog.logElapsedAndRestart();
 * doSomeMoreWork();
 * plog.logElapsed();
 * //...}
 * </pre>
 * 
 * For logging SLF4J with log level DEBUG is used; you can therefore disable
 * performance logging by simply setting the log level for this class accordingly.
 * </p>
 * 
 * @since 2.0.0
 * @author Matthias Langer
 */
@NotThreadSafe
public class PerformanceLogger
{
    private static final Logger log = LoggerFactory.getLogger(PerformanceLogger.class);
    
    private final Stopwatch stopwatch;
    private final long threshold;
    private final PerformanceLogFormatter logFormatter;
    private StackTraceElement from;
    
    /**
     * Constructs a new {@link PerformanceLogger} with no threshold.
     */
    public PerformanceLogger()
    {
        this(0);
    }
    
    /**
     * Constructs a new {@link PerformanceLogger}.
     * 
     * @param threshold the threshold in ms for which the logger should generate any output.
     */
    public PerformanceLogger(long threshold)
    {
        this.from = StackTrace.firstElementBelowClass();
        this.threshold = threshold;
        this.stopwatch = new Stopwatch().start();
        this.logFormatter = logFormatter();
    }
    
    private static PerformanceLogFormatter logFormatter()
    {
        return new DefaultPerformanceLogFormatter();
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
        if(!log.isDebugEnabled())
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
}
