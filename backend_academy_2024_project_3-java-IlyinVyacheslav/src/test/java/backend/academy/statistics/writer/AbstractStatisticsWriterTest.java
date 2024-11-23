package backend.academy.statistics.writer;

import backend.academy.statistics.Statistics;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStatisticsWriterTest {
    protected final static Statistics EMPTY_STATISTICS =
        new Statistics(0, new HashMap<>(), new HashMap<>(), new HashMap<>(), 0, 0, 0);
    protected final static Statistics FULL_STATISTICS = new Statistics(
        10,
        Map.ofEntries(Map.entry("/a", 12)),
        Map.ofEntries(Map.entry(200, 2), Map.entry(504, 10)),
        Map.ofEntries(Map.entry("114.152.188.135", 5), Map.entry("135.90.137.4", 7)),
        52,
        90,
        80
    );
}
