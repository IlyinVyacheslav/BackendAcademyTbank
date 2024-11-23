package backend.academy;

import backend.academy.parser.reader.FileLogsReader;
import backend.academy.parser.reader.LogsReader;
import backend.academy.parser.reader.UrlLogsReader;
import backend.academy.statistics.StatisticsCollector;
import backend.academy.statistics.writer.AdocStatisticsWriter;
import backend.academy.statistics.writer.MarkdownStatisticsWriter;
import backend.academy.statistics.writer.StatisticsWriter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogsAnalyzer {
    private static final Logger LOGGER = LogManager.getLogger(LogsAnalyzer.class);
    private final StatisticsCollector collector;
    @Parameter(
        names = "--path",
        description = "Path to files",
        required = true
    )
    private String filePathPattern;
    @Parameter(
        names = "--from",
        description = "Starting date",
        defaultValueDescription = ""
    )
    private String fromDate;
    @Parameter(
        names = "--to",
        description = "Ending date",
        defaultValueDescription = ""
    )
    private String toDate;
    @Parameter(
        names = "--format",
        description = "Format in which statistics is produced"
    )
    private String format;
    @Parameter(
        names = "--filter-field",
        description = "Log field to filter logs by"
    )
    private String filterField;
    @Parameter(
        names = "--filter-value",
        description = "Value to match for the filter field"
    )
    private String filterValue;

    public LogsAnalyzer() {
        this.collector = new StatisticsCollector();
    }

    @SuppressWarnings("UncommentedMain")
    public static void main(String[] args) {
        LogsAnalyzer analyzer = new LogsAnalyzer();
        JCommander logsAnalyzerCmd = JCommander.newBuilder()
            .addObject(analyzer)
            .build();
        logsAnalyzerCmd.parse(args);
        LOGGER.info(
            analyzer.analyzeLogs(analyzer.filePathPattern, analyzer.fromDate, analyzer.toDate, analyzer.format,
                analyzer.filterField, analyzer.filterValue));
    }

    public String analyzeLogs(
        String path,
        String from,
        String to,
        String format,
        String filterField,
        String filterValue
    ) {
        LogsReader logsReader = createReader(path);
        StatisticsWriter writer = createWriter(format);
        return writer.writeStatistics(
            collector.getStatistics(
                logsReader.readLogs(path, new LogsFilterArgs(from, to, filterField, filterValue))
            )
        );
    }

    private LogsReader createReader(String path) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return new UrlLogsReader();
        } else {
            return new FileLogsReader();
        }
    }

    private StatisticsWriter createWriter(String format) {
        if (format == null) {
            return new MarkdownStatisticsWriter();
        }
        if ("adoc".equalsIgnoreCase(format)) {
            return new AdocStatisticsWriter();
        }
        LOGGER.warn("Unsupported format: {}, default chosen", format);
        return new MarkdownStatisticsWriter();
    }
}
