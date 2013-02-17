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
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Tests for {@link PerformanceLogger} without a settings file.
 * 
 * @author Matthias Langer
 */
public class TestPerformanceLoggerWithoutSettingsFile
{
    @BeforeClass
    public static void beforeClass()
    {
        try
        {
            /*
             * Remove stray setting files before we start:
             */
            File file = LocalResources.getFile("at/ipsquare/commons/core/util/performanceLogger.xml");
            if(!file.delete())
                throw new RuntimeException("Could not delete '" + file + "'.");
        }
        catch(FileNotFoundException e)
        {
            // OK!
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }

        UnitTestAppender.reset();
        PerformanceLogger.reloadDefaults();
    }
    
    private static class InnerClass
    {
        static class InnerInnerClass
        {
            void doStuff() throws InterruptedException
            {
                PerformanceLogger plog = new PerformanceLogger();
                Thread.sleep(5);
                plog.logElapsed("done, bastards");
            }
        }
        
        
        InnerClass(final PerformanceLogger plog) throws InterruptedException
        {
            plog.restart();
            
            class InCtorClass
            {
                InCtorClass()
                {
                    plog.logElapsedAndRestart();
                }
            }
            
            new InCtorClass();
            new InnerInnerClass().doStuff();
        }
    }
    
    @Test
    public void test() throws InterruptedException
    {
        final PerformanceLogger plog = new PerformanceLogger();
        Thread.sleep(1);
        plog.logElapsed();
        
        assertThat(logString(), containsString(TestPerformanceLoggerWithoutSettingsFile.class.getSimpleName()));
        assertThat(logString(), containsString("test"));
        assertThat(logString(), not(containsString(UnitTestPeformanceLogFormatter.PREFIX)));
        
        new InnerClass(plog);
        plog.logElapsed("asdf");
        
        assertThat(logString(), containsString("asdf"));
        assertThat(logString(), containsString(InnerClass.class.getSimpleName()));
        
        new Object() 
        {
            {
                plog.logElapsedAndRestart("obj");
            }
            
            void strangeConstructIndeed()
            {
                plog.logElapsed("strange");
            }
        }.strangeConstructIndeed();
        
        assertThat(logString(), containsString("obj"));
        assertThat(logString(), containsString("strangeConstructIndeed"));
        
        new Runnable()
        {
            @Override
            public void run()
            {
                plog.logElapsed("running away");
            }
        }.run();
        
        assertThat(logString(), containsString("running"));
        
        PerformanceLogger plog2 = new PerformanceLogger(1000);
        plog2.logElapsed("should-never-be-logged");
        assertThat(logString(), not(containsString("should-never-be-logged")));
        Thread.sleep(1500);
        plog2.logElapsed("should-be-logged");
        assertThat(logString(), containsString("should-be-logged"));
        
        plog2 = new PerformanceLogger(new UnitTestPeformanceLogFormatter());
        plog2.logElapsed();
        assertThat(logString(), containsString(UnitTestPeformanceLogFormatter.PREFIX));
        
        Logger logbackLogger = (Logger) LoggerFactory.getLogger(PerformanceLogger.class);
        try
        {
            logbackLogger.setLevel(Level.ERROR);
            plog.logElapsedAndRestart("do-not-log-me");
            assertThat(logString(), not(containsString("do-not-log-me")));
        }
        finally
        {
            logbackLogger.setLevel(Level.DEBUG);
        }
    }

    private static String logString()
    {
        return UnitTestAppender.logString();
    }
}
