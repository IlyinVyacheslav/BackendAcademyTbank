package backend.academy.rendering;

public record Pixel(RGB rgb, int hitCount) {
    public Pixel(int r, int g, int b, int hitCount) {
        this(new RGB(r, g, b), hitCount);
    }

    public Pixel(int r, int g, int b) {
        this(r, g, b, 0);
    }

    public int red() {
        return this.rgb.red();
    }

    public int green() {
        return this.rgb.green();
    }

    public int blue() {
        return this.rgb.blue();
    }

    public record RGB(int red, int green, int blue) {
    }
}
