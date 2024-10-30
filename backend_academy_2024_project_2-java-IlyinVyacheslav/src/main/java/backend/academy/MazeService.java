package backend.academy;

import backend.academy.maze.EulerGenerator;
import backend.academy.maze.Generator;
import backend.academy.maze.Maze;
import backend.academy.maze.PrimsGenerator;
import backend.academy.rendering.ConsoleRenderer;
import backend.academy.rendering.Renderer;
import backend.academy.solution.AstarSolver;
import backend.academy.solution.BfsSolver;
import backend.academy.solution.Coordinate;
import backend.academy.solution.Solver;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Scanner;

/**
 * The {@link MazeService} class provides a command-line interface for generating and solving mazes.
 * It allows to choose a maze generation algorithm(e.g., Prim's algorithm, Euler algorithm) and
 * a maze solving algorithm(e.g., BFS, A*), then interactively provides options for maze size
 * and start/end coordinates. The result is rendered to the console.
 */
public class MazeService {
    /**
     * The default height of the maze if the user enters invalid option.
     */
    public static final int DEFAULT_HEIGHT = 7;
    /**
     * The default width of the maze if the user enters invalid option.
     */
    public static final int DEFAULT_WIDTH = 14;
    private final static Map<String, Class<? extends Generator>> GENERATORS = Map.of(
        "Prim's algorithm", PrimsGenerator.class,
        "Euler algorithm", EulerGenerator.class
    );
    private final static Map<String, Class<? extends Solver>> SOLVERS = Map.of(
        "BFS algorithm", BfsSolver.class,
        "A* algorithm", AstarSolver.class
    );
    private final static SecureRandom RANDOM = new SecureRandom();
    private final Scanner scanner;
    private final PrintStream out;

    /**
     * Constructs a MazeService that reads user input and outputs results to the provided streams.
     *
     * @param in  the input stream to read from.
     * @param out the output stream to write to.
     */
    public MazeService(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
        this.out = out;
    }

    /**
     * Displays the list of available maze generation and solving algorithms, then generates a maze
     * and solves it according to the user's choices. The maze is rendered to the console at each step.
     */
    public void showMazeAlgorithms() {
        int height = readParameter("Enter maze height:", DEFAULT_HEIGHT);
        int width = readParameter("Enter maze width:", DEFAULT_WIDTH);

        Renderer renderer = new ConsoleRenderer();
        Generator generator = chooseOption(GENERATORS, "Choose maze generator.");
        Solver solver = chooseOption(SOLVERS, "Choose maze solver.");

        Maze maze = generator.generate(height, width);
        out.println(renderer.render(maze));

        int startRow = readParameter("Enter maze start coordinate row:", 1);
        int startCol = readParameter("Enter maze start coordinate col:", 1);
        int endRow = readParameter("Enter maze end coordinate row:", height - 2);
        int endCol = readParameter("Enter maze end coordinate col:", width - 2);
        out.println(renderer.render(maze,
            solver.solve(maze, new Coordinate(startRow, startCol), new Coordinate(endRow, endCol))));
    }

    private <T> T chooseOption(Map<String, Class<? extends T>> options, String message) {
        int index = 1;
        out.println(message);
        for (String option : options.keySet()) {
            out.printf("%d: %s.%n", index++, option);
        }
        int choice = readParameter("Enter the index:", -1) - 1;
        if (choice < 0 || choice >= options.size()) {
            choice = RANDOM.nextInt(options.size());
            out.printf("Incorrect input, random algorithm chosen: %s.%n", options.keySet().toArray()[choice]);
        }
        Class<? extends T> selectedClass = options.get((String) options.keySet().toArray()[choice]);
        try {
            return selectedClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create new instance of " + selectedClass, e);
        }
    }

    private int readParameter(String message, int defaultValue) {
        out.println(message);
        if (scanner.hasNextInt()) {
            return scanner.nextInt();
        } else {
            if (scanner.hasNext()) {
                scanner.nextLine();
            }
            out.printf("Incorrect input, number expected, default value chosen: %d.%n", defaultValue);
            return defaultValue;
        }
    }
}
