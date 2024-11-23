package backend.academy.parser;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("MagicNumber")
public class LogParser {
    private static final String DEFAULT_FIELD_VALUE = "-";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
        .ofPattern("dd/MMM/yyyy:HH:mm:ss Z", java.util.Locale.US);
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\S+) - (\\S+) \\[(.+?)] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) (\\d+) \"(.+?)\" \"(.+?)\"$"
    );

    private String parseDefaultValue(String value) {
        if (DEFAULT_FIELD_VALUE.equals(value)) {
            return "";
        }
        return value;
    }

    public LogRecord parseLog(String log) {
        if (log == null || log.isEmpty()) {
            return null;
        }
        Matcher matcher = LOG_PATTERN.matcher(log);

        if (matcher.matches()) {
            String remoteAddress = matcher.group(1);
            String remoteUser = parseDefaultValue(matcher.group(2));
            OffsetDateTime localTime = OffsetDateTime.parse(matcher.group(3), DATE_TIME_FORMATTER);
            String requestType = matcher.group(4);
            String resource = matcher.group(5);
            String protocol = matcher.group(6);
            int status = Integer.parseInt(matcher.group(7));
            int bodyBytesSent = Integer.parseInt(matcher.group(8));
            String httpReferer = parseDefaultValue(matcher.group(9));
            String httpUserAgent = matcher.group(10);

            return new LogRecord(
                remoteAddress, remoteUser, localTime,
                new LogRecord.Request(requestType, resource, protocol), status,
                bodyBytesSent, httpReferer, httpUserAgent
            );

        } else {
            throw new IncorrectLogException("Invalid log: " + log);
        }
    }
}
