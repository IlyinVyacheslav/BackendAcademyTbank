package backend.academy.maze;

import lombok.Getter;

public record Cell(int row, int col, Type type) {
    @Getter public enum Type {
        WALL('#'),
        PASSAGE(' '),
        GOLD('*'),
        BUSH('~');

        private final char symbol;

        Type(char symbol) {
            this.symbol = symbol;
        }

        public static Type fromSymbol(char symbol) {
            for (Type type : Type.values()) {
                if (type.symbol == symbol) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No enum constant with symbol: " + symbol);
        }
    }
}
