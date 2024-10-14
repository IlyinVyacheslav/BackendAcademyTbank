package backend.academy;

import backend.academy.dictionary.Dictionary;
import backend.academy.game.GameController;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) {
        GameController controller = new GameController(new Dictionary(), System.in, System.out);
        controller.startSession();
    }
}
