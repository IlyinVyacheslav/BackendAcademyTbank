package backend.academy.rendering;

import backend.academy.FractalImage;
import backend.academy.rendering.transfromation.Transformation;
import java.util.List;

public interface FractalImageRenderer {
    @SuppressWarnings("checkstyle:ParameterNumber")
    FractalImage render(
        FractalImage canvas, Rect world, List<Transformation> variations,
        int affineTransformsNumber, int samples, short iterPerSample, int symmetry, long seed
    );
}
