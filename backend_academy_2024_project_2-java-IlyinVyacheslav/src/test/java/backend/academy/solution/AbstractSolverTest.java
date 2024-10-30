package backend.academy.solution;

import backend.academy.maze.Maze;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static backend.academy.TestService.getMazeFromString;
import static backend.academy.TestService.getPathFromArray;
import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractSolverTest {
    private Solver SOLVER;

    protected abstract Solver getSolver();

    protected void assertMazeHasPath(String visualization, int[][] expectedPathArray) {
        Maze maze = getMazeFromString(visualization);
        List<Coordinate> expectedPath = getPathFromArray(expectedPathArray);

        List<Coordinate> actualPath = SOLVER.solve(maze, expectedPath.getFirst(), expectedPath.getLast());

        assertThat(actualPath).isEqualTo(expectedPath);
    }

    @BeforeEach
    void initializeSolver() {
        SOLVER = getSolver();
    }

    @Nested
    class BasicPathTests {
        @Test
        void testMazePathWithStartOnBorder() {
            String maze = """
                # ###
                # # #
                #   #
                #####
                """;
            int[][] expectedPathArray = {{0, 1}, {1, 1}, {2, 1}, {2, 2}, {2, 3}, {1, 3}};

            assertMazeHasPath(maze, expectedPathArray);
        }

        @Test
        void testMazePathWithEndOnBorder() {
            String maze = """
                #########
                #   #   #
                # #   # #
                ####### #
                """;
            int[][] expectedPathArray = {
                {2, 1}, {1, 1}, {1, 2}, {1, 3},
                {2, 3}, {2, 4}, {2, 5}, {1, 5},
                {1, 6}, {1, 7}, {2, 7}, {3, 7}
            };

            assertMazeHasPath(maze, expectedPathArray);
        }

        @Test
        void testMazeWithCoincidentStartAndEnd() {
            String maze = """
                ###
                # #
                ###
                """;
            int[][] expectedPathArray = {{1, 1}};

            assertMazeHasPath(maze, expectedPathArray);
        }

        @Test
        void testMazeWithTwoPaths() {
            String maze = """
                #######
                #     #
                # ### #
                #     #
                #######
                """;
            int[][] expectedPathArray = {{1, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 5}, {3, 5}};

            assertMazeHasPath(maze, expectedPathArray);
        }

        @Test
        void testEulerMaze() {
            String maze = """
                #####################
                # #       # #   # # #
                # # ##### # # # # # #
                # # # # # # # # ### #
                #   # # # # # #   # #
                # ### # # # # # ### #
                #   # # #   # # #   #
                ### ### # # # # # # #
                #     # # # # # # # #
                ### ### # # # # # # #
                #       # #   #   # #
                #####################
                """;
            int[][] expectedPathArray = {
                {3, 3}, {2, 3}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {1, 8}, {1, 9},
                {2, 9}, {3, 9}, {4, 9}, {5, 9}, {6, 9}, {6, 10}, {6, 11}, {7, 11}, {8, 11},
                {9, 11}, {10, 11}, {10, 12}, {10, 13}, {9, 13}, {8, 13}, {7, 13}, {6, 13},
                {5, 13}, {4, 13}, {3, 13}, {2, 13}, {1, 13}, {1, 14}, {1, 15}, {2, 15},
                {3, 15}, {4, 15}, {5, 15}, {6, 15}, {7, 15}, {8, 15}, {9, 15}, {10, 15},
                {10, 16}, {10, 17}, {9, 17}, {8, 17}, {7, 17}, {6, 17}, {6, 18}, {6, 19},
                {5, 19}, {4, 19}, {3, 19}, {2, 19}, {1, 19}
            };

            assertMazeHasPath(maze, expectedPathArray);
        }

        @Test
        void testMazeWithNoPath() {
            Maze maze = getMazeFromString("""
                #####
                # # #
                #####
                """);
            Coordinate start = new Coordinate(1, 1);
            Coordinate end = new Coordinate(1, 3);

            List<Coordinate> actualPath = SOLVER.solve(maze, start, end);

            assertThat(actualPath).isEqualTo(List.of());
        }

        @Test
        void testMazeWithPathThroughBush() {
            String maze = """
                # ##
                #~ #
                ## #
                """;
            int[][] expectedPathArray = {{0, 1}, {1, 1}, {1, 2}, {2, 2}};

            assertMazeHasPath(maze, expectedPathArray);
        }

        @Test
        void testMazeWithGoldAndBushCells() {
            String maze = """
                # ## #
                #~ #~#
                ## * #
                ######
                """;
            int[][] expectedPathArray = {
                {0, 1}, {1, 1}, {1, 2}, {2, 2},
                {2, 3}, {2, 4}, {1, 4}, {0, 4}
            };

            assertMazeHasPath(maze, expectedPathArray);
        }
    }
}
