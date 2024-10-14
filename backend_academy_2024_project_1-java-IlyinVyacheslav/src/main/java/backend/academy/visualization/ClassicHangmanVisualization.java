package backend.academy.visualization;

import backend.academy.game.GameDuration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClassicHangmanVisualization extends HangmanVisualization {
    private final static Map<GameDuration, HangmanConfig> HANGMAN_CONFIG_MAP = Map.of(
        GameDuration.SHORT, new HangmanConfig(10, List.of(3, 7)),
        GameDuration.LONG, new HangmanConfig(12, List.of()));
    private final static List<String> BASE_HANGMAN_LIST = List.of("""

        """, """





         _____
        """, """
        |
        |
        |
        |
        |_____
        """, """
        _________
        |
        |
        |
        |
        |_____
        """, """
        _________
        | /
        |/
        |
        |
        |_____
        """, """
        _________
        | /  |
        |/
        |
        |
        |_____
        """, """
        _________
        | / |
        |/  o
        |
        |
        |_____
        """, """
        _________
        | / |
        |/  o
        |   O
        |
        |_____
        """, """
        _________
        | / |
        |/  o
        |   0
        |
        |_____
        """, """
        _________
        | / |
        |/  o
        |  /0
        |
        |_____
        """, """
        _________
        | / |
        |/  o
        |  /0\\
        |
        |_____
        """, """
        _________
        | / |
        |/  o
        |  /0\\
        |  /
        |_____
        """, """
        _________
        | / |
        |/  o
        |  /0\\
        |  / \\
        |_____
        """);

    public ClassicHangmanVisualization(GameDuration gameDuration) {
        super(gameDuration);
    }

    @Override
    public List<String> stages() {
        HangmanConfig config = HANGMAN_CONFIG_MAP.get(super.gameDuration);
        return switch (super.gameDuration) {
            case SHORT -> IntStream.range(0, BASE_HANGMAN_LIST.size())
                .filter(i -> !config.excludedStages.contains(i))
                .mapToObj(BASE_HANGMAN_LIST::get)
                .collect(Collectors.toList());
            case LONG -> BASE_HANGMAN_LIST;
        };
    }

    @Override
    int attempts() {
        return HANGMAN_CONFIG_MAP.get(super.gameDuration).length;
    }

    private record HangmanConfig(int length, List<Integer> excludedStages) {
    }
}
