package backend.academy.rendering;

import backend.academy.FractalImage;
import backend.academy.rendering.affine.AffineTransformation;
import backend.academy.rendering.affine.AffineTransformationUtils;
import backend.academy.rendering.transfromation.Transformation;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SingleThreadedFractalImageRenderer implements FractalImageRenderer {
    public static final int STEPS_TO_SKIP = -20;
    private static final Random RANDOM = new SecureRandom();

    @SuppressWarnings("checkstyle:ParameterNumber")
    @Override
    public FractalImage render(
        FractalImage canvas,
        Rect world,
        List<Transformation> variations,
        int affineTransformsNumber,
        int samples,
        short iterPerSample,
        int symmetry,
        long seed
    ) {
        RANDOM.setSeed(seed);
        List<AffineTransformation> affineTransformations = getAffineTransformations(affineTransformsNumber);
        for (int num = 0; num < samples; ++num) {
            Point pw = world.randomPoint(RANDOM);
            for (short step = STEPS_TO_SKIP; step < iterPerSample; ++step) {
                AffineTransformation affineTransformation =
                    affineTransformations.get(RANDOM.nextInt(affineTransformations.size()));
                Transformation variation = variations.get(RANDOM.nextInt(variations.size()));
                pw = affineTransformation.transformPoint(pw);
                pw = variation.apply(pw);
                if (step <= 0) {
                    continue;
                }
                double theta = 0.0;
                for (int s = 0; s < symmetry; theta += Math.PI * 2 / symmetry, s++) {
                    pw = pw.rotate(theta);
                    if (!world.contains(pw)) {
                        continue;
                    }

                    var coordinate = mapRange(world, pw, canvas);

                    canvas.transformPixel(coordinate.x, coordinate.y, variation.defaultPixel());
                }
            }

        }
        return canvas;
    }

    private List<AffineTransformation> getAffineTransformations(int n) {
        AffineTransformationUtils utils = new AffineTransformationUtils(RANDOM);
        List<AffineTransformation> transformations = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            transformations.add(utils.getRandomAffineTransformation());
        }
        return transformations;
    }

    private Coordinate mapRange(Rect world, Point p, FractalImage canvas) {
        int pixelX = (int) ((p.x() + world.x()) / world.width() * canvas.width());
        int pixelY = (int) ((p.y() + world.y()) / world.height() * canvas.height());

        return new Coordinate(pixelX, pixelY);
    }

    private record Coordinate(int x, int y) {
    }
}
