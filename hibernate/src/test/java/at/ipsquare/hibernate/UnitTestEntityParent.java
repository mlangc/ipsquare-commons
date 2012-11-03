package at.ipsquare.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * Entity for unit tests.
 * 
 * @author Matthias Langer
 */
@Entity
public class UnitTestEntityParent extends AbstractUnitTestEntity
{
    @Column(unique = true, nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnitTestEntityChild> children = new ArrayList<UnitTestEntityChild>(2);
    
    public UnitTestEntityParent()
    {
        
    }
    
    public UnitTestEntityParent(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public List<UnitTestEntityChild> getChildren()
    {
        return children;
    }
}
