package at.ipsquare.hibernate;

import org.hibernate.Session;

/**
 * An interface that represents a single Unit of Work.
 * 
 * <h4>Notes:</h4>
 * <ul>
 * <li>
 *  You should normally extend {@link AbstractUnitOfWork} instead of implementing this interface directly.
 * </li>
 * </ul>
 * 
 * @see AbstractUnitOfWork
 * @author Matthias Langer
 */
public interface UnitOfWork<T>
{
    /**
     * Performs some operations and returns the results.
     */
    T exectute(Session session) throws Exception;
    
    /**
     * A name that is used for logging.
     * 
     * @return an optional name that is used for logging purposes.
     */
    String getName();
}
