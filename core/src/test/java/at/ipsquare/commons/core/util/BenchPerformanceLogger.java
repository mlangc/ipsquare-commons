package at.ipsquare.commons.core.util;

import ch.qos.logback.classic.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@State(Scope.Benchmark)
public abstract class BenchPerformanceLogger {
    protected abstract boolean performanceLogsEnabled();

    @Setup(Level.Trial)
    public void configureLogger() {
        Logger logger = (Logger) LoggerFactory.getLogger(PerformanceLogger.class);
        UnitTestAppender.enabled = false;

        if (!performanceLogsEnabled()) {
            logger.setLevel(ch.qos.logback.classic.Level.INFO);
        } else {
            logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        UnitTestAppender.enabled = true;
    }

    @Benchmark
    public void timedExecBelowThreshold() {
        PerformanceLogger.timedExec(100, () -> { });
    }

    @Benchmark
    public void timedExecAboveThreshold() {
        PerformanceLogger.timedExec(0, () -> { });
    }

    protected static void runBenchmark(Class<?> clazz) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(Pattern.quote(clazz.getName()))
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.SECONDS)
                .warmupIterations(3)
                .measurementIterations(3)
                .threads(1)
                .forks(1)
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }
}
