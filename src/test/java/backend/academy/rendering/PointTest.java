package backend.academy.rendering;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class PointTest {

    static Stream<Object[]> provideDataForRadius() {
        return Stream.of(
            new Object[] {0.0, 0.0, 0.0},
            new Object[] {3.0, 4.0, 5.0},
            new Object[] {-3.0, -4.0, 5.0},
            new Object[] {12.0, -5.0, 13.0},
            new Object[] {1.0, 1.0, Math.sqrt(2)}
        );
    }

    static Stream<Object[]> provideDataForRotate() {
        return Stream.of(
            new Object[] {new Point(0.0, 0.0), 0.0, new Point(0.0, 0.0)},
            new Object[] {new Point(0.0, 0.0), Math.PI, new Point(0.0, 0.0)},
            new Object[] {new Point(1.5, -1.5), Math.PI * 2, new Point(1.5, -1.5)},
            new Object[] {new Point(1.0, 0.0), Math.PI / 2, new Point(0.0, 1.0)},
            new Object[] {new Point(1.0, 0.0), Math.PI, new Point(-1.0, 0.0)},
            new Object[] {new Point(1.0, 0.0), Math.PI / 4, new Point(Math.sqrt(2) / 2, Math.sqrt(2) / 2)}
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForRadius")
    void testPointRadius(double x, double y, double radius) {
        Point point = new Point(x, y);
        TestUtils.assertThatDoublesAreEqual(point.radius(), radius);
    }

    @ParameterizedTest
    @MethodSource("provideDataForRotate")
    void testRotate(Point point, double angle, Point expected) {
        Point rotatedPoint = point.rotate(angle);

        TestUtils.assertThatPointsAreEqual(rotatedPoint, expected);
    }
}
