package backend.academy.rendering.transfromation;

import backend.academy.rendering.Point;

public class TransformationPolar implements Transformation {
    @Override
    public Point apply(Point point) {
        return new Point(Math.atan2(point.x(), point.y()) / Math.PI, point.radius() - 1);
    }
}
