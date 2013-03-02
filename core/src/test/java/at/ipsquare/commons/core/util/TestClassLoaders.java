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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for {@link ClassLoaders}
 * 
 * @author Matthias Langer
 */
public class TestClassLoaders
{
    private static class TestClassLoader extends ClassLoader
    {
        final String name;
        
        public TestClassLoader(String name)
        {
            super(TestClassLoader.class.getClassLoader());
            this.name = name;
        }
        
        @Override
        public String toString()
        {
            return getClass().getSimpleName() + "[" + name + "]";
        }
    }
    
    /**
     * Verifies that CRUD operations work.
     */
    @Test
    public void testCrud()
    {
        setContextClassLoader(null);
        assertTrue(ClassLoaders.registered().isEmpty());
        assertThat(ClassLoaders.get().size(), equalTo(1));
        
        setContextClassLoader(new TestClassLoader("context"));
        assertThat(ClassLoaders.get().size(), equalTo(2));
        
        ClassLoader 
            loader1 = new TestClassLoader("1"),
            loader2 = new TestClassLoader("2");
        
        assertTrue(ClassLoaders.register(loader1));
        assertTrue(ClassLoaders.register(loader2));
        assertFalse(ClassLoaders.register(loader2));
        
        Set<ClassLoader> loaders = ClassLoaders.get();
        assertThat(loaders.size(), equalTo(4));
        Iterator<ClassLoader> iter = loaders.iterator();
        assertThat(iter.next(), equalTo(loader1));
        assertThat(iter.next(), equalTo(loader2));
        assertThat(iter.next(), equalTo(ClassLoaders.class.getClassLoader()));
        assertThat(iter.next(), equalTo(getContextClassLoader()));
        
        assertTrue(ClassLoaders.unregister(loader2));
        assertFalse(ClassLoaders.unregister(loader2));
        assertThat(ClassLoaders.get(), not(contains(loader2)));
        
        assertTrue(ClassLoaders.register(getContextClassLoader()));
        loaders = ClassLoaders.get();
        assertThat(loaders.size(), equalTo(3));
        iter = loaders.iterator();
        assertThat(iter.next(), equalTo(loader1));
        assertThat(iter.next(), equalTo(getContextClassLoader()));
        assertThat(iter.next(), equalTo(ClassLoaders.class.getClassLoader()));
        
        ClassLoaders.clear();
        assertTrue(ClassLoaders.registered().isEmpty());
    }
    
    @Test(expected = NullPointerException.class)
    public void testRegisterWithNull()
    {
        ClassLoaders.register(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void testUnregisterWithNull()
    {
        ClassLoaders.unregister(null);
    }
    
    private static void setContextClassLoader(ClassLoader loader)
    {
        Thread.currentThread().setContextClassLoader(loader);
    }
    
    private static ClassLoader getContextClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }
}
