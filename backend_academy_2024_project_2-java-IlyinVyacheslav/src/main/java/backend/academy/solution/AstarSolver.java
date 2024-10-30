package backend.academy.solution;

import backend.academy.maze.Cell;
import backend.academy.maze.Maze;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * An implementation of the {@link Solver} interface that uses A-star algorithm
 * to find an optimal path through a maze.
 */
public class AstarSolver implements Solver {
    private final static Map<Cell.Type, Integer> CELLS_WEIGHT_MAP = Map.of(
        Cell.Type.WALL, Integer.MAX_VALUE,
        Cell.Type.PASSAGE, 3,
        Cell.Type.GOLD, 0,
        Cell.Type.BUSH, 5
    );
    private Maze maze;
    private Coordinate end;

    @Override
    public List<Coordinate> solve(Maze maze, Coordinate start, Coordinate end) {
        this.maze = maze;
        this.end = end;
        SolvingService solvingService = new SolvingService(maze);

        if (!solvingService.isInputValid(start, end)) {
            return Collections.emptyList();
        }

        Map<Node, Double> f = new HashMap<>();
        Map<Node, Double> g = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(f::get));

        Node startNode = new Node(null, start);
        queue.add(startNode);
        g.put(startNode, 0.0);
        f.put(startNode, 0.0 + heuristics(start));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.coordinate().equals(end)) {
                return solvingService.reconstructPath(node);
            }

            solvingService.markNodeVisited(node);

            for (Node neighbor : solvingService.getNeighbors(node)) {
                if (solvingService.isNodeVisited(neighbor)) {
                    continue;
                }

                double tentativeScore = g.get(node) + getWeight(neighbor.coordinate());
                if (tentativeScore < g.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    g.put(neighbor, tentativeScore);
                    f.put(neighbor, g.get(neighbor) + heuristics(neighbor.coordinate()));
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    private int getWeight(Coordinate c) {
        return CELLS_WEIGHT_MAP.get(maze.getGrid()[c.row()][c.col()].type());
    }

    private double heuristics(Coordinate a) {
        return Math.abs(a.row() - end.row()) + Math.abs(a.col() - end.col());
    }
}
