package at.ipsquare.hibernate;

import org.apache.commons.lang3.StringUtils;

/**
 * Abstract base class for {@link UnitOfWork} implementations.
 * 
 * @since 1.0.0
 * @author Matthias Langer
 */
public abstract class AbstractUnitOfWork<T> implements UnitOfWork<T>
{
    public String getName()
    {
        return null;
    }
    
    @Override
    public String toString()
    {
        return "UnitOfWork[" + (StringUtils.isBlank(getName()) ? "?UNNAMED?" : getName()) + "]";
    }
}
