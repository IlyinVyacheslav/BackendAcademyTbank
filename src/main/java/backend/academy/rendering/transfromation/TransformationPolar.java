package backend.academy.rendering.transfromation;

import backend.academy.rendering.Pixel;
import backend.academy.rendering.Point;

public class TransformationPolar implements Transformation {
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Pixel defaultPixel() {
        return new Pixel(0, 80, 0);
    }

    @Override
    public Point apply(Point point) {
        return new Point(Math.atan2(point.x(), point.y()) / Math.PI, point.radius() - 1);
    }
}
