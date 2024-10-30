package backend.academy;

import backend.academy.maze.Cell;
import backend.academy.maze.Maze;
import backend.academy.solution.Coordinate;
import java.util.ArrayList;
import java.util.List;

public class TestService {
    public static Maze getMazeFromCellTypes(Cell.Type[][] types) {
        Cell[][] cells = new Cell[types.length][types[0].length];
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < types[i].length; j++) {
                cells[i][j] = new Cell(i, j, types[i][j]);
            }
        }
        return new Maze(cells.length, cells[0].length, cells);
    }

    public static Maze getMazeFromString(String maze) {
        String[] split = maze.split("\n");
        int height = split.length;
        int width = split[0].length();
        Cell[][] cells = new Cell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell.Type type = Cell.Type.fromSymbol(split[y].charAt(x));
                cells[y][x] = new Cell(y, x, type);
            }
        }
        return new Maze(height, width, cells);
    }

    public static List<Coordinate> getPathFromArray(int[][] coordinates) {
        List<Coordinate> path = new ArrayList<>();
        for (int[] coordinate : coordinates) {
            if (coordinate.length != 2) {
                throw new IllegalStateException(
                    "Nested array should contain 2 numbers, found: " + coordinate.length);
            }
            path.add(new Coordinate(coordinate[0], coordinate[1]));
        }
        return path;
    }
}
