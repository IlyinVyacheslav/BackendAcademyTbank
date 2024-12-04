package backend.academy;

import backend.academy.rendering.Pixel;
import java.util.Optional;

public record FractalImage(Pixel[] pixels, int width, int height) {
    public static FractalImage create(int width, int height) {
        Pixel[] pixels = new Pixel[width * height];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = new Pixel(0, 0, 0);
        }
        return new FractalImage(pixels, width, height);
    }

    public boolean contains(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private Pixel getPixel(int x, int y) {
        return this.pixels[y * width + x];
    }

    private void setPixel(int x, int y, Pixel pixel) {
        this.pixels[y * width + x] = pixel;
    }

    public Optional<Pixel> pixel(int x, int y) {
        if (this.contains(x, y)) {
            return Optional.of(this.getPixel(x, y));
        }
        return Optional.empty();
    }

    public int getHitCount(int x, int y) {
        if (!this.contains(x, y)) {
            return -1;
        }
        return this.getPixel(x, y).hitCount();
    }

    public void correctPixelColor(int x, int y, double c) {
        if (!this.contains(x, y)) {
            return;
        }

        Pixel pixel = this.getPixel(x, y);

        this.setPixel(x, y,
            new Pixel((int) (pixel.r() * c), (int) (pixel.g() * c), (int) (pixel.b() * c), pixel.hitCount()));
    }

    public void transformPixel(int x, int y, Pixel color) {
        if (!this.contains(x, y)) {
            return;
        }

        Pixel pixel = this.getPixel(x, y);
        if (pixel.hitCount() == 0) {
            this.setPixel(x, y, new Pixel(color.r(), color.g(), color.b(), 1));
        }
        this.setPixel(x, y,
            new Pixel((pixel.r() + color.r()) / 2, (pixel.g() + color.g()) / 2, (pixel.b() + color.b()) / 2,
                pixel.hitCount() + 1));
    }
}
