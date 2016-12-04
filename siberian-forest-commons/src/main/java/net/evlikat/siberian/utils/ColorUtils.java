package net.evlikat.siberian.utils;

import java.awt.Color;

public final class ColorUtils {
    private ColorUtils() {
    }

    public static Color modify(Color color, float fraction) {
        int red = Math.round(Math.min(255, color.getRed() + 255 * fraction));
        int green = Math.round(Math.min(255, color.getGreen() + 255 * fraction));
        int blue = Math.round(Math.min(255, color.getBlue() + 255 * fraction));

        int alpha = color.getAlpha();

        return new Color(red, green, blue, alpha);
    }
}
