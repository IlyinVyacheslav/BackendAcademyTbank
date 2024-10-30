package backend.academy.rendering;

import backend.academy.maze.Maze;
import backend.academy.solution.Coordinate;
import java.util.List;

public interface Renderer {
    String render(Maze maze);

    String render(Maze maze, List<Coordinate> path);
}
