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

    public static Color between(Color leftColor, Color rightColor, float fraction) {
        int red = between(leftColor.getRed(), rightColor.getRed(), fraction);
        int green = between(leftColor.getGreen(), rightColor.getGreen(), fraction);
        int blue = between(leftColor.getBlue(), rightColor.getBlue(), fraction);
        int alpha = between(leftColor.getAlpha(), rightColor.getAlpha(), fraction);

        return new Color(red, green, blue, alpha);
    }

    private static int between(int left, int right, float fraction) {
        int min;
        int max;
        if (left < right) {
            min = left;
            max = right;
        } else if (right < left) {
            min = left;
            max = right;
        } else {
            return left;
        }
        return Math.round(min + fraction * (max - min));
    }

    public static Color parse(String rgbHex) {
        return new Color(Integer.parseInt(rgbHex, 16));
    }
}
