package at.ipsquare.util;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * Tests for {@link Stopwatch}.
 * 
 * @author Matthias Langer
 */
public class TestStopwatch
{
    private static class TestTicker implements Ticker
    {
        long nanos;
        
        public long nanos()
        {
            return nanos;
        }
    }
    
    /**
     * Meant to cover the full functionality of the {@link Stopwatch} class.
     */
    @Test
    public void test()
    {
        TestTicker ticker = new TestTicker();
        Stopwatch sw = new Stopwatch(ticker);
        
        assertThat(sw.elapsed(), equalTo(0L));
        assertThat(sw.elapsed(TimeUnit.SECONDS), equalTo(0L));
        
        ticker.nanos += 1000L;
        assertThat(sw.elapsed(TimeUnit.NANOSECONDS), equalTo(1000L));
        assertThat(sw.elapsed(TimeUnit.MICROSECONDS), equalTo(1L));
        assertThat(sw.elapsedAndRestart(), equalTo(0L));
        
        ticker.nanos += 1000L * 1000L * 1000L;
        assertThat(sw.elapsed(), equalTo(1000L));
        assertThat(sw.elapsed(TimeUnit.SECONDS), equalTo(1L));
        
        sw.restart();
        assertThat(sw.elapsed(), equalTo(0L));
    }
}
