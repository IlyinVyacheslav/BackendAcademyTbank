package backend.academy;

import backend.academy.rendering.FractalImageRenderer;
import backend.academy.rendering.Rect;
import backend.academy.rendering.SingleThreadedFractalImageRenderer;
import backend.academy.rendering.transfromation.TransformationHeart;
import backend.academy.rendering.transfromation.TransformationHorseHoe;
import backend.academy.utils.ImageCorrection;
import backend.academy.utils.ImageFormat;
import backend.academy.utils.ImageUtils;
import java.nio.file.Path;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@UtilityClass
public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    @SuppressWarnings("checkstyle:MagicNumber")
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        FractalImageRenderer renderer = new SingleThreadedFractalImageRenderer();
        var image = renderer.render(
            FractalImage.create(800, 800),
            new Rect(-1, -1, 2, 2),
            List.of(new TransformationHeart(), new TransformationHorseHoe()), 20,
            10000,
            (short) 200,
            1,
            4334527);
        long renderEndTime = System.nanoTime();

        LOGGER.info("Rendering completed. Time taken: " + (renderEndTime - startTime) / 1_000_000 + " ms");

        ImageCorrection correction = new ImageCorrection(30);
        correction.correct(image);

        ImageUtils utils = new ImageUtils();
        utils.save(image, Path.of("corrected_img.jpeg"), ImageFormat.JPEG);
    }
}
