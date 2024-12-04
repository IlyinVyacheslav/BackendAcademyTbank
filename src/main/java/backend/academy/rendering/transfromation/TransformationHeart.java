package backend.academy.rendering.transfromation;

import backend.academy.rendering.Pixel;
import backend.academy.rendering.Point;

public class TransformationHeart implements Transformation {
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Pixel defaultPixel() {
        return new Pixel(100, 10, 89);
    }

    @Override
    public Point apply(Point point) {
        double r = point.radius();
        double theta = Math.atan2(point.x(), point.y());
        return new Point(r * Math.sin(theta * r), -r * Math.cos(theta * r));
    }
}
