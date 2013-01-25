package at.ipsquare.util;

import java.util.ArrayDeque;
import java.util.Deque;
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
 * @since 1.1.0
 * @author Matthias Langer
 */
@NotThreadSafe
public class PerformanceLogger
{
    private static final Logger log = LoggerFactory.getLogger(PerformanceLogger.class);
    
    private final Stopwatch stopwatch;
    private final long threshold;
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
            log.debug(formatLogMessage(from, to, msg, elapsed));
        }
    }
    
    private static String formatLogMessage(StackTraceElement from, StackTraceElement to, String msg, long elapsed)
    {
        StringBuilder sb = new StringBuilder();
        if(from != null && to != null)
        {
            Class<?> fromClass = StackTrace.associatedClass(from);
            Class<?> toClass = StackTrace.associatedClass(to);
            
            if(fromClass.equals(toClass))
            {
                sb.append(className(fromClass));
                if(from.getMethodName().equals(to.getMethodName()))
                {
                    sb.append(".")
                      .append(from.getMethodName())
                      .append("[")
                      .append(lineNumberToString(from))
                      .append("->")
                      .append(lineNumberToString(to))
                      .append("]");
                }
                else 
                {
                    sb.append("[")
                      .append(from.getMethodName())
                      .append(":")
                      .append(lineNumberToString(from))
                      .append("->")
                      .append(to.getMethodName())
                      .append(":")
                      .append(lineNumberToString(to))
                      .append("]");
                }
            }
            else
            {
                sb.append("[")
                  .append(className(fromClass))
                  .append(".")
                  .append(from.getMethodName())
                  .append(":")
                  .append(lineNumberToString(from))
                  .append("->")
                  .append(className(toClass))
                  .append(".")
                  .append(to.getMethodName())
                  .append(":")
                  .append(lineNumberToString(to))
                  .append("]");
            }
        }
        else
        {
            sb.append("[");
            StackTraceElement[] fromTo = { from, to };
            for(int i = 0; i < 2; ++i)
            {
                StackTraceElement elem = fromTo[i];
                if(elem == null)
                    sb.append("???");
                else
                {
                    Class<?> elemClass = StackTrace.associatedClass(elem);
                    sb.append(className(elemClass))
                      .append(".")
                      .append(from.getMethodName())
                      .append(":")
                      .append(lineNumberToString(elem));
                }
                
                if(i == 0)
                    sb.append("->");
            }
            sb.append("]");
        }
        
        if(msg != null)
        {
            sb.append(" <<")
              .append(msg)
              .append(">>");
        }
        
        sb.append(" ")
          .append(elapsed)
          .append("ms");
        
        return sb.toString();
    }
    
    private static String className(Class<?> clazz)
    {
        Deque<Class<?>> parents = new ArrayDeque<Class<?>>(2);
        parents.add(clazz);
        while(true)
        {
            Class<?> parent = parents.getFirst().getEnclosingClass();
            if(parent != null)
                parents.addFirst(parent);
            else
                break;
        }
        
        StringBuilder sb = new StringBuilder();
        for(Class<?> parent : parents)
        {
            if(sb.length() > 0)
                sb.append("$");
            sb.append(parent.getSimpleName());
        }
        return sb.toString();
    }
    
    private static String lineNumberToString(StackTraceElement elem)
    {
        int ln = elem.getLineNumber();
        return (ln > 0 ? String.valueOf(ln) : "?");
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
