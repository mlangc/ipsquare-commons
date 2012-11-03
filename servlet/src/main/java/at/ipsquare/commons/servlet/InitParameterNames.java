/**
 * Copyright (C) 2012 Matthias Langer <mlangc@gmx.at>
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

/**
 * Universally useful initialization parameter names for servlets and filters.
 * 
 * @author Matthias Langer
 */
public final class InitParameterNames
{
    /**
     * Suggested initialization parameter name to be used in connection with {@link PathPatternRequestMatcher}.
     */
    public static final String INCLUDE_PATH_PATTERN = "includePathPattern";
    
    /**
     * See {@link #INCLUDE_PATH_PATTERN}.
     */
    public static final String EXCLUDE_PATH_PATTERN = "excludePathPattern";
    
    public InitParameterNames()
    {
        
    }
}
