package at.ipsquare.commons.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.jcip.annotations.NotThreadSafe;
import ch.qos.logback.core.OutputStreamAppender;

/**
 * An {@link OutputStreamAppender} for unit tests.
 *
 * @author Matthias Langer
 */
@NotThreadSafe
public class UnitTestAppender<E> extends OutputStreamAppender<E>
{
    private static final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    public static boolean enabled = true;

    @Override
    public void start()
    {
        setOutputStream(stream);
        super.start();
    }

    public static String logString()
    {
        try
        {
            return stream.toString("UTF-8").trim();
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void reset()
    {
        stream.reset();
    }

    @Override
    protected void writeOut(E event) throws IOException
    {
        if (enabled)
            super.writeOut(event);
    }
}