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
package at.ipsquare.commons.core.interfaces;

/**
 * An API for executing {@link UnitOfWork} instances.
 * 
 * @since 2.0.0
 * @author Matthias Langer
 */
public interface UnitOfWorkExecutor
{
    /**
     * Executes the given {@link UnitOfWork} in an implementation defined context and returns its result.
     * 
     * @param work a unit of work.
     * @return the value returned by the given {@link UnitOfWork}.
     * @throws ExecutionError if {@link UnitOfWork#execute()} threw an exception.
     */
    <T> T executeUnitOfWork(UnitOfWork<T> work);
}
