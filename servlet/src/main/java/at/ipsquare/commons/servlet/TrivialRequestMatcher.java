package at.ipsquare.commons.servlet;

import javax.servlet.ServletRequest;

/**
 * Trivial request matcher implementations that might be useful.
 * 
 * @author Matthias Langer
 */
public enum TrivialRequestMatcher implements RequestMatcher
{
    /**
     * A {@link RequestMatcher} that matches anything.
     */
    ANYTHING, 
    
    /**
     * A {@link RequestMatcher} that matches nothing.
     */
    NOTHING;
    
    @Override
    public boolean matches(ServletRequest req)
    {
        return (this == ANYTHING);
    }
}
