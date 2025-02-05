package backend.academy.rendering.transfromation;

import backend.academy.rendering.Point;
import backend.academy.rendering.TestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

class TransformationSphericalTest {
    private final Transformation SPHERICAL = new TransformationSpherical();

    static Stream<Object[]> providePointsForTransformation() {
        return Stream.of(
            new Object[] {new Point(1.0, 1.0), new Point(0.5, 0.5)},
            new Object[] {new Point(-1.0, 1.0), new Point(-0.5, 0.5)},
            new Object[] {new Point(1.0, 0.0), new Point(1.0, 0.0)},
            new Object[] {new Point(0.0, 0.0), new Point(0.0, 0.0)},
            new Object[] {new Point(2.0, 1.0), new Point(0.4, 0.2)}
        );
    }

    @ParameterizedTest
    @MethodSource("providePointsForTransformation")
    void testPolarTransform(Point point, Point transformation) {
        TestUtils.assertThatPointsAreEqual(SPHERICAL.apply(point), transformation);
    }

}
