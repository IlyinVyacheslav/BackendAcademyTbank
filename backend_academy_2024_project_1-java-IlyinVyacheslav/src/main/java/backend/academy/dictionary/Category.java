package backend.academy.dictionary;

import lombok.Getter;

@Getter
public enum Category {
    ANIMALS("животные"),
    TOOLS("строительные предметы"),
    FRUITS("фрукты"),
    TRANSPORT("транспорт"),
    PROFESSIONS("профессии"),
    NATURAL_PHENOMENA("природные явления");

    private final String russianName;

    Category(String string) {
        russianName = string;
    }
}
