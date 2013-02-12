package at.ipsquare.commons.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Mock {@link FilterChain} implementation for testing.
 * 
 * @author Matthias Langer
 */
public class UnitTestFilterChain implements FilterChain
{
    private ServletRequest request;
    private ServletResponse response;
    private int timesCalled;
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException
    {
        request = req;
        response = res;
        ++timesCalled;
        
        performDoFilter(req, res);
    }
    
    /**
     * Override this method if you want to do anything special in {@link #doFilter(ServletRequest, ServletResponse)}.
     */
    @SuppressWarnings("unused")
    protected void performDoFilter( ServletRequest req, ServletResponse res)
    {
        
    }
    
    /**
     * The last request that was passed to {@link #doFilter(ServletRequest, ServletResponse)}.
     */
    public ServletRequest getRequest()
    {
        return request;
    }
    
    /**
     * The last response that was passed to {@link #doFilter(ServletRequest, ServletResponse)}.
     */
    public ServletResponse getResponse()
    {
        return response;
    }
    
    /**
     * Returns true if {@link #doFilter(ServletRequest, ServletResponse)} was called at least once.
     */
    public boolean wasCalled()
    {
        return (timesCalled > 0);
    }
    
    /**
     * Returns the number of times {@link #doFilter(ServletRequest, ServletResponse)} was called on this object.
     */
    public int getTimesCalled()
    {
        return timesCalled;
    }
}
