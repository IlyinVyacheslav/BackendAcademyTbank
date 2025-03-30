package backend.academy.bot.service;

import backend.academy.bot.exceptions.InvalidTagAndTimeFormatException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {
    private static final String TAG_TIME_PATTERN = "^(\\S+)\\s(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})$";

    public static TagAndTime parseTagAndTime(String message) {
        Pattern pattern = Pattern.compile(TAG_TIME_PATTERN);
        Matcher matcher = pattern.matcher(message);

        if (!matcher.matches()) {
            throw new InvalidTagAndTimeFormatException("Неверный формат ввода");
        }
        String tag = matcher.group(1);
        String timestampStr = matcher.group(2);

        LocalDateTime timestamp = LocalDateTime.parse(timestampStr);

        return new TagAndTime(tag, timestamp);
    }

    public record TagAndTime(String tag, LocalDateTime time) {}
}
