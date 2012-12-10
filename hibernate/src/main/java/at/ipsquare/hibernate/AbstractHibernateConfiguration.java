/**
 * Copyright (C) 2012 IP SQUARE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
