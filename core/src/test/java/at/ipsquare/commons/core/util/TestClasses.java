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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

/**
 * Tests for {@link Classes}.
 * 
 * @author Matthias Langer
 */
public class TestClasses
{
    private static final String
        SOME_JAR = "at/ipsquare/commons/core/util/_Classes/someJar.jar",
        SOME_CLASS_NAME_ONLY_IN_SOME_JAR = "org.apache.maven.doxia.sink.Sink";
    
    @Test
    public void testForName() throws IOException, ClassNotFoundException
    {
        assertNotNull(getClass().getName());
        
        try
        {
            Classes.forName(SOME_CLASS_NAME_ONLY_IN_SOME_JAR);
            fail();
        }
        catch(ClassNotFoundException e)
        {
            // OK!
        }
        
        ClassLoader fancyLoader = new URLClassLoader(new URL[] { LocalResources.getUrl(SOME_JAR) });
        Thread.currentThread().setContextClassLoader(fancyLoader);
        assertNotNull(Classes.forName(SOME_CLASS_NAME_ONLY_IN_SOME_JAR));
        Thread.currentThread().setContextClassLoader(null);
        
        ClassLoaders.register(fancyLoader);
        assertNotNull(Classes.forName(SOME_CLASS_NAME_ONLY_IN_SOME_JAR));
        
        try
        {
            ClassLoaders.unregister(fancyLoader);
            Classes.forName(SOME_CLASS_NAME_ONLY_IN_SOME_JAR);
        }
        catch(ClassNotFoundException e)
        {
            // OK!
        }
        
    }
}
