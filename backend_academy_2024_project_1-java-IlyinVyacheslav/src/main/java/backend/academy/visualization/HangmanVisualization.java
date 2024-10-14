package backend.academy.visualization;

import backend.academy.game.GameDuration;
import java.util.List;

public abstract class HangmanVisualization {
    protected final GameDuration gameDuration;

    public HangmanVisualization(GameDuration gameDuration) {
        this.gameDuration = gameDuration;
    }

    abstract List<String> stages();

    abstract int attempts();
}
