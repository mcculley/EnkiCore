package org.enki.core;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorUtilitiesTest {

    @Test
    public void testSetAlpha() {
        final Color c = Color.YELLOW;
        final Color a = ColorUtilities.setAlpha(c, 0.5f);
        assertEquals(c.getRed(), a.getRed());
        assertEquals(c.getGreen(), a.getGreen());
        assertEquals(c.getBlue(), a.getBlue());
        assertEquals(255, c.getAlpha());
        assertEquals(127, a.getAlpha());
    }

}
