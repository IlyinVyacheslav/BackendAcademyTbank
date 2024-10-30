package backend.academy;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) {
        MazeService service = new MazeService(System.in, System.out);
        service.showMazeAlgorithms();
    }
}
