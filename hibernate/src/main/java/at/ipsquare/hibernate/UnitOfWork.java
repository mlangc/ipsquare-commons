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
 * @author Matthias Langer
 * @since 1.0.0
 */
public interface UnitOfWork<T>
{
    /**
     * Performs some operations and returns the results.
     * 
     * @see HibernateRepository#executeUnitOfWork(UnitOfWork)
     * @since 1.0.0
     */
    T execute() throws Exception;
    
    /**
     * A name that is used for logging.
     * 
     * @return an optional name that is used for logging purposes.
     * @since 1.0.0
     */
    String getName();
}
