package backend.academy.parser.reader;

import backend.academy.LogsFilterArgs;
import backend.academy.parser.LogRecord;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FileLogsReaderFilteringTest {
    private static final LogsReader READER = new FileLogsReader();
    private static final String FILE_NAME = "logs/different_date_logs.log";

    private void testNumberOfFilteredLogs(LogsFilterArgs args, int logsNumber) {
        Stream<LogRecord> parsedLogs = READER.readLogs(FILE_NAME, args);

        assertThat(parsedLogs).hasSize(logsNumber);
    }

    @Test
    void testFilteringLogsWithNoArgs() {
        LogsFilterArgs args = new LogsFilterArgs("", "", "", "");

        testNumberOfFilteredLogs(args, 12);
    }

    @Test
    void testFilteringLogsByDateFrom() {
        LogsFilterArgs args = new LogsFilterArgs("2015-05-25", "");

        testNumberOfFilteredLogs(args, 7);
    }

    @Test
    void testFilteredLogsByDateTo() {
        LogsFilterArgs args = new LogsFilterArgs("2015-05-26", "");

        testNumberOfFilteredLogs(args, 3);
    }

    @Test
    void testFilteredLogsByDateFromAndTo() {
        LogsFilterArgs args = new LogsFilterArgs("2015-05-23", "2015-05-27");

        testNumberOfFilteredLogs(args, 4);
    }

    @Test
    void testFilteredLogsByAgentFieldWithWildcard() {
        LogsFilterArgs args = new LogsFilterArgs("", "", "httpUserAgent", "Debian*");

        testNumberOfFilteredLogs(args, 11);
    }

    @Test
    void testFilteredLogsByAgentFieldWithoutSeveralWildcards() {
        LogsFilterArgs args = new LogsFilterArgs("", "", "httpUserAgent", "*APT-HTTP/1.3*");

        testNumberOfFilteredLogs(args, 10);
    }

    @Test
    void testFilteredLogsByRequestType() {
        LogsFilterArgs args = new LogsFilterArgs("", "", "requestType", "GET");

        testNumberOfFilteredLogs(args, 12);
    }

    @Test
    void testFilteredLogsByStatusCode() {
        LogsFilterArgs args = new LogsFilterArgs("", "", "status", "404");

        testNumberOfFilteredLogs(args, 6);
    }
}
