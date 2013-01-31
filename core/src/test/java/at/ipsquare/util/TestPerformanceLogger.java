package at.ipsquare.util;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import at.ipsquare.commons.util.PerformanceLogger;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.OutputStreamAppender;

/**
 * Tests for {@link PerformanceLogger}.
 * 
 * @author Matthias Langer
 */
public class TestPerformanceLogger
{
    public static class TestAppender<E> extends OutputStreamAppender<E>
    {
        private static final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        @Override
        public void start()
        {
            setOutputStream(stream);
            super.start();
        }
    }
    
    private static String logString()
    {
        try
        {
            return TestAppender.stream.toString("UTF-8").trim();
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
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
    
    /**
     * Meant to cover the full functionality of our {@link PerformanceLogger}.
     */
    @Test
    public void test() throws InterruptedException
    {
        final PerformanceLogger plog = new PerformanceLogger();
        Thread.sleep(1);
        plog.logElapsed();
        
        assertThat(logString(), containsString(TestPerformanceLogger.class.getSimpleName()));
        assertThat(logString(), containsString("test"));
        
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
        
        Logger logbackLogger = (Logger) LoggerFactory.getLogger(PerformanceLogger.class);
        logbackLogger.setLevel(Level.ERROR);
        plog.logElapsedAndRestart("do-not-log-me");
        assertThat(logString(), not(containsString("do-not-log-me")));
    }
}
