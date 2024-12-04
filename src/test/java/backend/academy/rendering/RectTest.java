package backend.academy.rendering;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;

class RectTest {
    static Stream<Object[]> provideDataForContains() {
        return Stream.of(
            new Object[] {new Rect(0.0, 0.0, 10.0, 10.0), new Point(5.0, 5.0), true},
            new Object[] {new Rect(0.0, 0.0, 10.0, 10.0), new Point(10.0, 10.0), true},
            new Object[] {new Rect(0.0, 0.0, 10.0, 10.0), new Point(0.0, 0.0), true},
            new Object[] {new Rect(0.0, 0.0, 10.0, 10.0), new Point(15.0, 5.0), false},
            new Object[] {new Rect(0.0, 0.0, 10.0, 10.0), new Point(5.0, 15.0), false},
            new Object[] {new Rect(5.0, 5.0, 10.0, 10.0), new Point(10.0, 10.0), true},
            new Object[] {new Rect(5.0, 5.0, 10.0, 10.0), new Point(4.0, 4.0), false},
            new Object[] {new Rect(-5.0, -5.0, 10.0, 10.0), new Point(-5.0, -1.0), true},
            new Object[] {new Rect(-5.0, -5.0, 5.0, 2.0), new Point(-4.0, 1.0), false},
            new Object[] {new Rect(-5.0, -5.0, 5.0, 2.0), new Point(1.0, -1.0), false}
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForContains")
    void testContains(Rect rect, Point point, boolean expected) {
        assertThat(rect.contains(point)).isEqualTo(expected);
    }

}
