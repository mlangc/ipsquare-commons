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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.EnumerationUtils;

/**
 * For loading local resources from the classpath.
 *  
 * @author Matthias Langer
 * @since 2.0.0
 */
public final class LocalResources
{
  /**
   * Returns a file object for the given path.
   * 
   * Please note that this method does not work for resources that lie within JAR files. Use either {@link #getStream(String)}
   * or {@link #getUrl(String)} for these.
   * 
   * @param path a path relative to the current classpath.
   * @return an appropriate file object.
   */
  public static File getFile(String path) throws IOException
  {
    URL url = getUrl(path);
    if("jar".equals(url.getProtocol()))
        throw new IOException(url + " points to a file that lies within a JAR file. Please use getUrl() or getStream() instead.");
    
    File file = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
    if(!file.canRead())
      throw new IOException("Not readable: '" + file + "'.");
    return file;
  }
  
  /**
   * Returns an {@link InputStream} for the given path.
   * 
   * @see #getFile(String)
   */
  public static InputStream getStream(String path) throws IOException
  {
    return getUrl(path).openStream();
  }
  
  /**
   * See {@link #getFile(String)} and {@link #getStream(String)}.
   */
  public static URL getUrl(String path) throws IOException
  {
    Set<ClassLoader> classLoaders = ClassLoaders.get();
    Set<String> urls = new HashSet<String>(2);
    
    for(ClassLoader cl : classLoaders)
    {
      URL url = getUrl(path, cl);
      if(url != null)
        urls.add(url.toExternalForm());
    }
    
    if(urls.isEmpty())
    {
      StringBuilder sb = new StringBuilder("'");
      sb.append(path).append("'");
      
      if(path.startsWith("/"))
        sb.append("; try ommiting the leading '/'.");
      
      sb.append("\n").append(classLoaderInfo(classLoaders));
      throw new FileNotFoundException(sb.toString());
    }
    
    if(urls.size() > 1)
      throw new IOException("'" + path + "' : Ambiguous path.");
    
    return new URL(urls.iterator().next());
  }
  
  private static String classLoaderInfo(Collection<ClassLoader> classLoaders)
  {
    StringBuilder sb = new StringBuilder("ClassLoaders involved:");
    for(ClassLoader cl : classLoaders)
      sb.append("\n").append(classLoaderInfo(cl));
    return sb.toString();
  }
  
  private static String classLoaderInfo(ClassLoader cl)
  {
    StringBuilder sb = new StringBuilder("** ");
    
    if(cl instanceof URLClassLoader)
    {
      URLClassLoader urlCl = (URLClassLoader) cl;
      sb.append("URLClassLoader -- ").append(collectClassLoaderUrls(urlCl));
    }
    else
      sb.append(cl);
    
    return sb.toString();
  }
  
  private static URL getUrl(String path, ClassLoader cl) throws IOException
  {
    @SuppressWarnings("unchecked")
    List<URL> urls = EnumerationUtils.toList(cl.getResources(path));
    
    if(urls.isEmpty())
      return null;
    if(urls.size() > 1)
      throw new IOException("'" + path + "' : Ambiguous path; URLs: " + urls);
    return urls.get(0);
  }
  
  private static Set<String> collectClassLoaderUrls(URLClassLoader cl)
  {
    Set<String> urls = new TreeSet<String>();
    collectClassLoaderUrls(cl, urls);
    return urls;
  }
  
  private static void collectClassLoaderUrls(URLClassLoader cl, Set<String> accumulator)
  {
    for(URL url : cl.getURLs())
      accumulator.add(url.toString());
    
    ClassLoader parent = cl.getParent();
    if(parent instanceof URLClassLoader)
      collectClassLoaderUrls((URLClassLoader) parent, accumulator);
  }
  
  private LocalResources()
  {
    
  }
}
