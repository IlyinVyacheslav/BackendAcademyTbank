package backend.academy;

import backend.academy.rendering.FractalImageRenderer;
import backend.academy.rendering.MultipleThreadedFractalImageRenderer;
import backend.academy.rendering.Rect;
import backend.academy.rendering.transfromation.TransformationHeart;
import backend.academy.rendering.transfromation.TransformationHorseHoe;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@UtilityClass
public class Main {
    public static final int ITER_PER_SAMPLE = 200;
    public static final int IMAGE_WIDTH = 800;
    public static final int IMAGE_HEIGHT = 800;
    public static final int SAMPLES = 10000;
    public static final int AFFINE_TRANSFORMS_NUMBER = 20;
    public static final String RESULTS_FILE = "rendering_comparison.txt";
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static final int MAX_THREAD_NUMBER = 16;

    public static void main(String[] args) {
        for (int i = 1; i < MAX_THREAD_NUMBER; i++) {
            double time = renderingTimeMs(new MultipleThreadedFractalImageRenderer(i));
            addResultToFile(Path.of(RESULTS_FILE), i, time);
        }
    }

    public void addResultToFile(Path path, int threads, double renderingTime) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), StandardCharsets.UTF_8, true))) {
            writer.write("Rendering Parameters:\n");
            writer.write("Number of threads: " + threads + "\n");
            writer.write("Image width: " + IMAGE_WIDTH + "\n");
            writer.write("Image height: " + IMAGE_HEIGHT + "\n");
            writer.write("Max iterations: " + SAMPLES + "\n");
            writer.write("Steps number: " + ITER_PER_SAMPLE + "\n");
            writer.write("Affine transformations: " + AFFINE_TRANSFORMS_NUMBER + "\n");
            writer.write("Rendering time: " + renderingTime + " ms\n");
            writer.write("--------------------------------------------------\n");
        } catch (IOException e) {
            LOGGER.error("Error writing to file", e);
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public double renderingTimeMs(FractalImageRenderer renderer) {
        long startTime = System.nanoTime();
        renderer.render(
            FractalImage.create(IMAGE_WIDTH, IMAGE_HEIGHT),
            new Rect(-1, -1, 2, 2),
            List.of(new TransformationHeart(), new TransformationHorseHoe()), AFFINE_TRANSFORMS_NUMBER,
            SAMPLES,
            (short) ITER_PER_SAMPLE,
            1,
            4334527);
        long renderEndTime = System.nanoTime();
        return (double) (renderEndTime - startTime) / 1_000_000;
    }
}
