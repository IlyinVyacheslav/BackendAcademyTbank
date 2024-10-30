package backend.academy.rendering;

import java.util.InputMismatchException;

public class IncorrectPathException extends InputMismatchException {
    public IncorrectPathException(String message) {
        super(message);
    }
}
