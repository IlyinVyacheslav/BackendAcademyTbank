package backend.academy.rendering;

import backend.academy.FractalImage;
import backend.academy.rendering.affine.AffineTransformation;
import backend.academy.rendering.affine.AffineTransformationUtils;
import backend.academy.rendering.transfromation.Transformation;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BaseFractalImageRenderer implements FractalImageRenderer {
    public static final int STEPS_TO_SKIP = -20;
    protected static final SecureRandom RANDOM = new SecureRandom();
    private final ReentrantLock canvasLock = new ReentrantLock();

    protected void processSample(
        FractalImage canvas,
        Rect world,
        List<AffineTransformation> affineTransformations,
        List<Transformation> variations,
        short iterPerSample,
        int symmetry,
        Random random
    ) {
        Point pw = world.randomPoint(random);
        for (short step = STEPS_TO_SKIP; step < iterPerSample; ++step) {
            AffineTransformation affineTransformation =
                affineTransformations.get(random.nextInt(affineTransformations.size()));
            Transformation variation = variations.get(random.nextInt(variations.size()));
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
                canvasLock.lock();
                try {
                    canvas.transformPixel(coordinate.x, coordinate.y, affineTransformation.color());
                } finally {
                    canvasLock.unlock();
                }
            }
        }
    }

    protected List<AffineTransformation> getAffineTransformations(int n) {
        AffineTransformationUtils utils = new AffineTransformationUtils(RANDOM);
        List<AffineTransformation> transformations = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            transformations.add(utils.getRandomAffineTransformation());
        }
        return transformations;
    }

    protected Coordinate mapRange(Rect world, Point p, FractalImage canvas) {
        int pixelX = (int) ((p.x() - world.x()) / world.width() * canvas.width());
        int pixelY = (int) ((p.y() - world.y()) / world.height() * canvas.height());

        return new Coordinate(pixelX, pixelY);
    }

    protected record Coordinate(int x, int y) {
    }
}
