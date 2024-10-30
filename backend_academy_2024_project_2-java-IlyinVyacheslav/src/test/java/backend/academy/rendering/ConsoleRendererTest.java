package backend.academy.rendering;

import backend.academy.maze.Cell;
import backend.academy.maze.Maze;
import backend.academy.solution.Coordinate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static backend.academy.TestService.getMazeFromCellTypes;
import static backend.academy.TestService.getPathFromArray;
import static backend.academy.maze.Cell.Type.BUSH;
import static backend.academy.maze.Cell.Type.GOLD;
import static backend.academy.maze.Cell.Type.PASSAGE;
import static backend.academy.maze.Cell.Type.WALL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsoleRendererTest {
    private static final ConsoleRenderer RENDERER = new ConsoleRenderer();
    private static final Maze SQUARE_MAZE_WITH_PASSAGE = getMazeFromCellTypes(new Cell.Type[][] {
        {WALL, WALL, PASSAGE, WALL, WALL},
        {WALL, PASSAGE, PASSAGE, PASSAGE, WALL},
        {WALL, PASSAGE, WALL, PASSAGE, WALL},
        {WALL, PASSAGE, PASSAGE, WALL, WALL},
        {WALL, WALL, PASSAGE, WALL, WALL}
    });

    private static void assertThrowsIncorrectPathException(Maze maze, Coordinate c, String condition) {
        assertThatThrownBy(() -> RENDERER.render(maze, List.of(c)))
            .as(String.format("If %s IncorrectPathException should be thrown", condition))
            .isInstanceOf(IncorrectPathException.class);
    }

    private String unifyLineSeparator(String s) {
        return s.replace("\n", System.lineSeparator());
    }

    @Test
    void testEmptyMaze() {
        Maze emptyMaze = new Maze(0, 0, new Cell[0][0]);
        String expected = "";

        String actual = RENDERER.render(emptyMaze);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testOneCellMaze() {
        Maze oneCellMaze = getMazeFromCellTypes(new Cell.Type[][] {
            {WALL}
        });
        String expected = "#";

        String actual = RENDERER.render(oneCellMaze);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testSquareMaze3x3() {
        Maze squareMaze = getMazeFromCellTypes(new Cell.Type[][] {
            {WALL, WALL, WALL}, {WALL, PASSAGE, WALL}, {WALL, WALL, WALL}
        });
        String expected = unifyLineSeparator("""
            ###
            # #
            ###""");

        String actual = RENDERER.render(squareMaze);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testSquareMazeWithPassage() {
        String expected = unifyLineSeparator("""
            ## ##
            #   #
            # # #
            #  ##
            ## ##""");

        String actual = RENDERER.render(SQUARE_MAZE_WITH_PASSAGE);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testSquareMazeWithPassageWithEmptyPath() {
        String expected = unifyLineSeparator("""
            ## ##
            #   #
            # # #
            #  ##
            ## ##""");

        String actual = RENDERER.render(SQUARE_MAZE_WITH_PASSAGE, List.of());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testSquareMazeWithPassageWithCorrectPath() {
        List<Coordinate> path = getPathFromArray(new int[][] {
            {0, 2}, {1, 2}, {1, 1}, {2, 1},
            {3, 1}, {3, 2}, {4, 2}
        });
        String expected = unifyLineSeparator("""
            ##S##
            #.. #
            #.# #
            #..##
            ##E##""");

        String actual = RENDERER.render(SQUARE_MAZE_WITH_PASSAGE, path);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testMazeWithWrongPath() {
        Maze maze = getMazeFromCellTypes(new Cell.Type[][] {
            {WALL, PASSAGE, WALL, WALL, WALL},
            {WALL, PASSAGE, PASSAGE, PASSAGE, WALL},
            {WALL, WALL, WALL, PASSAGE, WALL}
        });
        List<Coordinate> outOfMazeCoordinates = getPathFromArray(new int[][] {
            {-1, 0}, {4, 1}, {0, -1}, {0, 5}
        });
        List<Coordinate> ofWallCoordinate = List.of(new Coordinate(0, 0));

        for (var c : outOfMazeCoordinates) {
            assertThrowsIncorrectPathException(maze, c, "coordinate is out of maze");
        }
        assertThrowsIncorrectPathException(maze, ofWallCoordinate.getFirst(), "path goes through wall");
    }

    @Test
    void testMazeWithGoldCells() {
        Maze maze = getMazeFromCellTypes(new Cell.Type[][] {
            {WALL, PASSAGE, WALL, WALL, WALL},
            {WALL, GOLD, PASSAGE, GOLD, WALL},
            {WALL, WALL, GOLD, PASSAGE, WALL}
        });
        String expected = unifyLineSeparator("""
            # ###
            #* *#
            ##* #""");

        String actual = RENDERER.render(maze);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testMazeWithBushCells() {
        Maze maze = getMazeFromCellTypes(new Cell.Type[][] {
            {WALL, WALL, WALL, WALL, WALL},
            {PASSAGE, PASSAGE, BUSH, WALL, WALL},
            {WALL, BUSH, WALL, PASSAGE, WALL},
            {WALL, PASSAGE, BUSH, PASSAGE, WALL},
            {WALL, WALL, WALL, WALL, WALL}
        });
        String expected = unifyLineSeparator("""
            #####
              ~##
            #~# #
            # ~ #
            #####""");

        String actual = RENDERER.render(maze);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testDifferentCellTypesMazeWithPath() {
        Maze maze = getMazeFromCellTypes(new Cell.Type[][] {
            {WALL, WALL, WALL, WALL, WALL},
            {PASSAGE, GOLD, BUSH, PASSAGE, WALL},
            {WALL, BUSH, WALL, GOLD, PASSAGE},
            {WALL, PASSAGE, WALL, PASSAGE, WALL},
            {WALL, GOLD, PASSAGE, PASSAGE, WALL},
            {WALL, WALL, WALL, WALL, WALL}
        });
        List<Coordinate> path = getPathFromArray(new int[][] {
            {1, 0}, {1, 1}, {2, 1}, {3, 1}, {4, 1},
            {4, 2}, {4, 3}, {3, 3}, {2, 3}, {2, 4}
        });
        String expected = unifyLineSeparator("""
            #####
            S*~ #
            #~#*E
            #.#.#
            #*..#
            #####""");

        String actual = RENDERER.render(maze, path);

        assertThat(actual).isEqualTo(expected);
    }
}
