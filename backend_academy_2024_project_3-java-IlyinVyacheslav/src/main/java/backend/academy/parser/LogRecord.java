package backend.academy.parser;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.function.Function;
import org.apache.logging.log4j.Logger;

public record LogRecord(String remoteAddress, String remoteUser,
                        OffsetDateTime localTime, Request request, int status,
                        int bodyBytesSent, String httpReferer, String httpUserAgent) {
    private static final Map<String, Function<LogRecord, String>> FIELD_EXTRACTOR = Map.of(
        "remoteaddress", LogRecord::remoteAddress,
        "remoteuser", LogRecord::remoteUser,
        "requesttype", log -> log.request().requestType(),
        "resource", log -> log.request().resource(),
        "protocol", log -> log.request().protocol(),
        "status", log -> String.valueOf(log.status()),
        "bodybytessent", log -> String.valueOf(log.bodyBytesSent()),
        "httpreferer", LogRecord::httpReferer,
        "httpuseragent", LogRecord::httpUserAgent
    );
    public static final String ALLOWED_FIELDS =
        "Allowed fields: " + String.join(",", LogRecord.FIELD_EXTRACTOR.keySet());

    public String getFieldValue(String field, Logger logger) {
        if (field == null || !FIELD_EXTRACTOR.containsKey(field.toLowerCase())) {
            logger.warn("Unsupported filter field: {}. {}", field, ALLOWED_FIELDS);
            return null;
        }

        return FIELD_EXTRACTOR.get(field.toLowerCase()).apply(this);
    }

    public record Request(String requestType, String resource, String protocol) {
    }
}
