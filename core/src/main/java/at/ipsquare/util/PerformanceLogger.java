package at.ipsquare.util;

import net.jcip.annotations.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * </p>
 * 
 * @author Matthias Langer
 */
@NotThreadSafe
public class PerformanceLogger
{
    private static final Logger log = LoggerFactory.getLogger(PerformanceLogger.class);
    
    private final Stopwatch stopwatch;
    private final long threshold;
    
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
        this.threshold = threshold;
        this.stopwatch = new Stopwatch();
    }
    
    /**
     * See {@link #logElapsed(String)}.
     */
    public void logElapsed()
    {
        log.debug(" asdf§");
    }
    
    /**
     * See {@link #logElapsedAndRestart(String)}.
     */
    public void logElapsedAndRestart()
    {
        
    }
    
    /**
     * Logs the elapsed time if it is above the threshold.
     * 
     * @param msg an optional message.
     */
    public void logElapsed(String msg)
    {
        
    }
    
    /**
     * Logs the elapsed time if it is above the threshold and restarts the internal timer.
     * 
     * @param msg an optional message.
     */
    public void logElapsedAndRestart(String msg)
    {
        
    }
    
    /**
     * Restarts the internal timer.
     */
    public void restart()
    {
        
    }
}
