package backend.academy.parser.reader;

import backend.academy.LogsFilterArgs;
import backend.academy.parser.LogRecord;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

public class UrlLogsReader extends AbstractLogsReader {

    public static final int SUCCESSFUL_HTTP_STATUS = 200;

    public UrlLogsReader() {
        super(UrlLogsReader.class);
    }

    @Override
    public Stream<LogRecord> readLogs(String url, LogsFilterArgs filterArgs) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(url);
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == SUCCESSFUL_HTTP_STATUS) {
                return response.body()
                    .lines()
                    .map(super::parseLog)
                    .filter(super.getLogRecordPredicate(filterArgs));
            } else {
                super.logger.error("Error reading logs from {}: Status code {}", url, response.statusCode());
            }
        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            super.logger.error("Error reading logs from {}.", url, e);
        }
        return Stream.empty();
    }
}
