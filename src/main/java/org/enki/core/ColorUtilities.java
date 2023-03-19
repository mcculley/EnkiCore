package org.enki.core;

import java.awt.Color;

/**
 * Some utilities for Color objects.
 */
public class ColorUtilities {

    @ExcludeFromJacocoGeneratedReport
    private ColorUtilities() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Set the alpha component of a Color, returning a new Color with the supplied alpha.
     *
     * @param c     the input Color
     * @param alpha the input alpha
     * @return a new Color object with the supplied alpha
     */
    public static Color setAlpha(final Color c, final float alpha) {
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();
        return new Color(red, green, blue, (int) (alpha * 255.0));
    }

}
