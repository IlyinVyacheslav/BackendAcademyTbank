package backend.academy.rendering.transfromation;

import backend.academy.rendering.Point;
import backend.academy.rendering.TestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class TransformationHorseHoeTest {
    private final Transformation HorseHoe = new TransformationHorseHoe();

    static Stream<Object[]> providePointsForTransformation() {
        return Stream.of(
                new Object[] {new Point(1.0, 0.0), new Point(1, 0)},
                new Object[] {new Point(3.0, 4.0), new Point(-1.4, 4.8)},
                new Object[] {new Point(4.0, 3.0), new Point(1.4, 4.8)},
                new Object[] {new Point(1.0, 1.0), new Point(0, Math.sqrt(2))},
                new Object[] {new Point(1.0, -1.0), new Point(0, -Math.sqrt(2))},
                new Object[] {new Point(0.0, 0.0), new Point(0.0, 0.0)}
        );
    }

    @ParameterizedTest
    @MethodSource("providePointsForTransformation")
    void testPolarTransform(Point point, Point transformation) {
        TestUtils.assertThatPointsAreEqual(HorseHoe.apply(point), transformation);
    }

}
