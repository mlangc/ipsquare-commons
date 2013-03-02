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

/**
 * Utility methods around {@link Class}.
 * 
 * @since 2.1.0
 * @author Matthias Langer
 */
public final class Classes
{
    /**
     * A version of {@link Class#forName(String, boolean, ClassLoader)} that uses the {@link ClassLoader}s obtained by {@link ClassLoaders#get()}.
     * 
     * <p>
     *  This method iterates through the {@link ClassLoaders} returned by {@link ClassLoaders#get()}
     *  and tries to load the desired class.
     * </p>
     * 
     * @param name the fully qualified name of the desired class.
     * @param initialize whether the class must be initialized.
     * @return the desired class.
     * @throws ClassNotFoundException if the specified class could not be located by any loader returned by {@link ClassLoaders#get()}
     */
    public static Class<?> forName(String name, boolean initialize) throws ClassNotFoundException
    {
        for(ClassLoader cl : ClassLoaders.get())
        {
            try
            {
                return Class.forName(name, initialize, cl);
            }
            catch(ClassNotFoundException e)
            {
                // Try next loader.
            }
        }
        
        throw new ClassNotFoundException(name);
    }
    
    /**
     * A version of {@link Class#forName(String)} that uses the {@link ClassLoader}s obtained by {@link ClassLoaders#get()}.
     * 
     * <p>
     *  Calling this method is equivalent to:
     *  <blockquote>
     *      {@code Classes.forName(name, true)}
     *  </blockquote>
     * </p>
     * 
     * @see #forName(String, boolean)
     */
    public static Class<?> forName(String name) throws ClassNotFoundException
    {
        return forName(name, true);
    }
    
    /**
     * An enhanced version of {@link #forName(String)} that makes sure that the returned class extends the given type.
     * 
     * <p>
     *  Calling this method is equivalent to:
     *  <blockquote>
     *      {@code Classes.forName(name, true, type)}
     *  </blockquote>
     * </p>
     */
    public static <T> Class<? extends T> forName(String name, Class<T> type) throws ClassNotFoundException
    {
        return forName(name, true, type);
    }
    
    /**
     * An enhanced version of {@link #forName(String, boolean)} that makes sure that the returned class extends the given type.
     * 
     * @param name the fully qualified name of the desired class.
     * @param initialize whether the class must be initialized.
     * @param type the type the returned class is expected to extend.
     * @return the desired class.
     * @throws ClassNotFoundException if the specified class could not be located by any loader returned by {@link ClassLoaders#get()}.
     * @throws ClassCastException if the desired class could be loaded, but does not extend the given type.
     */
    public static <T> Class<? extends T> forName(String name, boolean initialize, Class<T> type) throws ClassNotFoundException
    {
        Class<?> clazz = forName(name, initialize);
        if(!type.isAssignableFrom(clazz))
            throw new ClassCastException(clazz.getName() + " does not extend or implement " + type.getName() + ".");
        
        @SuppressWarnings("unchecked")
        Class<? extends T> ret = (Class<? extends T>) clazz;
        return ret;
    }
    
    private Classes()
    {
        
    }
}
