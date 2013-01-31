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

import javax.servlet.Filter;
import javax.servlet.Servlet;

/**
 * This exception is thrown for severe {@link Servlet} and {@link Filter} related configuration errors.
 * 
 * <p/>
 * Errors of this type usually indicate problems within your web.xml file.
 * 
 * @since 2.0.0
 * @author Matthias Langer
 */
public class ServletConfigurationError extends RuntimeException
{
    public ServletConfigurationError(String message, Throwable cause)
    {
        super(enhanceErrorMessage(message), cause);
    }

    public ServletConfigurationError(String message)
    {
        super(enhanceErrorMessage(message));
    }
    
    private static String enhanceErrorMessage(String message)
    {
        return message  + " Please check your web.xml.";
    }
}
