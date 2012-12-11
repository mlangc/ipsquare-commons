package at.ipsquare.util;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

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
    
    private static class SomeClass
    {
        SomeClass(PerformanceLogger plog)
        {
            plog.restart();
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
        
        new SomeClass(plog);
        plog.logElapsed("asdf");
        
        assertThat(logString(), containsString("asdf"));
        assertThat(logString(), containsString(SomeClass.class.getSimpleName()));
        
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
    }
}
