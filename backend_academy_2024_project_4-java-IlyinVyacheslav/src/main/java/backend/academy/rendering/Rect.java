package backend.academy.rendering;

import java.util.Random;

public record Rect(double x, double y, double width, double height) {
    public boolean contains(Point p) {
        return p.x() >= x && p.x() <= x + width && p.y() >= y && p.y() <= height + y;
    }

    public Point randomPoint(Random rand) {
        double pointX = x + rand.nextDouble() * width;
        double pointY = y + rand.nextDouble() * height;
        return new Point(pointX, pointY);
    }
}
