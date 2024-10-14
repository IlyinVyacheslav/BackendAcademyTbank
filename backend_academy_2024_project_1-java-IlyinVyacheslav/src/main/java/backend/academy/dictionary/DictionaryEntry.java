package backend.academy.dictionary;

public record DictionaryEntry(Category category, String word, String description) {
    public DictionaryEntry(Category category, WordAndDescription wordAndDescription) {
        this(category, wordAndDescription.word(), wordAndDescription.description());
    }

    public WordAndDescription wordAndDescription() {
        return new WordAndDescription(word, description);
    }

    public record WordAndDescription(String word, String description) {
    }
}
