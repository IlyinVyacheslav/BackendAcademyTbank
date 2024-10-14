package backend.academy.game;

import java.util.List;
import java.util.Set;

public record State(GameResult result, int attemptsMade, Set<Character> usedLetters,
                    List<Character> guessedParts, String category, String message) {
}
