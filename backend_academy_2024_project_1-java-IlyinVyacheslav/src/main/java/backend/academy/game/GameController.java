package backend.academy.game;

import backend.academy.dictionary.Category;
import backend.academy.dictionary.Dictionary;
import backend.academy.input.ConsoleLetterInput;
import backend.academy.input.LetterInput;
import backend.academy.visualization.VisualizationController;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

public class GameController {
    public static final String TERMINATION_MESSAGE = "Сессия завершена из консоли.";
    private final Dictionary dictionary;
    private final PrintStream out;
    private final InputStream in;
    private final Scanner scanner;

    public GameController(Dictionary dictionary, InputStream in, PrintStream out) {
        this.dictionary = dictionary;
        this.out = out;
        this.in = in;
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
    }

    public void startSession() {
        boolean sessionAlive = true;
        try {
            while (sessionAlive) {
                out.println("Новая игра началась!");
                Category category = chooseCategory();
                GameDuration gameDuration = chooseFromEnum(GameDuration.class, "Выберите продолжительность игры:");
                DifficultyLevel difficultyLevel = chooseFromEnum(DifficultyLevel.class, "Выберите сложность игры:");

                Game game =
                    new Game(dictionary, category, gameDuration, difficultyLevel);
                LetterInput input = new ConsoleLetterInput(in, out);
                VisualizationController visualizationController = new VisualizationController(game, out);

                visualizationController.visualize();

                while (!game.isFinished()) {
                    Character ch = input.readLetter("Введите букву:");
                    game.guess(ch);

                    while (game.isInvalidGuess()) {
                        game.guess(input.readLetter(game.message()));
                    }

                    visualizationController.visualize();
                }
                if (askForFinish()) {
                    out.println("Сессия завершена!");
                    sessionAlive = false;
                }
            }
        } catch (GameTerminationException e) {
            out.println(e.getMessage());
        }
    }

    private boolean askForFinish() {
        out.println("Нажмите 'й' если хотите закончить.");
        try {
            String answer = scanner.nextLine().trim();
            if ("й".equals(answer)) {
                return true;
            }
        } catch (NoSuchElementException e) {
            throw new GameTerminationException(TERMINATION_MESSAGE, e);
        }
        return false;
    }

    private <T extends Enum<T>> T chooseFromEnum(Class<T> enumClass, String message) {
        return chooseFromList(List.of(enumClass.getEnumConstants()), message, Enum::name);
    }

    private Category chooseCategory() {
        List<Category> categories = new ArrayList<>(dictionary.getCategories());
        categories.addFirst(null);

        return chooseFromList(categories, "Выберите категорию из списка (или нажмите 1 для рандомной): ",
            category -> category == null ? "рандомная категория" : category.russianName());
    }

    private <T> T chooseFromList(List<T> options, String message, Function<T, String> display) {
        out.println(message);

        for (int i = 0; i < options.size(); i++) {
            out.printf("%d. %s%s", i + 1, display.apply(options.get(i)), System.lineSeparator());
        }

        while (true) {
            String input;
            try {
                input = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                throw new GameTerminationException(TERMINATION_MESSAGE, e);
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= options.size()) {
                    return options.get(choice - 1);
                }
            } catch (NumberFormatException ignored) {
            }

            out.print("Некорректный ввод! Выберите число из промежутка:");
        }
    }
}
