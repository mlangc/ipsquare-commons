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

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import at.ipsquare.commons.core.util.DefaultPerformanceLogFormatter;
import at.ipsquare.commons.core.util.PerformanceLogFormatter;

/**
 * Tests for {@link DefaultPerformanceLogFormatter}.
 * 
 * @author Matthias Langer
 */
public class TestDefaultPerformanceLogFormatter
{
    private static class SomeClass
    {
       static final String NAME = SomeClass.class.getName();
       static final String SIMPLE_NAME = SomeClass.class.getSimpleName();
    }
    
    private static class SomeOtherClass
    {
        static final String NAME = SomeOtherClass.class.getName();
        static final String SIMPLE_NAME = SomeOtherClass.class.getSimpleName();
    }
    
    @Test
    public void testFormat()
    {
        testFormat(
                null, 
                null, 
                111, 
                null, 
                "111", "?");
        
        testFormat(
                new StackTraceElement(SomeClass.NAME, "foo", null, -1),
                null,
                111,
                null,
                "111", "?", "foo");
        
        testFormat(
                new StackTraceElement(SomeClass.NAME, "foo", null, -2),
                null,
                111,
                "test",
                "111", "test", "?", "foo");
        
        testFormat(
                new StackTraceElement(SomeClass.NAME, "bar", "From.java", -1),
                new StackTraceElement(SomeOtherClass.NAME, "to", "To.java", 33),
                66,
                null,
                SomeClass.SIMPLE_NAME, SomeOtherClass.SIMPLE_NAME, "to", "33", "66", "?", "bar");
        
        testFormat(
                new StackTraceElement(SomeClass.NAME, "method", "Clazz.java", 88),
                new StackTraceElement(SomeClass.NAME, "method", "Clazz.java", 99),
                313,
                "***",
                "88", "99", "313", SomeClass.SIMPLE_NAME, "method");
        
        testFormat(
                new StackTraceElement(SomeClass.NAME, "method", "Clazz.java", 88),
                new StackTraceElement(SomeClass.NAME, "otherMethod", "Clazz.java", 99),
                313,
                "***",
                "88", "99", "313", SomeClass.SIMPLE_NAME, "method", "otherMethod");
    }

    private static void testFormat(StackTraceElement from, StackTraceElement to, long ms, String msg, String... lookFor)
    {
        PerformanceLogFormatter formatter = new DefaultPerformanceLogFormatter();
        String res = formatter.format(from, to, ms, msg);
        for(String s : lookFor)
            assertThat(res, containsString(s));
    }
}
