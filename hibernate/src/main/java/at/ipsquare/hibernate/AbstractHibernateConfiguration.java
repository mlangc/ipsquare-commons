package at.ipsquare.hibernate;

import java.util.Collections;
import java.util.Map;

/**
 * An abstract {@link HibernateConfiguration} implementation, that requires clients only to supply values where no reasonable default can be found.
 * 
 * @since 1.0.0
 * @author Matthias Langer
 */
public abstract class AbstractHibernateConfiguration implements HibernateConfiguration
{
    public HibernateHbm2dllAuto getHbm2dllAuto()
    {
        return HibernateHbm2dllAuto.VALIDATE;
    }
    
    public Map<String, String> getProperties()
    {
        return Collections.emptyMap();
    }
}
