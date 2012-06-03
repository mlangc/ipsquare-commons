package at.ipsquare.hibernate;

/**
 * Abstract base class for {@link UnitOfWork} implementations.
 * 
 * @author Matthias Langer
 */
public abstract class AbstractUnitOfWork<T> implements UnitOfWork<T>
{
    public String getName()
    {
        return null;
    }
}
