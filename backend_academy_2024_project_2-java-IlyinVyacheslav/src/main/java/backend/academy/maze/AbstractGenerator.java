package backend.academy.maze;

import java.security.SecureRandom;

public abstract class AbstractGenerator implements Generator {
    public final static double GOLD_CELLS_PERCENTAGE = 0.1;
    public final static double BUSH_CELLS_PERCENTAGE = 0.1;
    protected final static SecureRandom RANDOM = new SecureRandom();
    private final static int MIN_SIDE_LENGTH = 2;

    protected static Cell.Type getPassageCellType() {
        double randomValue = RANDOM.nextDouble();
        if (randomValue < GOLD_CELLS_PERCENTAGE) {
            return Cell.Type.GOLD;
        } else if (randomValue - GOLD_CELLS_PERCENTAGE < BUSH_CELLS_PERCENTAGE) {
            return Cell.Type.BUSH;
        }
        return Cell.Type.PASSAGE;
    }

    protected int validateMazeSide(int side) {
        return Math.max(side, MIN_SIDE_LENGTH);
    }
}
