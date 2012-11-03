package at.ipsquare.hibernate;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import at.ipsquare.interfaces.HasId;

/**
 * Abstract base class for all unit test entities.
 * 
 * @author Matthias Langer
 */
@MappedSuperclass
public class AbstractUnitTestEntity implements HasId<Long>
{
    @Id
    @GeneratedValue
    private Long id;
    
    public Long getId()
    {
        return id;
    }
}
