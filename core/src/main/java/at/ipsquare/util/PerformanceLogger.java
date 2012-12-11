package at.ipsquare.util;


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
public class PerformanceLogger
{
    /**
     * Constructs a new {@link PerformanceLogger} with no threshold.
     */
    public PerformanceLogger()
    {
        
    }
    
    /**
     * Constructs a new {@link PerformanceLogger}.
     * 
     * @param threshold the threshold in ms for which the logger should generate any output.
     */
    public PerformanceLogger(long threshold)
    {
        
    }
    
    /**
     * See {@link #logElapsed(String)}.
     */
    public void logElapsed()
    {
        
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
