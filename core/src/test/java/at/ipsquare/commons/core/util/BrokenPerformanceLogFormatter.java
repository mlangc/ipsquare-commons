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
package at.ipsquare.commons.core.util;

/**
 * A broken {@link PerformanceLogFormatter} for testing.
 * 
 * @author Matthias Langer
 */
public class BrokenPerformanceLogFormatter implements PerformanceLogFormatter
{
    public BrokenPerformanceLogFormatter()
    {
        throw new RuntimeException("Damn");
    }
    
    @Override
    public String format(StackTraceElement from, StackTraceElement to, long millis, String message)
    {
        return null;
    }
}
