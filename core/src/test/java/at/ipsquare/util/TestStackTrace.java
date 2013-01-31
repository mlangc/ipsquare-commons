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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import at.ipsquare.commons.core.util.StackTrace;

/**
 * Tests for {@link StackTrace}.
 * 
 * @author Matthias Langer
 */
public class TestStackTrace
{
    /**
     * Tests {@link StackTrace#get()}.
     */
    @Test
    public void testGet() throws ClassNotFoundException
    {
        StackTraceElement[] stackTrace = StackTrace.get();
        assertThat(stackTrace.length, not(0));
        
        StackTraceElement first = stackTrace[0];
        assertEquals(getClass(), Class.forName(first.getClassName()));
        assertThat(first.getMethodName(), equalTo("testGet"));
    }
    
    /**
     * Tests {@link StackTrace#firstElementBelowClass()}.
     */
    @Test
    public void testFirstElementBelowClass() throws ClassNotFoundException
    {
        class TestClass
        {
            StackTraceElement elem;
            
            TestClass()
            {
                elem = StackTrace.firstElementBelowClass();
            }
        }
        
        StackTraceElement elem = new TestClass().elem;
        assertEquals(getClass(), Class.forName(elem.getClassName()));
    }
}
