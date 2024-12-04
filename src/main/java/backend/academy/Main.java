package backend.academy;

import backend.academy.rendering.FractalImageRenderer;
import backend.academy.rendering.Rect;
import backend.academy.rendering.SingleThreadedFractalImageRenderer;
import backend.academy.rendering.transfromation.TransformationPolar;
import backend.academy.rendering.transfromation.TransformationSpherical;
import backend.academy.rendering.transfromation.TransformationSpiral;
import backend.academy.utils.ImageCorrection;
import backend.academy.utils.ImageFormat;
import backend.academy.utils.ImageUtils;
import java.nio.file.Path;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    @SuppressWarnings("checkstyle:MagicNumber")
    public static void main(String[] args) {
        FractalImageRenderer renderer = new SingleThreadedFractalImageRenderer();
        var image = renderer.render(FractalImage.create(1000, 1000), new Rect(2, 2, 4, 4),
            List.of(new TransformationSpherical(), new TransformationSpiral(), new TransformationPolar()), 20,
            100000, (short) 200, 8, 433451227);
        ImageCorrection correction = new ImageCorrection(10);
        correction.correct(image);

        ImageUtils utils = new ImageUtils();
        utils.save(image, Path.of("corrected_img.jpeg"), ImageFormat.JPEG);
    }
}
