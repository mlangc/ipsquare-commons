/**
 * Copyright (C) 2013 Matthias Langer
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
package at.ipsquare.commons.core.interfaces;



/**
 * An interface that represents a single Unit of Work.
 * 
 * <h4>Notes:</h4>
 * <ul>
 * <li>
 *  You should normally extend {@link AbstractUnitOfWork} instead of implementing this interface directly.
 * </li>
 * </ul>
 * 
 * @see AbstractUnitOfWork
 * @since 2.0.0
 * @author Matthias Langer
 */
public interface UnitOfWork<T>
{
    /**
     * Performs some operations and returns the results.
     */
    T execute() throws Exception;
    
    /**
     * A name (most likely for logging purposes).
     */
    String getName();
}
