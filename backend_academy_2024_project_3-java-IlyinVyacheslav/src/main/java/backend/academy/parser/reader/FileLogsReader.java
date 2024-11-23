package backend.academy.parser.reader;

import backend.academy.LogsFilterArgs;
import backend.academy.parser.LogRecord;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class FileLogsReader extends AbstractLogsReader {
    public FileLogsReader() {
        super(FileLogsReader.class);
    }

    public Stream<LogRecord> readLogs(String pathPattern, LogsFilterArgs filterArgs) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pathPattern);

        try (Stream<Path> paths = Files.walk(Paths.get(""))) {
            List<Path> pathList = paths.toList();
            return pathList.stream()
                .filter(matcher::matches)
                .filter(Files::isRegularFile)
                .flatMap(path -> {
                    try {
                        return Files.lines(path)
                            .map(super::parseLog)
                            .filter(super.getLogRecordPredicate(filterArgs));
                    } catch (IOException e) {
                        super.logger.warn("Error while reading logs from file {}.", path, e);
                        return Stream.empty();
                    }
                });
        } catch (IOException ignored) {
        }
        return Stream.empty();
    }
}
