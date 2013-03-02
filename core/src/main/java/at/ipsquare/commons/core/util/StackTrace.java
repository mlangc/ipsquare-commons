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
package at.ipsquare.commons.core.util;

import java.util.Arrays;

/**
 * Utility methods for dealing with stack traces.
 * 
 * @since 2.0.0
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
        int first = firstElemBelowThisClass(elems);
        return Arrays.copyOfRange(elems, first, elems.length);
    }
    
    /**
     * Returns the first element in the stack that does not originate from the class of the caller.
     */
    public static StackTraceElement firstElementBelowClass()
    {
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        int first = firstElemBelowThisClass(elems);
        Class<?> callerClass = associatedClass(elems[first]);
        for(int i = first + 1; i < elems.length; ++i)
        {
            StackTraceElement current = elems[i];
            if(!callerClass.equals(associatedClass(current)))
                return current;
        }
        return null;
    }
    
    private static int firstElemBelowThisClass(StackTraceElement[] elems)
    {
        boolean seenThisClass = false;
        int i = 0;
        for(; i < elems.length; ++i)
        {
            StackTraceElement elem = elems[i];
            Class<?> clazz = associatedClass(elem);
            if(StackTrace.class.equals(clazz))
                seenThisClass = true;
            else if(seenThisClass)
                break;
        }
        return i;
    }
    
    /**
     * Returns the class associated with the given {@link StackTraceElement}.
     */
    static Class<?> associatedClass(StackTraceElement elem)
    {
        try
        {
            return Class.forName(elem.getClassName());
        }
        catch(ClassNotFoundException e)
        {
            throw new RuntimeException("Could not find class from StackTraceElement; trouble awaits.", e);
        }
    }

    private StackTrace()
    {
        
    }
}
