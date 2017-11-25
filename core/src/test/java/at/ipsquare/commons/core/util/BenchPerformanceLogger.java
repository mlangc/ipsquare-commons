package at.ipsquare.commons.core.util;

import ch.qos.logback.classic.Logger;
import org.openjdk.jmh.annotations.*;
import org.slf4j.LoggerFactory;

@State(Scope.Benchmark)
public abstract class BenchPerformanceLogger {
    protected abstract boolean performanceLogsEnabled();

    @Setup(Level.Trial)
    public void disablePerformanceLogs() {
        Logger logger = (Logger) LoggerFactory.getLogger(PerformanceLogger.class);

        if (!performanceLogsEnabled()) {
            System.out.println("lyu");
            logger.setLevel(ch.qos.logback.classic.Level.INFO);
        } else {
            System.out.println("Hello");
            logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
        }
    }

    @Benchmark
    @Fork(1)
    public void timedExecBelowThreshold() {
        PerformanceLogger.timedExec(100, () -> { });
    }
}
