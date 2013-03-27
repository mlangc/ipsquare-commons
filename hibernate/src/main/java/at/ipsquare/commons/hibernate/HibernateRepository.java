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
package at.ipsquare.commons.hibernate;

import java.io.Closeable;

import org.hibernate.Session;
import org.hibernate.Transaction;

import at.ipsquare.commons.core.interfaces.ExecutionError;
import at.ipsquare.commons.core.interfaces.UnitOfWork;
import at.ipsquare.commons.core.interfaces.UnitOfWorkExecutor;

/**
 * Wraps our Hibernate repository.
 * 
 * <h4>Notes:</h4>
 * <ul>
 *  <li>Implementations of this interface can be expected to be thread save.</li>
 * </ul>
 * 
 * @author Matthias Langer
 * @since 2.0.0
 */
public interface HibernateRepository extends UnitOfWorkExecutor, Closeable
{
    /**
     * Executes the given {@link UnitOfWork} within a single transaction.
     * 
     * <h4>Notes:</h4>
     *  <ul>
     *      <li>If you need more fine grained transaction management, you can do so by manipulating {@link #currentSession()} accordingly.</li>
     *      <li>Implementations of this class can be expected to be thread save; executing multiple units of work concurrently is save.</li>
     *      <li>
     *          It is not an error to execute a {@link UnitOfWork} while another {@link UnitOfWork} is already executing. However note that
     *          when doing so the {@link Session} or the current {@link Transaction} is not modified.
     *      </li>
     *  </ul>
     * 
     * @param work a {@link UnitOfWork} that should be executed.
     * @return the result of the {@link UnitOfWork}.
     * 
     * @throws ExecutionError if {@link UnitOfWork#execute()} throws an exception.
     */
    <T> T executeUnitOfWork(UnitOfWork<T> work);
    
    /**
     * Returns the currently open session for this thread.
     * 
     * @throws IllegalStateException if no {@link UnitOfWork} is currently executing in this thread.
     */
    Session currentSession();
    
    /**
     * Closes the repository (all acquired resources are released).
     */
    @Override
    void close();
    
    /**
     * Returns true if the repository is closed.
     */
    boolean isClosed();
}
