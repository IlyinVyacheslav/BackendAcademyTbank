package backend.academy.utils;

import backend.academy.FractalImage;
import backend.academy.rendering.Pixel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class ImageUtils {

    public static final int GREEN_SHIFT = 8;
    public static final int RED_SHIFT = 16;

    public void save(FractalImage image, Path fileName, ImageFormat format) {
        BufferedImage bufferedImage = new BufferedImage(image.width(), image.height(), BufferedImage.TYPE_INT_RGB);
        int width = image.width();
        int height = image.height();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel pixel = image.pixel(x, y).orElseGet(() -> new Pixel(0, 0, 0));
                int rgb = (pixel.red() << RED_SHIFT) | (pixel.green() << GREEN_SHIFT) | pixel.blue();
                bufferedImage.setRGB(x, y, rgb);
            }
        }

        try {
            String formatName = format.name().toLowerCase();
            ImageIO.write(bufferedImage, formatName, fileName.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save the image to " + fileName, e);
        }
    }
}
