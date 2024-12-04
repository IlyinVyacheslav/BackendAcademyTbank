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
            new Pixel((int) (pixel.red() * c), (int) (pixel.green() * c), (int) (pixel.blue() * c), pixel.hitCount()));
    }

    public void transformPixel(int x, int y, Pixel.RGB color) {
        if (!this.contains(x, y)) {
            return;
        }

        Pixel pixel = this.getPixel(x, y);
        if (pixel.hitCount() == 0) {
            this.setPixel(x, y, new Pixel(color.red(), color.green(), color.blue(), 1));
        }
        this.setPixel(x, y,
            new Pixel((pixel.red() + color.red()) / 2, (pixel.green() + color.green()) / 2,
                (pixel.blue() + color.blue()) / 2,
                pixel.hitCount() + 1));
    }
}
