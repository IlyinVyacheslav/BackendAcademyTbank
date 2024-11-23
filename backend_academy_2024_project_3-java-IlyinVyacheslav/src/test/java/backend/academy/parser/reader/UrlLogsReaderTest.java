package backend.academy.parser.reader;

import backend.academy.parser.LogRecord;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UrlLogsReaderTest {
    public static final LogsReader URL_LOGS_READER = new UrlLogsReader();

    @Test
    void testEmptyUrl() {
        Stream<LogRecord> parsedLogs = URL_LOGS_READER.readLogs("");

        assertThat(parsedLogs).isEmpty();
    }

    @Test
    void testIncorrectUrl() {
        Stream<LogRecord> parsedLogs = URL_LOGS_READER.readLogs("http://example.com");

        assertThat(parsedLogs).isEmpty();
    }

    @Test
    void testCorrectUrl() {
        String url =
            "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs";
        Stream<LogRecord> parsedLogs = URL_LOGS_READER.readLogs(url);

        assertThat(parsedLogs).hasSize(51462);
    }
}
