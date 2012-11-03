package at.ipsquare.commons.servlet;

import javax.servlet.Filter;
import javax.servlet.Servlet;

/**
 * This exception is thrown for severe {@link Servlet} and {@link Filter} related configuration errors.
 * 
 * <p/>
 * Errors of this type usually indicate problems within your web.xml file.
 * 
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
