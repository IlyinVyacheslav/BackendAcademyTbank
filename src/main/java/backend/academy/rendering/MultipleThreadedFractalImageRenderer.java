package backend.academy.rendering;

import backend.academy.FractalImage;
import backend.academy.rendering.affine.AffineTransformation;
import backend.academy.rendering.transfromation.Transformation;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultipleThreadedFractalImageRenderer extends BaseFractalImageRenderer {
    public static final int TIMEOUT = 4;
    private static final ThreadLocal<Random> THREAD_LOCAL_RANDOM = ThreadLocal.withInitial(SecureRandom::new);
    private final int threads;

    public MultipleThreadedFractalImageRenderer(int threads) {
        this.threads = threads;
    }

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
        List<AffineTransformation> affineTransformations = super.getAffineTransformations(affineTransformsNumber);
        try (ExecutorService threadPool = Executors.newFixedThreadPool(threads)) {

            for (int num = 0; num < samples; ++num) {
                long threadSeed = seed + num;
                threadPool.submit(
                    () -> {
                        Random threadRandom = THREAD_LOCAL_RANDOM.get();
                        threadRandom.setSeed(threadSeed);
                        super.processSample(canvas, world, affineTransformations, variations, iterPerSample, symmetry,
                            threadRandom);
                    });
            }

            threadPool.shutdown();
            try {
                threadPool.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread pool execution interrupted", e);
            }
        }
        return canvas;
    }
}
