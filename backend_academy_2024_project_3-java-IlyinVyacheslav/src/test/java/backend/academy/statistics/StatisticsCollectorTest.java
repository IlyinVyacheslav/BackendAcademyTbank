package backend.academy.statistics;

import backend.academy.parser.LogRecord;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StatisticsCollectorTest {
    public static final StatisticsCollector STATISTICS_COLLECTOR = new StatisticsCollector();

    private LogRecord createLogRecord(int status, int serverResponseSize, String address, String recourse) {
        return new LogRecord(address, "", null, new LogRecord.Request("", recourse, ""), status, serverResponseSize, "",
            "");
    }

    @Test
    void testEmptyStream() {
        Stream<LogRecord> emptyStream = Stream.empty();

        Statistics stats = STATISTICS_COLLECTOR.getStatistics(emptyStream);

        assertThat(stats).isNotNull();
        assertThat(stats.requestsNumber()).isEqualTo(0);
        assertThat(stats.averageServerResponseSize()).isEqualTo(0);
        assertThat(stats.p95ServerResponseSize()).isEqualTo(0);
        assertThat(stats.errorRate()).isEqualTo(0);
    }

    @Test
    void testSingleLogRecordStream() {
        Stream<LogRecord> singleLogRecordStream = Stream.of(createLogRecord(200, 10, "135.90.137.4", "/"));

        Statistics stats = STATISTICS_COLLECTOR.getStatistics(singleLogRecordStream);

        assertThat(stats.requestsNumber()).isEqualTo(1);
        assertThat(stats.averageServerResponseSize()).isEqualTo(10);
        assertThat(stats.p95ServerResponseSize()).isEqualTo(10);
        assertThat(stats.errorRate()).isEqualTo(0);
        assertThat(stats.responseCodesFrequencyMap()).containsExactly(Map.entry(200, 1));
        assertThat(stats.resourceFrequencyMap()).containsExactly(Map.entry("/", 1));
        assertThat(stats.remoteAddressFrequencyMap()).containsExactly(Map.entry("135.90.137.4", 1));
    }

    @Test
    void testTwoLogsRecordStream() {
        Stream<LogRecord> twoLogsRecordStream = Stream.of(
            createLogRecord(200, 10, "135.90.137.4", "/"),
            createLogRecord(200, 30, "135.90.137.4", "/")
        );

        Statistics stats = STATISTICS_COLLECTOR.getStatistics(twoLogsRecordStream);

        assertThat(stats.requestsNumber()).isEqualTo(2);
        assertThat(stats.averageServerResponseSize()).isEqualTo(20);
        assertThat(stats.p95ServerResponseSize()).isEqualTo(29);
        assertThat(stats.errorRate()).isEqualTo(0);
        assertThat(stats.responseCodesFrequencyMap()).containsExactly(Map.entry(200, 2));
        assertThat(stats.resourceFrequencyMap()).containsExactly(Map.entry("/", 2));
        assertThat(stats.remoteAddressFrequencyMap()).containsExactly(Map.entry("135.90.137.4", 2));

    }

    @Test
    void testThreeLogsRecordStream() {
        Stream<LogRecord> threeLogsRecordStream = Stream.of(
            createLogRecord(504, 0, "114.152.188.135", "/a"),
            createLogRecord(200, 100, "114.152.188.135", "/b"),
            createLogRecord(504, 50, "135.90.137.4", "/b")
        );

        Statistics stats = STATISTICS_COLLECTOR.getStatistics(threeLogsRecordStream);

        assertThat(stats.requestsNumber()).isEqualTo(3);
        assertThat(stats.averageServerResponseSize()).isEqualTo(50);
        assertThat(stats.p95ServerResponseSize()).isEqualTo(95);
        assertThat(stats.errorRate()).isEqualTo((double) 2 / 3 * 100);
        assertThat(stats.responseCodesFrequencyMap()).containsExactly(Map.entry(504, 2), Map.entry(200, 1));
        assertThat(stats.resourceFrequencyMap()).containsExactly(Map.entry("/a", 1), Map.entry("/b", 2));
        assertThat(stats.remoteAddressFrequencyMap()).containsExactly(
            Map.entry("114.152.188.135", 2), Map.entry("135.90.137.4", 1));
    }

    @Test
    void testMultipleLogsRecordStream() {
        Stream<LogRecord> multipleLogsRecordStream = Stream.of(
            createLogRecord(301, 1, "114.152.188.135", "/e"),
            createLogRecord(301, 2, "135.90.137.4", "/b"),
            createLogRecord(301, 3, "135.90.137.4", "/c"),
            createLogRecord(301, 4, "114.152.188.135", "/c"),
            createLogRecord(200, 5, "83.161.176.231", "/b"),
            createLogRecord(301, 6, "114.152.188.135", "/e"),
            createLogRecord(200, 7, "135.90.137.4", "/e"),
            createLogRecord(504, 8, "114.152.188.135", "/e"),
            createLogRecord(504, 9, "135.90.137.4", "/b"),
            createLogRecord(200, 10, "114.152.188.135", "/d")
        );

        Statistics stats = STATISTICS_COLLECTOR.getStatistics(multipleLogsRecordStream);

        assertThat(stats.requestsNumber()).isEqualTo(10);
        assertThat(stats.averageServerResponseSize()).isEqualTo(5.5);
        assertThat(stats.p95ServerResponseSize()).isEqualTo(9.55);
        assertThat(stats.errorRate()).isEqualTo(20.0);
        assertThat(stats.responseCodesFrequencyMap()).containsExactly(Map.entry(301, 5), Map.entry(200, 3),
            Map.entry(504, 2));
        assertThat(stats.resourceFrequencyMap()).containsExactly(Map.entry("/e", 4), Map.entry("/b", 3),
            Map.entry("/c", 2), Map.entry("/d", 1));
        assertThat(stats.remoteAddressFrequencyMap()).containsExactly(Map.entry("114.152.188.135", 5),
            Map.entry("135.90.137.4", 4), Map.entry("83.161.176.231", 1));
    }
}
