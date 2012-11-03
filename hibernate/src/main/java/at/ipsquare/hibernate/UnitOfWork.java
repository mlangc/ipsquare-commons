package at.ipsquare.hibernate;


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
 * @since 1.0.0
 */
public interface UnitOfWork<T>
{
    /**
     * Performs some operations and returns the results.
     * 
     * @see HibernateRepository#executeUnitOfWork(UnitOfWork)
     * @since 1.0.0
     */
    T execute() throws Exception;
    
    /**
     * A name that is used for logging.
     * 
     * @return an optional name that is used for logging purposes.
     * @since 1.0.0
     */
    String getName();
}
