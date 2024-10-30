package backend.academy.rendering;

import backend.academy.maze.Cell;
import backend.academy.maze.Maze;
import backend.academy.solution.Coordinate;
import java.util.Collections;
import java.util.List;

public class ConsoleRenderer implements Renderer {

    @Override
    public String render(Maze maze) {
        return render(maze, Collections.emptyList());
    }

    @Override
    public String render(Maze maze, List<Coordinate> path) {
        char[][] chars = new char[maze.getHeight()][maze.getWidth()];
        for (int y = 0; y < chars.length; y++) {
            for (int x = 0; x < chars[y].length; x++) {
                chars[y][x] = maze.getGrid()[y][x].type().symbol();
            }
        }

        for (int i = 0; i < path.size(); i++) {
            Coordinate step = path.get(i);

            validateCoordinate(maze, step);

            if (i == 0) {
                chars[step.row()][step.col()] = 'S';
            } else if (i == path.size() - 1) {
                chars[step.row()][step.col()] = 'E';
            } else if (maze.getGrid()[step.row()][step.col()].type() == Cell.Type.PASSAGE) {
                chars[step.row()][step.col()] = '.';
            }
        }
        return charArrayToString(chars);
    }

    private void validateCoordinate(Maze maze, Coordinate step) {
        if (!isCoordinateInMaze(step, maze.getHeight(), maze.getWidth())) {
            throw new IncorrectPathException(
                String.format("Coordinate (%d,%d) is out of maze with height: %d, width: %d.", step.row(),
                    step.col(),
                    maze.getHeight(), maze.getWidth()));
        } else if (maze.getGrid()[step.row()][step.col()].type() == Cell.Type.WALL) {
            throw new IncorrectPathException(
                String.format("Coordinate (%d, %d) can not be path, as it is wall.", step.row(), step.col()));
        }
    }

    private boolean isCoordinateInMaze(Coordinate coordinate, int height, int width) {
        return coordinate.row() >= 0 && coordinate.row() < height && coordinate.col() >= 0 && coordinate.col() < width;
    }

    private String charArrayToString(char[][] chars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            for (var ch : chars[i]) {
                sb.append(ch);
            }
            sb.append(i != chars.length - 1 ? System.lineSeparator() : "");
        }
        return sb.toString();
    }
}
