package at.ipsquare.hibernate;

/**
 * This enum represents the 'hibernate.hbm2ddl.auto' Hibernate configuration property.
 * 
 * @since 1.0.0
 */
public enum HibernateHbm2dllAuto
{
    VALIDATE("validate"), UPDATE("update"), CREATE("create"), CREATE_DROP("create-drop");
    
    private final String value;
    
    private HibernateHbm2dllAuto(String value)
    {
        if(value == null)
            throw new NullPointerException();
        
        this.value = value;
    }
    
    @Override
    public String toString()
    {
        return value;
    }
}
