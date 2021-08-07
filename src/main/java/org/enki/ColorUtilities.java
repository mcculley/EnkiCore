package org.enki;

import java.awt.Color;

public class ColorUtilities {

    private ColorUtilities() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    public static Color setAlpha(final Color c, final float alpha) {
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();
        return new Color(red, green, blue, (int) (alpha * 255.0));
    }

    public static Color interpolateRGB(final Color endColor, final Color startColor, final double t) {
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException();
        }

        final float inverse = 1.0f - (float) t;
        final int r = (int) (endColor.getRed() * t + startColor.getRed() * inverse);
        final int g = (int) (endColor.getGreen() * t + startColor.getGreen() * inverse);
        final int b = (int) (endColor.getBlue() * t + startColor.getBlue() * inverse);
        return new Color(r, g, b);
    }

    public static Color interpolateHSB(final Color endColor, final Color startColor, final double t) {
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException();
        }

        final float[] startHSB = Color.RGBtoHSB(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), null);
        final float[] endHSB = Color.RGBtoHSB(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), null);

        final float inverse = 1.0f - (float) t;
        final float h = endHSB[0] * (float) t + startHSB[0] * inverse;
        final float s = endHSB[1] * (float) t + startHSB[1] * inverse;
        final float b = endHSB[2] * (float) t + startHSB[2] * inverse;
        return Color.getHSBColor(h, s, b);
    }

}
