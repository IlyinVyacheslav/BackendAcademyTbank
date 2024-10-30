package backend.academy.solution;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AstarSolverTest extends AbstractSolverTest {

    @Override
    protected Solver getSolver() {
        return new AstarSolver();
    }

    @Nested
    class AdvancedPathTests {
        @Test
        void testShortestPathWithoutGold() {
            String maze = """
                # # #
                #   #
                # # #
                #  *#
                #####
                """;
            int[][] expectedPathArray = {
                {0, 1}, {1, 1}, {1, 2}, {1, 3}, {0, 3}
            };

            assertMazeHasPath(maze, expectedPathArray);
        }

        @Test
        void testPathWithGold() {
            String maze = """
                ## ##
                #   #
                #*# #
                #   #
                ## ##
                """;
            int[][] expectedPathArray = {
                {0, 2}, {1, 2}, {1, 1},
                {2, 1}, {3, 1}, {3, 2},
                {4, 2}
            };

            assertMazeHasPath(maze, expectedPathArray);
        }

        @Test
        void testPathWithoutBush() {
            String maze = """
                ## ##
                #   #
                # #~#
                #   #
                ## ##
                """;
            int[][] expectedPathArray = {
                {0, 2}, {1, 2}, {1, 1},
                {2, 1}, {3, 1}, {3, 2},
                {4, 2}
            };

            assertMazeHasPath(maze, expectedPathArray);
        }

        @Test
        void testPathWithBushAndGold() {
            String maze = """
                ## ##
                #   #
                #~# #
                #*  #
                ## ##
                """;
            int[][] expectedPathArray = {
                {0, 2}, {1, 2}, {1, 1},
                {2, 1}, {3, 1}, {3, 2},
                {4, 2}
            };

            assertMazeHasPath(maze, expectedPathArray);
        }
    }
}
