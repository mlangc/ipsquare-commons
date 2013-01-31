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
package at.ipsquare.commons.interfaces;


/**
 * Thrown by {@link UnitOfWorkExecutor}s if executing {@link UnitOfWork#execute()} fails.
 * 
 * @since 1.1.0
 * @author Matthias Langer
 */
public class ExecutionError extends RuntimeException
{
    public ExecutionError(String message)
    {
        super(message);
    }

    public ExecutionError(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ExecutionError(Throwable cause)
    {
        super(cause);
    }
}
