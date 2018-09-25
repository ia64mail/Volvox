package volvox.common.utils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActorNameUtilsTest {
    static ActorNameUtils utils;

    @BeforeClass
    public static void setup() {
        utils = new ActorNameUtils();
    }

    @AfterClass
    public static void teardown() {
        //NOOP;
    }

    @Test
    public void shouldTransformToUpperCaseSuccessfully() {
        assertEquals("UPPER CASE", utils.toUpperCase("Upper CASe"));
    }

    @Test
    public void shouldTransformToLowerCaseSuccessfully() {
        assertEquals("upper case", utils.toLowerCase("Upper CASe"));
    }
}
