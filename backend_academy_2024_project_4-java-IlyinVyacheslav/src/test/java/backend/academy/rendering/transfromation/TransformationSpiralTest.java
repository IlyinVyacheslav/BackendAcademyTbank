package backend.academy.rendering.transfromation;

import backend.academy.rendering.Point;
import backend.academy.rendering.TestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

class TransformationSpiralTest {
    private final Transformation SPIRAL = new TransformationSpiral();

    static Stream<Object[]> providePointsForTransformation() {
        return Stream.of(
            new Object[] {new Point(1.0, 1.0), new Point(1.1984559, 0.3897311)},
            new Object[] {new Point(-1.0, 1.0), new Point(1.1984559, -0.6102688)},
            new Object[] {new Point(0.0, 1.0), new Point(1.84147098, -0.5403023)}
        );
    }

    @ParameterizedTest
    @MethodSource("providePointsForTransformation")
    void testSpiralTransform(Point point, Point transformation) {
        TestUtils.assertThatPointsAreEqual(SPIRAL.apply(point), transformation);
    }

}
