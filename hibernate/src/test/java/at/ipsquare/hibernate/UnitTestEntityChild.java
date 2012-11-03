package at.ipsquare.hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Entity for unit tests.
 * 
 * @author Matthias Langer
 */
@Entity
public class UnitTestEntityChild extends AbstractUnitTestEntity
{
    @Column(unique = true, nullable = false)
    private String name;
    
    @ManyToOne(cascade = CascadeType.ALL)
    private UnitTestEntityParent parent;
    
    public UnitTestEntityChild()
    {
        
    }
    
    public UnitTestEntityChild(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    
    public UnitTestEntityParent getParent()
    {
        return parent;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setParent(UnitTestEntityParent parent)
    {
        this.parent = parent;
    }
}
