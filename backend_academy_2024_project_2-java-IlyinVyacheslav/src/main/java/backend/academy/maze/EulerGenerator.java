package backend.academy.maze;

import java.util.Arrays;

/**
 * An implementation of the {@link Generator} interface that uses Euler's algorithm.
 * This class generates a maze row by row, connecting cells with random vertical
 * and horizontal walls, ensuring a solvable maze with no isolated sections.
 */
public class EulerGenerator extends AbstractGenerator {
    private int height;
    private int width;
    private boolean[][] verticalWalls;
    private boolean[][] horizontalWalls;
    private int[] line;
    private int counter;

    @Override
    public Maze generate(int height, int width) {
        initializeFields(validateMazeSide(height), validateMazeSide(width));

        for (int y = 0; y < height - 1; y++) {
            allocateNewGroups();
            buildVerticalWalls(y);
            buildHorizontalWalls(y);
            prepareNextLine(y);
        }

        buildLastLine();
        return generateMazeFromWalls();
    }

    private Maze generateMazeFromWalls() {
        int mazeHeight = height * 2 + 1;
        int mazeWidth = width * 2 + 1;
        Cell.Type[][] cellTypes = new Cell.Type[mazeHeight][mazeWidth];
        for (Cell.Type[] cellType : cellTypes) {
            Arrays.fill(cellType, Cell.Type.WALL);
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                cellTypes[2 * i + 1][2 * j + 1] = getPassageCellType();
                if (!verticalWalls[i][j]) {
                    cellTypes[2 * i + 1][2 * j + 2] = getPassageCellType();
                }
                if (!horizontalWalls[i][j]) {
                    cellTypes[2 * i + 2][2 * j + 1] = getPassageCellType();
                }
            }
        }

        Cell[][] cells = new Cell[mazeHeight][mazeWidth];
        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                cells[y][x] = new Cell(y, x, cellTypes[y][x]);
            }
        }
        return new Maze(mazeHeight, mazeWidth, cells);
    }

    private void buildLastLine() {
        System.arraycopy(verticalWalls[height - 2], 0, verticalWalls[height - 1], 0, width);
        for (int x = 0; x < width - 1; x++) {
            horizontalWalls[height - 1][x] = true;
            if (line[x] != line[x + 1]) {
                verticalWalls[height - 1][x] = false;
            }
        }
        horizontalWalls[height - 1][width - 1] = true;
        verticalWalls[height - 1][width - 1] = true;
    }

    private void prepareNextLine(int row) {
        for (int x = 0; x < width - 1; x++) {
            if (horizontalWalls[row][x]) {
                line[x] = 0;
            }
        }
    }

    private void buildHorizontalWalls(int row) {
        int groupSize = 0;
        int groupIndex = 0;
        int wallsInGroup = 0;
        for (int x = 0; x < width; x++) {
            groupIndex++;
            if (groupIndex >= groupSize) {
                groupSize = calculateGroupSize(x);
                groupIndex = 0;
            }
            boolean isWall = RANDOM.nextBoolean();
            if (wallsInGroup < groupSize - 1 && isWall) {
                wallsInGroup++;
                horizontalWalls[row][x] = true;
            }
        }

    }

    private int calculateGroupSize(int pos) {
        int size = 1;
        int index = pos;
        while (index < line.length - 1 && line[index] == line[index + 1]) {
            size++;
            index++;
        }
        return size;
    }

    private void buildVerticalWalls(int row) {
        for (int x = 0; x < width - 1; x++) {
            boolean isWall = RANDOM.nextBoolean();
            if (isWall || line[x] == line[x + 1]) {
                verticalWalls[row][x] = true;
            } else {
                line[x + 1] = line[x];
            }
        }
        verticalWalls[row][width - 1] = true;
    }

    private void allocateNewGroups() {
        for (int x = 0; x < width; x++) {
            if (line[x] == 0) {
                line[x] = counter++;
            }
        }
    }

    private void initializeFields(int height, int width) {
        this.height = height;
        this.width = width;
        this.verticalWalls = new boolean[height][width];
        this.horizontalWalls = new boolean[height][width];
        this.line = new int[width];
        for (int x = 0; x < width; x++) {
            line[x] = 0;
        }
        this.counter = 0;
    }
}
