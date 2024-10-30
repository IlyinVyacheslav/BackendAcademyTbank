package backend.academy.solution;

import backend.academy.maze.Maze;
import java.util.List;

/**
 * Interface for maze solvers.
 */
public interface Solver {
    /**
     * Attempts to find a path in maze.
     *
     * @param maze  the maze to search the path in.
     * @param start the coordinate of path start.
     * @param end   the coordinate of path end.
     * @return a list of coordinates representing the path, or an empty list if no path is found.
     */
    List<Coordinate> solve(Maze maze, Coordinate start, Coordinate end);
}
