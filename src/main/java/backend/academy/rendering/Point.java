package backend.academy.rendering;

public record Point(double x, double y) {
    public Point rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Point(x * cos - y * sin, y * cos + x * sin);
    }

    public double radius() {
        return Math.sqrt(x * x + y * y);
    }
}
