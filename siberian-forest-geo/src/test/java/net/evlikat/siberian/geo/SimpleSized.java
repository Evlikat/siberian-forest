package net.evlikat.siberian.geo;

public class SimpleSized implements Sized {

    private final int width;
    private final int height;

    public SimpleSized(int width, int height) {

        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
