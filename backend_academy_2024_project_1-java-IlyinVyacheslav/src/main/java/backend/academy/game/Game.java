package backend.academy.game;

import backend.academy.dictionary.Category;
import backend.academy.dictionary.Dictionary;
import backend.academy.dictionary.DictionaryEntry;
import backend.academy.dictionary.NoSuchCategoryException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

public class Game {
    private static final double REVEALED_LETTERS_PERCENT = 0.25;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Getter private final int attempts;
    @Getter private final GameDuration gameDuration;
    private final DifficultyLevel difficultyLevel;
    private final String category;
    private final String word;
    private final String hint;

    private GameResult gameResult;
    @Getter private String message;
    private int attemptsMade;
    private int guessedLettersNumber;
    private List<Character> guessedParts;
    private Set<Character> usedLetters;
    private Set<Character> uniqueWordLetters;

    public Game(
        Dictionary dictionary,
        Category category,
        GameDuration duration,
        DifficultyLevel difficultyLevel
    ) {
        DictionaryEntry entry;

        try {
            entry = dictionary.getRandomWordFromCategory(category);
        } catch (NoSuchCategoryException e) {
            entry = dictionary.getRandomCategoryAndWord();
        }

        this.category = entry.category().russianName();
        word = entry.word();
        hint = entry.description();
        gameDuration = duration;
        attempts = duration.attempts();
        this.difficultyLevel = difficultyLevel;
        gameResult = GameResult.IN_PROCESS;
        message = "";

        prepareGame();
    }

    public State getCurrentState() {
        return new State(gameResult, attemptsMade, usedLetters, guessedParts, category, message);
    }

    public boolean isFinished() {
        return gameResult == GameResult.WIN || gameResult == GameResult.LOSE;
    }

    public boolean isInvalidGuess() {
        return gameResult == GameResult.INCORRECT_INPUT;
    }

    public State guess(Character ch) {
        message = "";
        if (isFinished()) {
            return getCurrentState();
        }
        Character letter = Character.toLowerCase(ch);

        if (usedLetters.contains(letter)) {
            gameResult = GameResult.INCORRECT_INPUT;
            message = "Буква уже использована, попробуйте снова:";
        } else if (!isRussianLetter(letter)) {
            gameResult = GameResult.INCORRECT_INPUT;
            message = "Ожидалась русская буква, попробуйте снова:";
        } else {
            gameResult = GameResult.IN_PROCESS;
            usedLetters.add(letter);

            if (uniqueWordLetters.contains(letter)) {
                updateGuessedParts(letter);
            } else {
                attemptsMade++;
            }

            if (attemptsMade >= attempts) {
                gameResult = GameResult.LOSE;
                message = "Вы проиграли(";
            } else if (guessedLettersNumber == uniqueWordLetters.size()) {
                gameResult = GameResult.WIN;
                message = "Гооооооооол!";
            } else if (attempts - attemptsMade <= 2) {
                message = String.format("Подсказка: %s.", hint);
            }
        }

        return getCurrentState();
    }

    private void prepareGame() {
        uniqueWordLetters = new HashSet<>(word.length());
        for (int i = 0; i < word.length(); i++) {
            uniqueWordLetters.add(word.charAt(i));
        }

        usedLetters = new LinkedHashSet<>();
        guessedLettersNumber = 0;
        attemptsMade = 0;

        buildGuessedPartsList();
    }

    private void buildGuessedPartsList() {
        guessedParts = Stream
            .generate(() -> '_')
            .limit(word.length())
            .collect(Collectors.toList());

        if (difficultyLevel == DifficultyLevel.NORMAL) {
            int lettersToRevealCount = (int) Math.ceil(uniqueWordLetters.size() * REVEALED_LETTERS_PERCENT);
            List<Character> uniqueLetterList = new ArrayList<>(uniqueWordLetters);
            for (int i = 0; i < lettersToRevealCount; i++) {
                updateGuessedParts(uniqueLetterList.get(RANDOM.nextInt(uniqueLetterList.size())));
            }
        }
    }

    private void updateGuessedParts(Character letter) {
        guessedLettersNumber++;
        usedLetters.add(letter);
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                guessedParts.set(i, letter);
            }
        }
    }

    private boolean isRussianLetter(Character letter) {
        return letter >= 'а' && letter <= 'я' || letter == 'ё';
    }
}
