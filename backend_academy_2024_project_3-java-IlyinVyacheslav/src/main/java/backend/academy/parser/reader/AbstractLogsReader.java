package backend.academy.parser.reader;

import backend.academy.LogsFilterArgs;
import backend.academy.parser.IncorrectLogException;
import backend.academy.parser.LogParser;
import backend.academy.parser.LogRecord;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractLogsReader implements LogsReader {
    protected LogParser logParser;
    protected Logger logger;

    protected AbstractLogsReader(Class<?> clazz) {
        logger = LogManager.getLogger(clazz);
        logParser = new LogParser();
    }

    @Override
    public Stream<LogRecord> readLogs(String path) {
        return readLogs(path, new LogsFilterArgs("", "", "", ""));
    }

    protected Predicate<LogRecord> getLogRecordPredicate(LogsFilterArgs filterArgs) {
        LocalDate parsedFromDate = getLocalDate(filterArgs.startDate());
        LocalDate parsedToDate = getLocalDate(filterArgs.endDate());

        return log -> {
            if (log == null) {
                return false;
            }
            LocalDate logDate = log.localTime().toLocalDate();
            return matchesFieldFilter(log, filterArgs.filterField(), filterArgs.filterValue())
                   && (parsedFromDate == null || logDate.isAfter(parsedFromDate))
                   && (parsedToDate == null || logDate.isBefore(parsedToDate));
        };
    }

    protected Predicate<LogRecord> getLogRecordPredicate(Predicate<LogRecord> logRecordPredicate) {
        return log -> log != null && logRecordPredicate.test(log);
    }

    protected LogRecord parseLog(String line) {
        try {
            return logParser.parseLog(line);
        } catch (IncorrectLogException ignored) {
            return null;
        }
    }

    private LocalDate getLocalDate(String date) {
        return date != null ? parseDateFromString(date) : null;
    }

    private boolean matchesFieldFilter(LogRecord log, String field, String filterValue) {
        if (filterValue == null || filterValue.isEmpty()) {
            return true;
        }
        String fieldValue = log.getFieldValue(field, logger);

        return fieldValue != null && Pattern.compile(filterValue.replace("*", ".*")).matcher(fieldValue).matches();
    }

    private LocalDate parseDateFromString(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            logger.error("Unable to parse date: {}.", date, e);
            return null;
        }
    }

}
