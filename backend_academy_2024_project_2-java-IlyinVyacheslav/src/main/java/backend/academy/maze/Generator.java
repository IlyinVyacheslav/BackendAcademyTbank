package backend.academy.maze;

/**
 * Interface for maze generators.
 */
public interface Generator {
    /**
     * Generates a maze with the given params
     *
     * @param height the number of cells in vertical direction.
     * @param width  the number of cells in horizontal direction.
     * @return a Maze of dimension <code>(2 * height + 1) × (2 * width + 1)</code>
     *     representing the generated maze.
     */
    Maze generate(int height, int width);
}
