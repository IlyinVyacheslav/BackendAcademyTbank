package backend.academy.input;

import backend.academy.game.GameTerminationException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleLetterInput implements LetterInput {
    private final PrintStream out;
    private final Scanner scanner;

    public ConsoleLetterInput(InputStream in, PrintStream out) {
        this.out = out;
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
    }

    @Override
    public Character readLetter(String message) {
        out.print(message);

        String line;
        try {
            line = scanner.nextLine();
        } catch (NoSuchElementException e) {
            throw new GameTerminationException("Игра завершена из консоли.", e);
        }

        if (line.length() != 1) {
            return readLetter("Ожидалась одна буква, попробуйте снова:");
        }

        return line.charAt(0);
    }
}
