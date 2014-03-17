package com.eventor.sample.start;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class RunnerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Test
    public void testMain() throws Exception {
        Runner.main(new String[]{});

        String[] out = outContent.toString().split(System.getProperty("line.separator"));
        String lastLine = out[out.length - 1];

        assertEquals("90.0", lastLine);
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }
}
