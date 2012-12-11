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
package at.ipsquare.util;

import java.util.Arrays;

/**
 * Utility methods for dealing with stack traces.
 * 
 * @author Matthias Langer
 */
public final class StackTrace
{
    /**
     * Returns the current stack trace, starting with the invocation of this method (unlike {@link Thread#getStackTrace()}).
     * 
     * <h4>Note:</h4>
     *  This method might return an empty array if the VM has no stack trace information
     *  concerning the current thread. With a decent VM however, this should never happen.
     */
    public static StackTraceElement[] get()
    {
        StackTraceElement[] elems =  Thread.currentThread().getStackTrace();
        
        boolean seenThisClass = false;
        int i = 0;
        for(; i < elems.length; ++i)
        {
            StackTraceElement elem = elems[i];
            try
            {
                Class<?> clazz = Class.forName(elem.getClassName());
                if(StackTrace.class.equals(clazz))
                    seenThisClass = true;
                else if(seenThisClass)
                    break;
            }
            catch(ClassNotFoundException e)
            {
                throw new RuntimeException("Could not find class from stack trace; trouble awaits!", e);
            }
        }
        
        return Arrays.copyOfRange(elems, i, elems.length);
    }
    
    private StackTrace()
    {
        
    }
}
