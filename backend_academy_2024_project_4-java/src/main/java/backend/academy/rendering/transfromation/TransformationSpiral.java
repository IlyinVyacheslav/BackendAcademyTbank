package backend.academy.rendering.transfromation;

import backend.academy.rendering.Point;

public class TransformationSpiral implements Transformation {

    @Override
    public Point apply(Point point) {
        double r = point.radius();
        double atan = Math.atan2(point.x(), point.y());
        return new Point((Math.cos(atan) + Math.sin(r)) / r, (Math.sin(atan) - Math.cos(r)) / r);
    }
}
