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

import java.util.concurrent.TimeUnit;

/**
 * A simple stopwatch.
 * <p>
 * Note that a {@link Stopwatch} is always running by design; there are no start and stop methods.
 * It is therefore not possible to misuse the API and there is no need to throw {@link IllegalStateException}s
 * as other stopwatch implementations do.
 * </p>
 * 
 * <p>
 * This class is not thread save.
 * </p>
 * 
 * @author Matthias Langer
 */
public final class Stopwatch
{
    private final Ticker ticker;
    private long start;
    
    /**
     * Constructs a new stopwatch and starts it immediately.
     */
    public Stopwatch()
    {
        this(SystemTicker.INSTANCE);
    }
    
    /**
     * Constructs a new stopwatch with a custom ticker.
     * 
     * @see #Stopwatch()
     */
    Stopwatch(Ticker ticker)
    {
        if(ticker == null)
            throw new NullPointerException("Missing ticker.");
        
        this.ticker = ticker;
        this.start = ticker.nanos();
    }
    
    /**
     * Returns the elapsed time in ms.
     */
    public long elapsed()
    {
        return elapsed(TimeUnit.MILLISECONDS);
    }
    
    /**
     * Returns the elapsed time in the desired unit.
     */
    public long elapsed(TimeUnit timeUnit)
    {
        long nanos = (ticker.nanos() - start);
        return timeUnit.convert(nanos, TimeUnit.NANOSECONDS);
    }
    
    /**
     * Returns the elapsed time in ms and restarts the stopwatch immediately.
     */
    public long elapsedAndRestart()
    {
        return elapsedAndRestart(TimeUnit.MILLISECONDS);
    }
    
    /**
     * Returns the elapsed time in the desired unit an restarts the stopwatch immediately.
     */
    public long elapsedAndRestart(TimeUnit timeUnit)
    {
        long ret = elapsed(timeUnit);
        restart();
        return ret;
    }
    
    /**
     * Restarts the stopwatch.
     */
    public void restart()
    {
        start = ticker.nanos();
    }
    
    /**
     * Returns a string representing the elapsed time.
     */
    @Override
    public String toString()
    {
        return elapsed() + "ms";
    }
}
