package backend.academy.rendering.transfromation;

import backend.academy.rendering.Point;

public class TransformationSwirl implements Transformation {
    @Override
    public Point apply(Point point) {
        double squareR = Math.pow(point.radius(), 2);
        double sinR = Math.sin(squareR);
        double cosR = Math.cos(squareR);
        return new Point(point.x() * sinR - point.y() * cosR, point.x() * cosR + point.y() * sinR);
    }
}
