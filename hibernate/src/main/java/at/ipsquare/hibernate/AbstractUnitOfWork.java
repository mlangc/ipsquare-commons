/**
 * Copyright (C) 2012 Matthias Langer <mlangc@gmx.at>
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
