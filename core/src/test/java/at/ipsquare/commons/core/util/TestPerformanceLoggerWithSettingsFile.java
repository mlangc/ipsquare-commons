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
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Tests for {@link PerformanceLogger} with a settings file.
 * 
 * @author Matthias Langer
 */
public class TestPerformanceLoggerWithSettingsFile
{
    private static final String 
        SETTING_SRC_BASE = "at/ipsquare/commons/core/util/_PerformanceLogger/",
        SETTINGS_FULL = "performanceLoggerFull.xml",
        SETTINGS_NO_THRESHOLD = "performanceLoggerNoThreshold.xml",
        SETTINGS_NO_FORMATTER = "performanceLoggerNoFormatter.xml",
        SETTINGS_INVALID_THRESHOLD = "performanceLoggerInvalidThreshold.xml",
        SETTINGS_INVALID_FORMATTER = "performanceLoggerInvalidFormatter.xml",
        SETTINGS_BROKEN_FORMATTER = "performanceLoggerBrokenFormatter.xml";
    
    private static void installDefaultSettingsFile(String basename)
    {
        try
        {
            File src = LocalResources.getFile(SETTING_SRC_BASE + basename);
            FileUtils.copyFile(src, defaultSettingsFile());
            PerformanceLogger.reloadDefaults();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private static void installDefaultSettingsFileAndResetTestAppender(String basename)
    {
        UnitTestAppender.reset();
        installDefaultSettingsFile(basename);
    }
    
    
    private static File defaultSettingsFile() throws IOException
    {
        File settingsDir = LocalResources.getFile("at/ipsquare/commons/core/util/_PerformanceLogger/").getParentFile();
        return new File(settingsDir, "performanceLogger.xml");
    }
    
    @AfterClass
    public static void afterClass()
    {
        try
        {
            defaultSettingsFile().delete();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void testWithFullSettings() throws InterruptedException
    {
        installDefaultSettingsFileAndResetTestAppender(SETTINGS_FULL);
        PerformanceLogger plog = new PerformanceLogger();
        plog.logElapsedAndRestart();
        assertThat(UnitTestAppender.logString(), isEmptyString());
        
        Thread.sleep(6);
        plog.logElapsed();
        assertThat(UnitTestAppender.logString(), containsString(UnitTestPeformanceLogFormatter.PREFIX));
    }
    
    @Test
    public void testWithNoThreshold() throws InterruptedException
    {
        assertThatSomethingIsLogged(SETTINGS_NO_THRESHOLD);
    }
    
    @Test
    public void testWithNoFormatter() throws InterruptedException
    {
        assertThatSomethingIsLogged(SETTINGS_NO_FORMATTER);
    }
    
    @Test
    public void testWithInvalidFormatter() throws InterruptedException
    {
       assertThatSomethingIsLogged(SETTINGS_INVALID_FORMATTER, 0, "Upsala");
    }
    
    @Test
    public void testWithBrokenFormatter() throws InterruptedException
    {
        assertThatSomethingIsLogged(SETTINGS_BROKEN_FORMATTER, 0, "Damn");
    }
    
    @Test
    public void testWithInvalidThreshold() throws InterruptedException
    {
        assertThatSomethingIsLogged(SETTINGS_INVALID_THRESHOLD, 0, "NaN");
        assertThatSomethingIsLogged(SETTINGS_INVALID_THRESHOLD, 0, TestPerformanceLoggerWithSettingsFile.class.getSimpleName());
    }
    
    private void assertThatSomethingIsLogged(String settingsFile) throws InterruptedException
    {
        assertThatSomethingIsLogged(settingsFile, 0);
    }
    
    private void assertThatSomethingIsLogged(String settingsFile, int sleep) throws InterruptedException
    {
        assertThatSomethingIsLogged(settingsFile, sleep, null);
    }
    
    private void assertThatSomethingIsLogged(String settingsFile, int sleep, String catchword) throws InterruptedException
    {
        installDefaultSettingsFileAndResetTestAppender(settingsFile);
        PerformanceLogger plog = new PerformanceLogger();
        Thread.sleep(sleep);
        plog.logElapsed();
        
        if(catchword == null)
            assertThat(UnitTestAppender.logString(), not(isEmptyString()));
        else
            assertThat(UnitTestAppender.logString(), containsString(catchword));
        
    }
}
