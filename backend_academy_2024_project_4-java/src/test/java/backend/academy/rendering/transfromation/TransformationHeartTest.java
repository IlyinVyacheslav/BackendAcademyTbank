package backend.academy.rendering.transfromation;

import backend.academy.rendering.Point;
import backend.academy.rendering.TestUtils;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TransformationHeartTest {
    private final Transformation HEART = new TransformationHeart();

    static Stream<Object[]> providePointsForTransformation() {
        return Stream.of(
            new Object[] {new Point(1.0, 1.0), new Point(1.267162131330, -0.62793322329)},
            new Object[] {new Point(-1.0, 1.0), new Point(-1.267162131330, -0.62793322329)},
            new Object[] {new Point(0.0, 0.0), new Point(0.0, 0.0)}
        );
    }

    @ParameterizedTest
    @MethodSource("providePointsForTransformation")
    void testHeartTransform(Point point, Point transformation) {
        TestUtils.assertThatPointsAreEqual(HEART.apply(point), transformation);
    }
}
