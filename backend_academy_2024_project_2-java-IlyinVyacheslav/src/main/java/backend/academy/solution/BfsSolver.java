package backend.academy.solution;

import backend.academy.maze.Maze;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * An implementation of the {@link Solver} interface that uses BFS algorithm
 * to find the shortest path through a maze.
 */
public class BfsSolver implements Solver {

    @Override
    public List<Coordinate> solve(Maze maze, Coordinate start, Coordinate end) {
        SolvingService solvingService = new SolvingService(maze);

        if (!solvingService.isInputValid(start, end)) {
            return Collections.emptyList();
        }

        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(null, start));

        while (!queue.isEmpty()) {
            Node node = queue.remove();
            if (node.coordinate().equals(end)) {
                return solvingService.reconstructPath(node);
            }

            solvingService.markNodeVisited(node);

            for (Node neighbor : solvingService.getNeighbors(node)) {
                if (!solvingService.isNodeVisited(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }
}
