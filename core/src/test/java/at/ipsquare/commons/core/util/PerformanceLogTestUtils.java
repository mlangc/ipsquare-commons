package at.ipsquare.commons.core.util;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceLogTestUtils {
    public static void enablePerformanceLogs(boolean setting) {
        Logger logger = (Logger) LoggerFactory.getLogger(PerformanceLogger.class);
        Level level = setting ? Level.DEBUG : Level.INFO;
        logger.setLevel(level);
    }
}
