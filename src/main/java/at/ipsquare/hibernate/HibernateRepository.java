package at.ipsquare.hibernate;

/**
 * Wraps our Hibernate repository.
 * 
 * @author Matthias Langer
 */
public interface HibernateRepository
{
    /**
     * Executes the given {@link UnitOfWork} within a single transaction.
     * 
     * @param work a {@link UnitOfWork} that should be executed.
     * @return the result of the {@link UnitOfWork}.
     * 
     * @throws ExecutionError if {@link UnitOfWork#exectute(org.hibernate.Session)} throws an exception.
     */
    <T> T executeUnitOfWork(UnitOfWork<T> work);
}
