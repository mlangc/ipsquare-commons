/**
 * Copyright (C) 2013 Matthias Langer
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
 * An interface for formatting log messages.
 * 
 * @see PerformanceLogger
 * @author Matthias Langer
 * @since 2.1.0
 */
public interface PerformanceLogFormatter
{
    /**
     * Formats a log message according to the given input.
     * 
     * @param from the point where performance logging was started (can be null in theory).
     * @param to the point where performance logging was stopped (can be null in theory).
     * @param millis the number of elapsed milliseconds.
     * @param message an optional message.
     * @return a string for logging.
     */
    String format(StackTraceElement from, StackTraceElement to, long millis, String message);
}
