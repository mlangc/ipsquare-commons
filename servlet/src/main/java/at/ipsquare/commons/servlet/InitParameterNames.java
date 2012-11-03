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
