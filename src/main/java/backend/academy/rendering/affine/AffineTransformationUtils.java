package backend.academy.rendering.affine;

import java.util.Random;

public class AffineTransformationUtils {
    private final Random random;

    public AffineTransformationUtils(Random random) {
        this.random = random;
    }

    public AffineTransformation getRandomAffineTransformation() {
        AffineTransformation affineTransformation;
        int cnt = 0;
        do {
            affineTransformation = getRandomCoefficients();
            cnt++;
        } while (!affineTransformation.isValid());
        return affineTransformation;
    }

    private AffineTransformation getRandomCoefficients() {
        return new AffineTransformation(generateRandomCoefficient(), generateRandomCoefficient(),
            generateRandomCoefficient(), generateRandomCoefficient(),
            generateRandomCoefficient(), generateRandomCoefficient());
    }

    private double generateRandomCoefficient() {
        return random.nextDouble(2) - 1;
    }
}
