package backend.academy.visualization;

import backend.academy.game.Game;
import backend.academy.game.GameResult;
import backend.academy.game.State;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

public class VisualizationController {
    @Getter private final int attempts;
    private final List<String> hangmanStages;
    private final PrintStream out;
    private final Game game;

    public VisualizationController(Game game, PrintStream out) {
        this.out = out;
        this.game = game;
        HangmanVisualization visualization = new ClassicHangmanVisualization(game.gameDuration());
        attempts = visualization.attempts();
        hangmanStages = visualization.stages();
    }

    public void visualize() {
        State state = game.getCurrentState();
        out.println(state.message());
        if (state.result() == GameResult.WIN || state.result() == GameResult.LOSE) {
            return;
        }
        out.printf("Категория: %s. %d попыток осталось%s",
            state.category(),
            attempts - state.attemptsMade(),
            System.lineSeparator());
        out.print(hangmanStages.get(state.attemptsMade()));
        out.println(joinChars(state.guessedParts(), " "));
        out.println(joinChars(state.usedLetters(), ","));
    }

    private String joinChars(Collection<Character> characters, String delimiter) {
        return characters.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(delimiter));
    }
}
