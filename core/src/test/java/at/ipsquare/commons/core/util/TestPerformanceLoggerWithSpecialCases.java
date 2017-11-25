package at.ipsquare.commons.core.util;

import org.junit.AfterClass;
import org.junit.Test;

import static at.ipsquare.commons.core.util.PerformanceLogTestUtils.enablePerformanceLogs;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class TestPerformanceLoggerWithSpecialCases {
    @Test
    public void testDisableAndRenableLoggerWhileRunning() {
        String mark = mark(0);

        enablePerformanceLogs(true);
        PerformanceLogger plog = new PerformanceLogger(0);
        enablePerformanceLogs(false);
        plog.logElapsed(mark);
        assertThat(UnitTestAppender.logString(), not(containsString(mark)));
        plog.restart();
        enablePerformanceLogs(true);
        plog.logElapsed(mark);
        assertThat(UnitTestAppender.logString(), containsString(mark));
    }

    @Test
    public void testEnableAndDisableLoggerWhileRunning() {
        String mark = mark(1);

        enablePerformanceLogs(false);
        PerformanceLogger plog = new PerformanceLogger(0);
        enablePerformanceLogs(true);
        plog.logElapsed(mark);
        plog.logElapsedAndRestart(mark);
        assertThat(UnitTestAppender.logString(), not(containsString(mark)));
        plog.logElapsed(mark);
        assertThat(UnitTestAppender.logString(), containsString(mark));
    }

    @AfterClass
    public static void afterClass() {
        PerformanceLogTestUtils.enablePerformanceLogs(true);
    }

    private static String mark(int i) {
        return TestPerformanceLoggerWithSpecialCases.class.getSimpleName() + "-mark-" + i;
    }
}
