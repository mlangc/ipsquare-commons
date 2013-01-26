package at.ipsquare.interfaces;


/**
 * Interface for generating strings from various objects.
 * 
 * @author Matthias Langer
 */
public interface StringGenerator<T>
{
    /**
     * Generates a string from the given object.
     */
    String generate(T obj);
}
