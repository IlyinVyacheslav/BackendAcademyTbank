package backend.academy.dictionary;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DictionaryTest {
    private static Map<Category, List<DictionaryEntry.WordAndDescription>> testDictionary;

    private static Dictionary dictionary;

    @BeforeAll
    static void setUp() {
        testDictionary = generateTestDictionary();
        dictionary = new Dictionary(testDictionary);
    }

    private static List<DictionaryEntry.WordAndDescription> generateWordList(int size) {
        return Instancio.ofList(DictionaryEntry.WordAndDescription.class)
            .size(size)
            .create();
    }

    private static Map<Category, List<DictionaryEntry.WordAndDescription>> generateTestDictionary() {
        return Map.of(
            Category.ANIMALS, generateWordList(3),
            Category.TOOLS, generateWordList(2)
        );
    }

    @Test
    void getCategories() {
        Set<Category> expectedCategories = testDictionary.keySet();

        Set<Category> actualCategories = dictionary.getCategories();

        assertThat(expectedCategories).isEqualTo(actualCategories);
    }

    @Test
    void getRandomWordFromCategory() throws NoSuchCategoryException {
        Category category = Category.TOOLS;
        List<DictionaryEntry.WordAndDescription> expectedWordsList = testDictionary.get(category);

        DictionaryEntry.WordAndDescription word = dictionary.getRandomWordFromCategory(category).wordAndDescription();

        assertThat(word).isNotNull();
        assertThat(expectedWordsList).contains(word);
    }

    @Test
    void getRandomWordFromNonExistingCategory() {
        Category[] nonExistingCategories = {Category.FRUITS, null};

        Arrays.stream(nonExistingCategories).forEach(
            category -> assertThatThrownBy(() -> dictionary.getRandomWordFromCategory(category))
                .as("If category does not exist or is null NoSuchCategoryException must be thrown")
                .isInstanceOf(NoSuchCategoryException.class)
        );
    }

    @Test
    void getRandomWord() {
        DictionaryEntry word = dictionary.getRandomCategoryAndWord();

        assertThat(word).isNotNull();
        assertThat(testDictionary).containsKey(word.category());
        assertThat(testDictionary.get(word.category())).contains(word.wordAndDescription());
    }
}
