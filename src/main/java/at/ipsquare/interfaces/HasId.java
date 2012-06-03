package at.ipsquare.interfaces;

/**
 * Interface for types (usually entities) that have some kind of ID.
 * 
 * @author Matthias Langer
 */
public interface HasId<T>
{
    /**
     * Returns the ID.
     */
    T getId();
}
