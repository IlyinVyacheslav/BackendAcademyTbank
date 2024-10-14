package backend.academy.input;

import backend.academy.game.GameTerminationException;
import java.util.List;

public class ArrayLetterInput implements LetterInput {
    private final List<Character> letters;
    private int index;

    public ArrayLetterInput(List<Character> letters) {
        this.letters = letters;
    }

    @Override
    public Character readLetter(String message) {
        if (index >= letters.size()) {
            throw new GameTerminationException("Not enough letters provided");
        }
        return letters.get(index++);
    }
}
