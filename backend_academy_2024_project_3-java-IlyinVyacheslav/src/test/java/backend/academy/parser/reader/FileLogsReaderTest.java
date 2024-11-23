package backend.academy.parser.reader;

import backend.academy.parser.LogRecord;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FileLogsReaderTest {
    private static final LogsReader READER = new FileLogsReader();

    private void testParsedLogsNumber(String path, int expectedLogsNumber) {
        Stream<LogRecord> parsedLogs = READER.readLogs(path);

        assertThat(parsedLogs).hasSize(expectedLogsNumber);
    }

    @Test
    void testEmptyFile() {
        testParsedLogsNumber("empty.log", 0);
    }

    @Test
    void testIncorrectPath() {
        testParsedLogsNumber("logs/3.log", 0);
    }

    @Test
    void testFileWithOneLog() {
        testParsedLogsNumber("logs/1_line.log", 1);
    }

    @Test
    void testFileWithOneLogAndEmptyLine() {
        testParsedLogsNumber("logs/3_lines_1_log.log", 1);
    }

    @Test
    void testFileWithIncorrectLogs() {
        testParsedLogsNumber("logs/incorrect_logs.log", 0);
    }

    @Test
    void testFileWith9Logs() {
        testParsedLogsNumber("logs/9_logs.log", 9);
    }

    @Test
    void testOneLevelPathPattern() {
        testParsedLogsNumber("logs/*", 23);
    }

    @Test
    void testDeepPathPattern() {
        testParsedLogsNumber("logs/**", 83);
    }

    @Test
    void testFileExtensionPattern() {
        testParsedLogsNumber("**/*.log", 72);
    }
}
