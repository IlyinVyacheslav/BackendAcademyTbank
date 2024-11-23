package backend.academy.parser.reader;

import backend.academy.LogsFilterArgs;
import backend.academy.parser.LogRecord;
import java.util.stream.Stream;

public interface LogsReader {
    Stream<LogRecord> readLogs(String path, LogsFilterArgs filterArgs);

    Stream<LogRecord> readLogs(String path);
}
