package backend.academy.dictionary;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dictionary {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final Map<Category, List<DictionaryEntry.WordAndDescription>> dictionary;

    public Dictionary() {
        dictionary = Map.of(
            Category.ANIMALS, List.of(
                new DictionaryEntry.WordAndDescription("носорог",
                    "Крупное животное с массивным телом и одним или двумя рогами на носу."),
                new DictionaryEntry.WordAndDescription("тигр", "Крупный хищник с полосатой шкурой, обитающий в Азии."),
                new DictionaryEntry.WordAndDescription("лев", "Царь зверей, обитающий в Африке и имеющий гриву."),
                new DictionaryEntry.WordAndDescription("слон", "Самое большое наземное животное с длинным хоботом."),
                new DictionaryEntry.WordAndDescription("жираф",
                    "Самое высокое животное с длинной шеей и пятнистой шкурой."),
                new DictionaryEntry.WordAndDescription("зебра", "Животное с черно-белыми полосами, обитающее в Африке.")
            ),
            Category.TOOLS, List.of(
                new DictionaryEntry.WordAndDescription("пила", "Инструмент для распиливания древесины или металла."),
                new DictionaryEntry.WordAndDescription("молоток",
                    "Инструмент для забивания гвоздей или разбивания предметов."),
                new DictionaryEntry.WordAndDescription("дрель", "Инструмент для сверления отверстий."),
                new DictionaryEntry.WordAndDescription("отвёртка",
                    "Инструмент для завинчивания или отвинчивания винтов."),
                new DictionaryEntry.WordAndDescription("гвозди",
                    "Металлические крепежи, используемые для соединения материалов."),
                new DictionaryEntry.WordAndDescription("цемент",
                    "Строительный материал, используемый для создания бетона.")
            ),
            Category.FRUITS, List.of(
                new DictionaryEntry.WordAndDescription("яблоко", "Популярный фрукт красного или зеленого цвета."),
                new DictionaryEntry.WordAndDescription("банан", "Длинный желтый фрукт с мягкой мякотью."),
                new DictionaryEntry.WordAndDescription("апельсин",
                    "Цитрусовый фрукт с оранжевой кожурой и сочной мякотью."),
                new DictionaryEntry.WordAndDescription("груша",
                    "Фрукт с сочной сладкой мякотью, форма напоминает лампочку."),
                new DictionaryEntry.WordAndDescription("манго",
                    "Тропический фрукт с сочной желтой мякотью и крупной косточкой."),
                new DictionaryEntry.WordAndDescription("ананас",
                    "Тропический фрукт с шипастой кожурой и сладкой мякотью.")
            ),
            Category.TRANSPORT, List.of(
                new DictionaryEntry.WordAndDescription("машина",
                    "Четырёхколёсное транспортное средство для перевозки людей и грузов."),
                new DictionaryEntry.WordAndDescription("велосипед",
                    "Транспортное средство на двух колесах, приводимое в движение педалями."),
                new DictionaryEntry.WordAndDescription("самолёт",
                    "Летательный аппарат с крыльями для воздушных перелетов."),
                new DictionaryEntry.WordAndDescription("поезд", "Состав из вагонов, движущийся по рельсам."),
                new DictionaryEntry.WordAndDescription("мотоцикл",
                    "Моторизированное двухколёсное транспортное средство."),
                new DictionaryEntry.WordAndDescription("корабль", "Крупное плавсредство для передвижения по воде.")
            ),
            Category.PROFESSIONS, List.of(
                new DictionaryEntry.WordAndDescription("врач", "Специалист по лечению и диагностике болезней."),
                new DictionaryEntry.WordAndDescription("инженер",
                    "Специалист, занимающийся проектированием и конструированием."),
                new DictionaryEntry.WordAndDescription("учитель",
                    "Человек, обучающий детей или взрослых различным дисциплинам."),
                new DictionaryEntry.WordAndDescription("пожарный",
                    "Человек, занимающийся тушением пожаров и спасением людей."),
                new DictionaryEntry.WordAndDescription("полицейский",
                    "Человек, поддерживающий правопорядок и защищающий граждан."),
                new DictionaryEntry.WordAndDescription("программист",
                    "Специалист по разработке и написанию программного обеспечения.")
            ),
            Category.NATURAL_PHENOMENA, List.of(
                new DictionaryEntry.WordAndDescription("дождь", "Выпадение капель воды из облаков."),
                new DictionaryEntry.WordAndDescription("снег", "Замерзшие капли воды, выпадающие в виде кристаллов."),
                new DictionaryEntry.WordAndDescription("ветер", "Движение воздушных масс в атмосфере."),
                new DictionaryEntry.WordAndDescription("землетрясение",
                    "Подземные толчки, вызывающие колебания земной поверхности."),
                new DictionaryEntry.WordAndDescription("цунами",
                    "Огромная волна, вызванная подводным землетрясением или вулканом."),
                new DictionaryEntry.WordAndDescription("град", "Осадки в виде льдинок, выпадающие из облаков.")
            )
        );
    }

    public Dictionary(Map<Category, List<DictionaryEntry.WordAndDescription>> dict) {
        dictionary = dict;
    }

    public Set<Category> getCategories() {
        return dictionary.keySet();
    }

    private Category getRandomCategory() {
        return new ArrayList<>(dictionary.keySet()).get(RANDOM.nextInt(dictionary.size()));
    }

    public boolean containsCategory(Category category) {
        return dictionary.containsKey(category);
    }

    public DictionaryEntry getRandomWordFromCategory(Category category) throws NoSuchCategoryException {
        if (category == null || !containsCategory(category)) {
            throw new NoSuchCategoryException(category);
        }
        List<DictionaryEntry.WordAndDescription> words = dictionary.get(category);
        return new DictionaryEntry(category, words.get(RANDOM.nextInt(words.size())));
    }

    public DictionaryEntry getRandomCategoryAndWord() {
        Category randomCategory = getRandomCategory();
        List<DictionaryEntry.WordAndDescription> words = dictionary.get(randomCategory);
        return new DictionaryEntry(randomCategory, words.get(RANDOM.nextInt(words.size())));
    }
}
