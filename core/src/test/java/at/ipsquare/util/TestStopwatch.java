/**
 * Copyright (C) 2012 IP SQUARE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
