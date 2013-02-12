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

import net.jcip.annotations.ThreadSafe;

/**
 * Default {@link PerformanceLogFormatter} implementation.
 * 
 * @author Matthias Langer
 * @since 2.1.0
 */
@ThreadSafe
public final class DefaultPerformanceLogFormatter implements PerformanceLogFormatter
{
    @Override
    public String format(StackTraceElement from, StackTraceElement to, long millis, String message)
    {
        return null;
    }
}
