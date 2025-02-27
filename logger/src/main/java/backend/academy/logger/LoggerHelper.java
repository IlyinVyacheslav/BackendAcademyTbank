package backend.academy.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@UtilityClass
public class LoggerHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerHelper.class);
    private static final String FORMAT = "Message: {}, Context: {}";

    public void info(String message, Map<String, Object> context) {
        withMDC(context, () -> LOGGER.info(FORMAT, message, toJson(context)));
    }

    public void info(String message) {
        info(message, Map.of());
    }

    public void error(String message, Map<String, Object> context, Throwable error) {
        withMDC(context, () -> LOGGER.error(FORMAT, message, toJson(context), error));
    }

    public void error(String message, Throwable error) {
        error(message, Map.of(), error);
    }

    public void warn(String message, Map<String, Object> context) {
        withMDC(context, () -> LOGGER.warn(FORMAT, message, toJson(context)));
    }

    public void debug(String message, Map<String, Object> context) {
        withMDC(context, () -> LOGGER.debug(FORMAT, message, toJson(context)));
    }

    private void withMDC(Map<String, Object> context, Runnable action) {
        try {
            context.forEach((key, value) -> {
                MDC.put(key, toJson(value));
            });
            action.run();
        } finally {
            MDC.clear();
        }
    }

    private static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
