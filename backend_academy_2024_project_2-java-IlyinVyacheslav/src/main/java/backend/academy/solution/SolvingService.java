package backend.academy.solution;

import backend.academy.maze.Cell;
import backend.academy.maze.Maze;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SolvingService {
    private final static List<Direction> DIRECTIONS = List.of(
        new Direction(0, 1),
        new Direction(0, -1),
        new Direction(1, 0),
        new Direction(-1, 0)
    );
    private final Maze maze;
    private final boolean[][] visitedNodes;

    public SolvingService(Maze maze) {
        this.maze = maze;
        this.visitedNodes = new boolean[maze.getHeight()][maze.getWidth()];
    }

    public boolean isInputValid(Coordinate start, Coordinate end) {
        return isFree(start) && isFree(end) && maze.getHeight() > 1 && maze.getWidth() > 1;
    }

    public void markNodeVisited(Node node) {
        visitedNodes[node.coordinate().row()][node.coordinate().col()] = true;
    }

    public boolean isNodeVisited(Node node) {
        return visitedNodes[node.coordinate().row()][node.coordinate().col()];
    }

    private boolean isFree(Coordinate c) {
        return c.row() >= 0 && c.row() < maze.getHeight() && c.col() >= 0 && c.col() < maze.getWidth()
            && maze.getGrid()[c.row()][c.col()].type() != Cell.Type.WALL;
    }

    public List<Coordinate> reconstructPath(Node node) {
        Node currentNode = node;
        List<Coordinate> path = new ArrayList<>();
        while (currentNode != null) {
            path.add(currentNode.coordinate());
            currentNode = currentNode.parent();
        }
        Collections.reverse(path);
        return path;
    }

    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (var direction : DIRECTIONS) {
            int row = node.coordinate().row() + direction.deltaRow;
            int col = node.coordinate().col() + direction.deltaCol;

            Coordinate neighborCoordinate = new Coordinate(row, col);
            if (isFree(neighborCoordinate)) {
                neighbors.add(new Node(node, neighborCoordinate));
            }
        }
        return neighbors;
    }

    private record Direction(int deltaRow, int deltaCol) {
    }
}
