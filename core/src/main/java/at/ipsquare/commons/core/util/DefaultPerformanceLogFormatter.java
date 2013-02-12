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
package at.ipsquare.commons.core.util;

import java.util.ArrayDeque;
import java.util.Deque;

import net.jcip.annotations.ThreadSafe;

/**
 * Default {@link PerformanceLogFormatter} implementation.
 * 
 * @author Matthias Langer
 * @since 2.1.0
 */
@ThreadSafe
public final class DefaultPerformanceLogFormatter implements PerformanceLogFormatter
{
    @Override
    public String format(StackTraceElement from, StackTraceElement to, long elapsed, String msg)
    {
        StringBuilder sb = new StringBuilder();
        if(from != null && to != null)
        {
            Class<?> fromClass = StackTrace.associatedClass(from);
            Class<?> toClass = StackTrace.associatedClass(to);
            
            if(fromClass.equals(toClass))
            {
                sb.append(className(fromClass));
                if(from.getMethodName().equals(to.getMethodName()))
                {
                    sb.append(".")
                      .append(from.getMethodName())
                      .append("[")
                      .append(lineNumberToString(from))
                      .append("->")
                      .append(lineNumberToString(to))
                      .append("]");
                }
                else 
                {
                    sb.append("[")
                      .append(from.getMethodName())
                      .append(":")
                      .append(lineNumberToString(from))
                      .append("->")
                      .append(to.getMethodName())
                      .append(":")
                      .append(lineNumberToString(to))
                      .append("]");
                }
            }
            else
            {
                sb.append("[")
                  .append(className(fromClass))
                  .append(".")
                  .append(from.getMethodName())
                  .append(":")
                  .append(lineNumberToString(from))
                  .append("->")
                  .append(className(toClass))
                  .append(".")
                  .append(to.getMethodName())
                  .append(":")
                  .append(lineNumberToString(to))
                  .append("]");
            }
        }
        else
        {
            sb.append("[");
            StackTraceElement[] fromTo = { from, to };
            for(int i = 0; i < 2; ++i)
            {
                StackTraceElement elem = fromTo[i];
                if(elem == null)
                    sb.append("???");
                else
                {
                    Class<?> elemClass = StackTrace.associatedClass(elem);
                    sb.append(className(elemClass))
                      .append(".")
                      .append(from.getMethodName())
                      .append(":")
                      .append(lineNumberToString(elem));
                }
                
                if(i == 0)
                    sb.append("->");
            }
            sb.append("]");
        }
        
        sb.append(" ")
        .append(elapsed)
        .append("ms");
        
        if(msg != null)
        {
            sb.append(" <<")
              .append(msg)
              .append(">>");
        }
        
        return sb.toString();
    }
    
    private static String className(Class<?> clazz)
    {
        Deque<Class<?>> parents = new ArrayDeque<Class<?>>(2);
        parents.add(clazz);
        while(true)
        {
            Class<?> parent = parents.getFirst().getEnclosingClass();
            if(parent != null)
                parents.addFirst(parent);
            else
                break;
        }
        
        StringBuilder sb = new StringBuilder();
        for(Class<?> parent : parents)
        {
            if(sb.length() > 0)
                sb.append("$");
            sb.append(parent.getSimpleName());
        }
        return sb.toString();
    }
    
    private static String lineNumberToString(StackTraceElement elem)
    {
        int ln = elem.getLineNumber();
        return (ln > 0 ? String.valueOf(ln) : "?");
    }
}
