package at.ipsquare.hibernate;

/**
 * Thrown by {@link HibernateRepository} if the executed {@link UnitOfWork} throws an {@link Exception}.
 * 
 * @author Matthias Langer
 */
public class ExecutionError extends RuntimeException
{
    public ExecutionError(String message)
    {
        super(message);
    }

    public ExecutionError(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ExecutionError(Throwable cause)
    {
        super(cause);
    }
}
