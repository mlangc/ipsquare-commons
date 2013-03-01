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

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;

import net.jcip.annotations.GuardedBy;

import com.google.common.collect.Sets;

/**
 * A registry for class loaders.
 * 
 * @since 2.1.0
 * @author Matthias Langer
 */
public final class ClassLoaders
{
    private static final Object lock = new Object();
    
    @GuardedBy("lock")
    private static Set<WeakReference<ClassLoader>> clRefs = Collections.emptySet();
    
    /**
     * Registers a {@link ClassLoader}.
     * 
     * <p>
     *  Please note that this class only keeps weak references to the {@link ClassLoaders}
     *  you pass to avoid potential memory leaks.
     * </p>
     * 
     * @param cl a {@link ClassLoader}.
     * @return true if the loader has not been registered before.
     * @see #unregister(ClassLoader)
     */
    public static boolean register(ClassLoader cl)
    {
        if(cl == null)
            throw new NullPointerException();
        
        synchronized(lock)
        {
            Set<ClassLoader> cls = resolveWeakReferences(clRefs);
            boolean ret = cls.add(cl);
            if(ret)
                clRefs = toWeakReferences(cls);
            return ret;
        }
    }
    
    /**
     * Unregisters a {@link ClassLoader}.
     * 
     * @param cl the {@link ClassLoader} that should be removed from the registry.
     * @return true if the loader was registered before.
     * @see #register(ClassLoader)
     */
    public static boolean unregister(ClassLoader cl)
    {
        if(cl == null)
            throw new NullPointerException();
        
        synchronized(lock)
        {
            Set<ClassLoader> resolved = resolveWeakReferences(clRefs);
            boolean ret = resolved.remove(cl);
            if(ret)
                clRefs = toWeakReferences(resolved);
            return ret;
        }
    }
    
    /**
     * Unregisters all {@link ClassLoader}s.
     * 
     * @see #register(ClassLoader)
     * @see #unregister(ClassLoader)
     */
    public static void clear()
    {
        synchronized(lock)
        {
            clRefs = Collections.emptySet();
        }
    }
    
    /**
     * Returns a set of all explicitly registered {@link ClassLoader}s.
     * 
     * @return a snapshot of the previously registered {@link ClassLoader}s in the order they have been registered.
     * @see #get()
     */
    public static Set<ClassLoader> registered()
    {
        synchronized(lock)
        {
            return resolveWeakReferences(clRefs);
        }
    }
    
    /**
     * Returns a set of all explicitly registered {@link ClassLoader}s as well as the {@link ClassLoader} that 
     * loaded this class and the current context class loader if set.
     * 
     * @return the previously registered {@link ClassLoader}s in the order the have been registered, followed by the {@link ClassLoader}
     *  that loaded this class and the context {@link ClassLoader} if set.
     */
    public static Set<ClassLoader> get()
    {
        synchronized(lock)
        {
            Set<ClassLoader> ret = resolveWeakReferences(clRefs);
            ret.add(ClassLoaders.class.getClassLoader());
            
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if(contextClassLoader != null)
                ret.add(contextClassLoader);
            return ret;
        }
    }
    
    private static Set<WeakReference<ClassLoader>> toWeakReferences(Set<ClassLoader> cls)
    {
        Set<WeakReference<ClassLoader>> ret = Sets.newLinkedHashSetWithExpectedSize(cls.size());
        for(ClassLoader cl : cls)
            ret.add(new WeakReference<ClassLoader>(cl));
        return ret;
    }

    private static Set<ClassLoader> resolveWeakReferences(Set<WeakReference<ClassLoader>> cls)
    {
        Set<ClassLoader> ret = Sets.newLinkedHashSetWithExpectedSize(cls.size() + 2);
        for(WeakReference<ClassLoader> clRef : cls)
        {
            ClassLoader cl = clRef.get();
            if(cl != null)
                ret.add(cl);
        }
        return ret;
    }

    private ClassLoaders()
    {
        
    }
}
