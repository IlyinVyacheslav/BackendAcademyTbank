package backend.academy.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

/**
 * An implementation of the {@link Generator} interface that uses Prim's algorithm.
 * This class generates a maze by starting with a single cell, then expanding
 * the maze by randomly connecting it to neighbors cells.
 */
public class PrimsGenerator extends AbstractGenerator {
    private final static Logger LOGGER = Logger.getLogger(PrimsGenerator.class.getName());
    private PrimsMaze maze;
    private List<PrimsCell> neighbors;
    private int height;
    private int width;

    @Override
    public Maze generate(int height, int width) {
        this.height = validateMazeSide(height);
        this.width = validateMazeSide(width);
        maze = new PrimsMaze(this.height, this.width);

        neighbors = new ArrayList<>();

        markCellInner(0, 0);

        while (!neighbors.isEmpty()) {
            PrimsCell neighbor = neighbors.remove(RANDOM.nextInt(neighbors.size()));
            PrimsCell innerCell = findRandomInnerNeighbor(neighbor);
            if (innerCell == null) {
                LOGGER.log(Level.WARNING, "Error: could not find inner neighbor");
                break;
            }
            maze.connectCells(neighbor, innerCell);

            markCellInner(neighbor.row(), neighbor.col());
        }

        return new Maze(height * 2 + 1, width * 2 + 1, maze.cells);
    }

    private PrimsCell findRandomInnerNeighbor(PrimsCell neighbor) {
        List<PrimsCell> innerNeighbors = new ArrayList<>();
        int row = neighbor.row();
        int col = neighbor.col();
        if (isCellOfGivenType(row - 1, col, PrimsCell.CellType.INNER)) {
            innerNeighbors.add(maze.getCell(row - 1, col));
        }
        if (isCellOfGivenType(row + 1, col, PrimsCell.CellType.INNER)) {
            innerNeighbors.add(maze.getCell(row + 1, col));
        }
        if (isCellOfGivenType(row, col - 1, PrimsCell.CellType.INNER)) {
            innerNeighbors.add(maze.getCell(row, col - 1));
        }
        if (isCellOfGivenType(row, col + 1, PrimsCell.CellType.INNER)) {
            innerNeighbors.add(maze.getCell(row, col + 1));
        }
        return innerNeighbors.isEmpty() ? null : innerNeighbors.get(RANDOM.nextInt(innerNeighbors.size()));
    }

    private void markCellInner(int row, int col) {
        maze.setCell(new PrimsCell(row, col, PrimsCell.CellType.INNER));
        markCellNeighbor(row + 1, col);
        markCellNeighbor(row - 1, col);
        markCellNeighbor(row, col + 1);
        markCellNeighbor(row, col - 1);
    }

    private void markCellNeighbor(int row, int col) {
        if (isCellOfGivenType(row, col, PrimsCell.CellType.OUTER)) {
            maze.setCell(new PrimsCell(row, col, PrimsCell.CellType.NEIGHBOR));
            neighbors.add(maze.getCell(row, col));
        }
    }

    private boolean isCellOfGivenType(int row, int col, PrimsCell.CellType type) {
        return row < height && row >= 0 && col < width && col >= 0 && maze.getCell(row, col).type == type;
    }

    record PrimsCell(int row, int col, CellType type) {
        enum CellType { NEIGHBOR, INNER, OUTER }
    }

    private static class PrimsMaze {
        private final int height;
        private final int width;
        private PrimsCell[][] algorithmCells;
        @Getter private Cell[][] cells;

        PrimsMaze(int height, int width) {
            this.height = height;
            this.width = width;
            cells = new Cell[height * 2 + 1][width * 2 + 1];
            initializeCellTypes();
            initializeCells();
        }

        PrimsCell getCell(int row, int col) {
            return algorithmCells[row][col];
        }

        void setCell(PrimsCell cell) {
            algorithmCells[cell.row][cell.col] = cell;
        }

        void connectCells(PrimsCell a, PrimsCell b) {
            if (a.col() == b.col() && Math.abs(a.row() - b.row()) == 1
                || a.row() == b.row() && Math.abs(a.col() - b.col()) == 1) {
                int r = toMazeCoordinate(b.row()) + (toMazeCoordinate(a.row()) - toMazeCoordinate(b.row())) / 2;
                int c = toMazeCoordinate(b.col()) + (toMazeCoordinate(a.col()) - toMazeCoordinate(b.col())) / 2;
                cells[r][c] = new Cell(r, c, getPassageCellType());
            }
        }

        private int toMazeCoordinate(int logicalCoordinate) {
            return logicalCoordinate * 2 + 1;
        }

        private void initializeCellTypes() {
            algorithmCells = new PrimsCell[height][width];
            for (int y = 0; y < algorithmCells.length; y++) {
                for (int x = 0; x < algorithmCells[y].length; x++) {
                    algorithmCells[y][x] = new PrimsCell(y, x, PrimsCell.CellType.OUTER);
                }
            }
        }

        private void initializeCells() {
            cells = new Cell[height * 2 + 1][width * 2 + 1];
            for (int y = 0; y < cells.length; y++) {
                for (int x = 0; x < cells[y].length; x++) {
                    Cell.Type type = y % 2 == 0 || x % 2 == 0 ? Cell.Type.WALL : getPassageCellType();
                    cells[y][x] = new Cell(y, x, type);
                }
            }
        }
    }
}
