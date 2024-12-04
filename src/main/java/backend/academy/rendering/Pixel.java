package backend.academy.rendering;

public record Pixel(int r, int g, int b, int hitCount) {
    public Pixel(int r, int g, int b) {
        this(r, g, b, 0);
    }
}
