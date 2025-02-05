package backend.academy.rendering.affine;

import backend.academy.rendering.Pixel;
import java.util.Random;

public class AffineTransformationUtils {
    public static final int COLOR_BOUND = 255;
    private final Random random;

    public AffineTransformationUtils(Random random) {
        this.random = random;
    }

    public AffineTransformation getRandomAffineTransformation() {
        AffineTransformation affineTransformation;
        do {
            affineTransformation = getRandomCoefficients();
        } while (!affineTransformation.isValid());
        return affineTransformation;
    }

    private AffineTransformation getRandomCoefficients() {
        return new AffineTransformation(generateRandomCoefficient(), generateRandomCoefficient(),
            generateRandomCoefficient(), generateRandomCoefficient(),
            generateRandomCoefficient(), generateRandomCoefficient(),
            new Pixel.RGB(generateRandomColor(), generateRandomColor(), generateRandomColor()));
    }

    private double generateRandomCoefficient() {
        return random.nextDouble(2) - 1;
    }

    private int generateRandomColor() {
        return random.nextInt(COLOR_BOUND);
    }
}
