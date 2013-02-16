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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import at.ipsquare.commons.core.util.LocalResources;

/**
 * Tests for {@link LocalResources}.
 * 
 * @author Matthias Langer
 */
public class TestLocalResources
{
    private static String TEST_FILE_PATH = "at/ipsquare/commons/core/util/_LocalResources/test.txt";
    private static String NOT_EXISTING_PATH = "ASDFKJASDLFlsdfsdll/sdf/dd";
    private static String WITHIN_COMMONS_COLLECTIONS_JAR_PATH = "META-INF/maven/commons-collections/commons-collections/pom.properties";
    private static String STARTS_WITH_SLASH = "/META-INF/maven/commons-collections/commons-collections/pom.properties";
    
    /**
     * Tests for {@link LocalResources#getFile(String)}.
     */
    @Test
    public void testGetFile()
    {
        testGetFile(TEST_FILE_PATH, "test", null);
        testGetFile(NOT_EXISTING_PATH, null, NOT_EXISTING_PATH);
        testGetFile(WITHIN_COMMONS_COLLECTIONS_JAR_PATH, null, "getStream");
        testGetFile(STARTS_WITH_SLASH, null, "leading");
    }
    
    /**
     * Tests for {@link LocalResources#getUrl(String)}.
     */
    @Test
    public void testGetUrl() throws IOException
    {
        /*
         * As getFile() and getStream() use this method we only verify that we can read contents from a JAR file:
         */
        URL url = LocalResources.getUrl(WITHIN_COMMONS_COLLECTIONS_JAR_PATH);
        assertNotNull(url);
        String contents = consume(url.openStream());
        assertTrue(StringUtils.containsIgnoreCase(contents, "commons-collections"));
    }
    
    /**
     * Tests for {@link LocalResources#getStream(String)}.
     */
    @Test
    public void testGetStream() throws IOException
    {
        /*
         * As getStream() just forwards to getUrl() we just verify that we can open our test file:
         */
        InputStream in = LocalResources.getStream(TEST_FILE_PATH);
        assertNotNull(in);
        String contents = consume(in);
        assertTrue(StringUtils.containsIgnoreCase(contents, "test"));
    }
    
    /**
     * See {@link #testGetFile(String, String, String, ClassLoader)}.
     * 
     * <p/>
     * Note that this method tests {@link LocalResources#getFile(String)} with different context {@link ClassLoader}s.
     */
    private static void testGetFile(String path, String expectedContents, String expectedError)
    {
    	ClassLoader origContextClassLoader = Thread.currentThread().getContextClassLoader();
    	try
    	{
    		testGetFile(path, expectedContents, expectedError, origContextClassLoader);
    		testGetFile(path, expectedContents, expectedError, null);
    		testGetFile(path, expectedContents, expectedError, TestLocalResources.class.getClassLoader());
    	}
    	finally
    	{
    		Thread.currentThread().setContextClassLoader(origContextClassLoader);
    	}
    }
    

    /**
     * Tests {@link LocalResources#getFile(String)}.
     * 
     * @param path the path.
     * @param expectedContents a string that should be contained in the file if it is expected to exist.
     * @param expectedError a string that should be contained in the exception message we get, if any.
     * @param contextClassLoader the context {@link ClassLoader} to use (see {@link Thread#setContextClassLoader(ClassLoader)}).
     */
    private static void testGetFile(String path, String expectedContents, String expectedError, ClassLoader contextClassLoader)
    {
        try
        {
        	Thread.currentThread().setContextClassLoader(contextClassLoader);
            File f = LocalResources.getFile(path);
            if(expectedError != null)
                fail("Expected an error containing '" + expectedError + "'.");
            
            assertNotNull(f);
            assertTrue(f.exists());
            assertTrue(f.canRead());
            
            String contents = consume(new FileInputStream(f));
            assertTrue(StringUtils.containsIgnoreCase(contents, expectedContents));
        }
        catch(Throwable th)
        {
            if(expectedError ==  null)
                fail("Did not expect an error but got: " + th);
            
            assertTrue("Expected exception message containing '" + expectedError + "' but got:\n" + th.getMessage(),
                    StringUtils.containsIgnoreCase(th.getMessage(), expectedError));
        }
    }
    
    private static String consume(InputStream in) throws IOException
    {
        try
        {
            return IOUtils.toString(in);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }
}
