package backend.academy.utils;

import backend.academy.FractalImage;

public class ImageCorrection {
    private final double gamma;

    public ImageCorrection(double gamma) {
        this.gamma = gamma;
    }

    private static double getMaxHitCount(FractalImage image, double[][] normal) {
        double maxHitCount = 0;
        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                int hitCount = image.getHitCount(x, y);
                if (hitCount > 0) {
                    normal[y][x] = Math.log10(hitCount);
                    maxHitCount = Math.max(maxHitCount, hitCount);
                }
            }
        }
        return maxHitCount;
    }

    public void correct(FractalImage image) {
        double[][] normal = new double[image.height()][image.width()];

        double maxHitCount = getMaxHitCount(image, normal);

        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                normal[y][x] /= maxHitCount;
                image.correctPixelColor(x, y, Math.pow(normal[y][x], 1 / gamma));
            }
        }
    }
}
