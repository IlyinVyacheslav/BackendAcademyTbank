package backend.academy.rendering.transfromation;

import backend.academy.rendering.Pixel;
import backend.academy.rendering.Point;

public class TransformationHorseHoe implements Transformation {
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Pixel defaultPixel() {
        return new Pixel(13, 89, 210);
    }

    @Override
    public Point apply(Point point) {
        double r = point.radius();
        return r == 0 ? new Point(0, 0)
            : new Point((point.x() - point.y()) * (point.x() + point.y()) / r, 2 * point.x() * point.y() / r);
    }
}
