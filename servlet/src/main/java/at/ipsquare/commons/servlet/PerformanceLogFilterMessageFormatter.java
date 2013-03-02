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
package at.ipsquare.commons.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Formats messages for {@link PerformanceLogFilter}.
 * 
 * @since 2.1.0
 * @author Matthias Langer
 */
public interface PerformanceLogFilterMessageFormatter
{
    /**
     * Generates a message according to the given input.
     * 
     * @param req the associated {@link ServletRequest}.
     * @param res the associated {@link ServletResponse}.
     * @param th an optional error.
     * @return a message.
     */
    String format(ServletRequest req, ServletResponse res, Throwable th);
}
