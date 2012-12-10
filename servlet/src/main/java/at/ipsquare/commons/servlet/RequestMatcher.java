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
package at.ipsquare.commons.servlet;

import javax.servlet.ServletRequest;

/**
 * Matcher for {@link ServletRequest}s.
 * 
 * <h4>Notes:</h4>
 * <ul>
 *  <li>Implementations are expected to be thread save.</li>
 * </ul>
 * 
 * @author Matthias Langer
 */
public interface RequestMatcher
{
    /**
     * Attempts to match the given request.
     */
    boolean matches(ServletRequest req);
}
