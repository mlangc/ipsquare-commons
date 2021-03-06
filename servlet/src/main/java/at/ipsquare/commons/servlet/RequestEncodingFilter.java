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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * A filter that consistently sets the request encoding.
 * 
 * @since 2.1.0
 * @author Matthias Langer
 */
public class RequestEncodingFilter implements Filter
{
    private String encoding;
    private boolean force;
    private RequestMatcher matcher;
    
    /**
     * The init parameter name for the encoding (defaults to UTF-8).
     */
    public static final String INIT_PARAM_NAME_ENCODING = "encoding";
    
    /**
     * The init parameter name for the force encoding property (defaults to false).
     */
    public static final String INIT_PARAM_NAME_FORCE = "forceEncoding";

    @Override
    public void init(FilterConfig cfg) throws ServletException
    {
        encoding = cfg.getInitParameter(INIT_PARAM_NAME_ENCODING);
        force = Boolean.parseBoolean(cfg.getInitParameter(INIT_PARAM_NAME_FORCE));
        matcher = PathPatternRequestMatcher.fromFilterConfig(cfg);
        
        if(encoding == null)
            encoding = "UTF-8";
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        if(matcher.matches(req))
        {
            if(force || req.getCharacterEncoding() == null)
                req.setCharacterEncoding(encoding);
        }
        
        chain.doFilter(req, res);
    }
    
    @Override
    public void destroy()
    {
        
    }
}
