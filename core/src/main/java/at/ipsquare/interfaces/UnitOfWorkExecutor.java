package at.ipsquare.interfaces;

/**
 * An API for executing {@link UnitOfWork} instances.
 * 
 * @since 1.1.0
 * @author Matthias Langer
 */
public interface UnitOfWorkExecutor
{
    /**
     * Executes the given {@link UnitOfWork} in an implementation defined context and returns its result.
     * 
     * @param work a unit of work.
     * @return the value returned by the given {@link UnitOfWork}.
     * @throws ExecutionError if {@link UnitOfWork#execute()} threw an exception.
     */
    <T> T executeUnitOfWork(UnitOfWork<T> work);
}
