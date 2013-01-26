package at.ipsquare.interfaces;


/**
 * Interface for generating strings from various objects.
 * 
 * @since 1.0.0
 * @author Matthias Langer
 */
public interface StringGenerator<T>
{
    /**
     * Generates a string from the given object.
     */
    String generate(T obj);
}
