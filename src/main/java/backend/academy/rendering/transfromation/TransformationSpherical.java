package backend.academy.rendering.transfromation;

import backend.academy.rendering.Pixel;
import backend.academy.rendering.Point;

public class TransformationSpherical implements Transformation {
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Pixel defaultPixel() {
        return new Pixel(10, 67, 67);
    }

    @Override
    public Point apply(Point point) {
        double squareR = Math.pow(point.radius(), 2);
        return squareR == 0 ? new Point(0, 0) : new Point(point.x() / squareR, point.y() / squareR);
    }
}
