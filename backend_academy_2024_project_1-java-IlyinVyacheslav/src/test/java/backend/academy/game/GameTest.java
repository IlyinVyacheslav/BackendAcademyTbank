package backend.academy.game;

import backend.academy.dictionary.Category;
import backend.academy.dictionary.Dictionary;
import backend.academy.dictionary.DictionaryEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GameTest {
    private static final DictionaryEntry.WordAndDescription
        WORD_AND_DESCRIPTION = new DictionaryEntry.WordAndDescription("корова", "дает молоко");
    private static final Category CATEGORY = Category.ANIMALS;
    private static final Dictionary DICTIONARY =
        new Dictionary(
            Map.of(CATEGORY, List.of(WORD_AND_DESCRIPTION)));
    private static List<Character> unusedLetters;
    private static Set<Character> usedLetters;
    private static Game GAME;

    @BeforeAll
    static void setUpAlphabets() {
        List<Character> russianAlphabet = new ArrayList<>();
        for (char c = 'а'; c <= 'я'; c++) {
            russianAlphabet.add(c);
        }
        russianAlphabet.add('ё');

        usedLetters = WORD_AND_DESCRIPTION.word().chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toSet());

        unusedLetters = russianAlphabet.stream()
            .filter(c -> !usedLetters.contains(c))
            .toList();
    }

    private static void testWinningByMakingNoMistakes(Game game) {
        for (Character letter : usedLetters) {
            game.guess(letter);
        }
        State state = game.getCurrentState();

        assertThat(state).isNotNull();
        assertThat(state.result()).isEqualTo(GameResult.WIN);
        assertThat(state.attemptsMade()).isEqualTo(0);
    }

    @BeforeEach
    void makeGame() {
        GAME = new Game(DICTIONARY, CATEGORY, GameDuration.SHORT, DifficultyLevel.HARD);
    }

    @Test
    void testGameWithNonExistentCategory() {
        Category[] nonExistingCategories = {Category.TOOLS, null};

        Arrays.stream(nonExistingCategories).forEach(
            category -> {
                Game game = new Game(DICTIONARY, category, GameDuration.SHORT, DifficultyLevel.HARD);

                testWinningByMakingNoMistakes(game);
            });
    }

    @Test
    void testCorrectWordGuess() {
        testWinningByMakingNoMistakes(GAME);
    }

    @Test
    void testLosingByUsingAllAttempts() {
        for (Character letter : unusedLetters) {
            GAME.guess(letter);
        }
        State resultState = GAME.getCurrentState();

        assertThat(resultState).isNotNull();
        assertThat(resultState.result()).isEqualTo(GameResult.LOSE);
        assertThat(resultState.attemptsMade()).isEqualTo(GAME.attempts());
    }

    @Test
    void testWinningByUsingNotAllAttempts() {
        int attemptsCount = 5;
        List<Character> moves = new ArrayList<>(
            unusedLetters.stream().limit(attemptsCount).toList()
        );
        moves.addAll(usedLetters);
        Game game = new Game(DICTIONARY, CATEGORY, GameDuration.LONG, DifficultyLevel.HARD);

        for (Character letter : moves) {
            game.guess(letter);
        }
        State resultState = game.getCurrentState();

        assertThat(resultState).isNotNull();
        assertThat(resultState.result()).isEqualTo(GameResult.WIN);
        assertThat(resultState.attemptsMade()).isEqualTo(attemptsCount);
    }

    @Test
    void testUpperCase() {
        List<Character> upperCaseLetters = usedLetters.stream()
            .map(Character::toUpperCase)
            .toList();

        for (Character letter : upperCaseLetters) {
            GAME.guess(letter);
        }
        State resultState = GAME.getCurrentState();

        assertThat(resultState).isNotNull();
        assertThat(resultState.result()).isEqualTo(GameResult.WIN);
    }

    @Nested
    class IsInvalidGuessAndIsFinishedTests {
        private static final List<Character> MOVES = List.of('к', 'е', 'j', 'к', 'р', 'о', 'е', 'а', 'з', 'в');
        private static final int MOVES_ATTEMPTS = 2;
        private static final GameResult MOVES_RESULT = GameResult.WIN;

        @Test
        void testRepeatedOrInvalidLetterInput() {
            List<Boolean> expectedInvalidGuessList = List.of(
                Boolean.FALSE,
                Boolean.FALSE,
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.FALSE,
                Boolean.FALSE,
                Boolean.TRUE,
                Boolean.FALSE,
                Boolean.FALSE,
                Boolean.FALSE);

            testGameMethodWithMoves(Game::isInvalidGuess, expectedInvalidGuessList);
        }

        @Test
        void testGameIsFinished() {
            List<Boolean> expectedIsFinishedList = new ArrayList<>(
                Collections.nCopies(MOVES.size() - 1, Boolean.FALSE));
            expectedIsFinishedList.add(Boolean.TRUE);

            testGameMethodWithMoves(Game::isFinished, expectedIsFinishedList);
        }

        private <T> void testGameMethodWithMoves(
            Function<Game, T> gameTFunction,
            List<T> expectedResults
        ) {
            List<T> results = new ArrayList<>();

            for (Character move : MOVES) {
                GAME.guess(move);
                results.add(gameTFunction.apply(GAME));
            }
            State resultState = GAME.getCurrentState();

            assertThat(results).isEqualTo(expectedResults);
            assertThat(resultState).isNotNull();
            assertThat(resultState.result()).isEqualTo(MOVES_RESULT);
            assertThat(resultState.attemptsMade()).isEqualTo(MOVES_ATTEMPTS);
        }
    }
}
