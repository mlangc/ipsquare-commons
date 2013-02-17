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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link PerformanceLogger} with a settings file.
 * 
 * @author Matthias Langer
 */
public class TestPerformanceLoggerWithSettingsFile
{
    private static final String 
        SETTINGS_SRC  = "at/ipsquare/commons/core/util/performanceLoggerOff.xml",
        SETTINGS_DEST = "at/ipsquare/commons/core/util/performanceLogger.xml";
    
    @BeforeClass
    public static void beforeClass()
    {
        try
        {
            File src = LocalResources.getFile(SETTINGS_SRC);
            File dest = new File(src.getParentFile(), "performanceLogger.xml");
            FileUtils.copyFile(src, dest);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }

        PerformanceLogger.reloadDefaults();
        UnitTestAppender.reset();
    }
    
    @AfterClass
    public static void afterClass()
    {
        try
        {
            File settingsFile = LocalResources.getFile(SETTINGS_DEST);
            settingsFile.delete();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void test() throws InterruptedException
    {
        PerformanceLogger plog = new PerformanceLogger();
        plog.logElapsedAndRestart();
        assertThat(UnitTestAppender.logString(), isEmptyString());
        
        Thread.sleep(6);
        plog.logElapsed();
        assertThat(UnitTestAppender.logString(), containsString(UnitTestPeformanceLogFormatter.PREFIX));
    }
}
