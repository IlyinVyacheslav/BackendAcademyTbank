package backend.academy.rendering.transfromation;

import backend.academy.rendering.Point;
import backend.academy.rendering.TestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

class TransformationSwirlTest {
    private final Transformation SWIRL = new TransformationSwirl();

    static Stream<Object[]> providePointsForTransformation() {
        return Stream.of(
            new Object[] {new Point(1.0, 1.0), new Point(1.32544426, 0.49315059)},
            new Object[] {new Point(-1.0, 1.0), new Point(-0.49315059, 1.32544426)},
            new Object[] {new Point(1.0, 0.0), new Point(0.84147098, 0.54030230)},
            new Object[] {new Point(0.0, 0.0), new Point(0.0, 0.0)}
        );
    }

    @ParameterizedTest
    @MethodSource("providePointsForTransformation")
    void testPolarTransform(Point point, Point transformation) {
        TestUtils.assertThatPointsAreEqual(SWIRL.apply(point), transformation);
    }
}
