package backend.academy.rendering.transfromation;

import backend.academy.rendering.Point;
import backend.academy.rendering.TestUtils;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TransformationPolarTest {
    private final Transformation POLAR = new TransformationPolar();

    static Stream<Object[]> providePointsForTransformation() {
        return Stream.of(
            new Object[] {new Point(1.0, 1.0), new Point(0.25, Math.sqrt(2) - 1)},
            new Object[] {new Point(-1.0, 1.0), new Point(-0.25, Math.sqrt(2) - 1)},
            new Object[] {new Point(1.0, 0.0), new Point(0.5, 0)},
            new Object[] {new Point(0.0, 0.0), new Point(0.0, -1.0)}
        );
    }

    @ParameterizedTest
    @MethodSource("providePointsForTransformation")
    void testPolarTransform(Point point, Point transformation) {
        TestUtils.assertThatPointsAreEqual(POLAR.apply(point), transformation);
    }

}
