package at.ipsquare.commons.core.util;

/**
 * {@link PerformanceLogFormatter} for unit tests.
 * 
 * @author Matthias Langer
 */
public class UnitTestPeformanceLogFormatter implements PerformanceLogFormatter
{
    private final DefaultPerformanceLogFormatter defaultFormatter = new DefaultPerformanceLogFormatter();
    
    public static final String PREFIX = "***UnitTest***|";
    
    @Override
    public String format(StackTraceElement from, StackTraceElement to, long millis, String message)
    {
        return PREFIX.concat(defaultFormatter.format(from, to, millis, message));
    }
}
