package backend.academy.rendering.affine;

import backend.academy.rendering.Pixel;
import backend.academy.rendering.Point;

public record AffineTransformation(
    double a,
    double b,
    double c,
    double d,
    double e,
    double f,
    Pixel.RGB color) {

    private static double sumOfSquares(double... values) {
        double sum = 0.0;
        for (double value : values) {
            sum += Math.pow(value, 2);
        }
        return sum;
    }

    public boolean isValid() {
        double adSquare = sumOfSquares(a, d);
        double beSquare = sumOfSquares(b, e);
        return adSquare <= 1
               && beSquare <= 1
               && adSquare + beSquare <= 1 + Math.pow(a * e - b * d, 2);
    }

    public Point transformPoint(Point p) {
        double newX = p.x() * a + p.y() * b + c;
        double newY = p.x() * d + p.y() * e + f;
        return new Point(newX, newY);
    }
}
