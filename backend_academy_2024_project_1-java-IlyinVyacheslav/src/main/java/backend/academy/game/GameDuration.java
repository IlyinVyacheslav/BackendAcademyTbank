package backend.academy.game;

import lombok.Getter;

@Getter public enum GameDuration {
    SHORT(10),
    LONG(12);

    private final int attempts;

    GameDuration(int attempts) {
        this.attempts = attempts;
    }
}
