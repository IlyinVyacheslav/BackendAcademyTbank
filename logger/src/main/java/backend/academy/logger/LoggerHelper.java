package backend.academy.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;

@UtilityClass
public class LoggerHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerHelper.class);

    public void info(String message, Map<String, Object> context) {
        logWithContext(LOGGER.atInfo(), message, context);
    }

    public void info(String message) {
        info(message, Map.of());
    }

    public void error(String message, Map<String, Object> context, Throwable error) {
        logWithContext(LOGGER.atError().setCause(error), message, context);
    }

    public void error(String message, Throwable error) {
        error(message, Map.of(), error);
    }

    public void warn(String message, Map<String, Object> context) {
        logWithContext(LOGGER.atWarn(), message, context);
    }

    public void warn(String message) {
        warn(message, Map.of());
    }

    public void debug(String message, Map<String, Object> context) {
        logWithContext(LOGGER.atDebug(), message, context);
    }

    private static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private void logWithContext(LoggingEventBuilder loggingEventBuilder, String message, Map<String, Object> context) {
        loggingEventBuilder = loggingEventBuilder.setMessage(message);
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            loggingEventBuilder = loggingEventBuilder.addKeyValue(key, toJson(value));
        }
        loggingEventBuilder.log();
    }
}
