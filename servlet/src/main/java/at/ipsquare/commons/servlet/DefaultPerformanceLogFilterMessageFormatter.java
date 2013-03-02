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
import javax.servlet.http.HttpServletRequest;

import net.jcip.annotations.Immutable;

import org.apache.commons.lang3.StringUtils;

/**
 * Default {@link PerformanceLogFilterMessageFormatter} implementation.
 *
 * @since 2.1.0
 * @author Matthias Langer
 */
@Immutable
public class DefaultPerformanceLogFilterMessageFormatter implements PerformanceLogFilterMessageFormatter
{
    @Override
    public String format(ServletRequest request, ServletResponse res, Throwable th)
    {
        if(!(request instanceof HttpServletRequest))
            return "" + request;
        
        HttpServletRequest req = (HttpServletRequest) request;
        StringBuilder sb = new StringBuilder()
            .append(req.getMethod())
            .append(errorString(th))
            .append(" ");
        int lenWithMethodAndSpace = sb.length();
        
        if(req.getServletPath() != null)
            sb.append(req.getServletPath());
        if(req.getPathInfo() != null)
            sb.append(req.getPathInfo());
        if(StringUtils.isNotEmpty(req.getQueryString()))
            sb.append("?").append(req.getQueryString());
        
        if(sb.length() == lenWithMethodAndSpace)
            sb.setLength(lenWithMethodAndSpace - 1);
        return sb.toString();
    }
    
    private static String errorString(Throwable th)
    {
        if(th == null)
            return "";
        
        return new StringBuilder()
            .append("(!")
            .append(th.getClass().getSimpleName())
            .append("!)")
            .toString();
    }
}
