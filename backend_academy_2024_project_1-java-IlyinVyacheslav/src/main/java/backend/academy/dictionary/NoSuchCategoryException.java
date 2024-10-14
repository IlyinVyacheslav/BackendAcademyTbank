package backend.academy.dictionary;

public class NoSuchCategoryException extends Exception {
    public NoSuchCategoryException(Category category) {
        super("No such category: " + category);
    }
}
