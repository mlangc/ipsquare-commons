package at.ipsquare.commons.core.util;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;

public class BenchEnabledPerformanceLogger extends BenchPerformanceLogger {
    @Override
    protected boolean performanceLogsEnabled() {
        return true;
    }

    public static void main(String[] args) throws IOException, RunnerException {
        Main.main(args);
    }
}
