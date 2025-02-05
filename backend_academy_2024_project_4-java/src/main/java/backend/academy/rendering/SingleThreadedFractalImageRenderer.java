package backend.academy.rendering;

import backend.academy.FractalImage;
import backend.academy.rendering.affine.AffineTransformation;
import backend.academy.rendering.transfromation.Transformation;
import java.util.List;

public class SingleThreadedFractalImageRenderer extends BaseFractalImageRenderer {

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
        for (int num = 0; num < samples; ++num) {
            super.processSample(canvas, world, affineTransformations, variations, iterPerSample, symmetry, RANDOM);
        }
        return canvas;
    }
}
