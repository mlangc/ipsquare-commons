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
