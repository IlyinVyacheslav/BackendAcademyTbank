package backend.academy.rendering;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class TestUtils {

    public static final double ERROR = 1e-6;

    public static void assertThatPointsAreEqual(Point actual, Point expected) {
        assertThat(actual.x()).isCloseTo(expected.x(), within(ERROR));
        assertThat(actual.y()).isCloseTo(expected.y(), within(ERROR));
    }

    public static void assertThatDoublesAreEqual(double actual, double expected) {
        assertThat(actual).isCloseTo(expected, within(ERROR));
    }
}
