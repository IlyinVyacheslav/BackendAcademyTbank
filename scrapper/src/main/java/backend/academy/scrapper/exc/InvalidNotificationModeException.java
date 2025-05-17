package backend.academy.scrapper.exc;

import backend.academy.scrapper.service.digest.NotificationMode;
import java.util.Arrays;

public class InvalidNotificationModeException extends BaseChatRepositoryException {
    public InvalidNotificationModeException(String mode) {
        super(String.format(
                "Got invalid notification mode: %s, expected: %s", mode, Arrays.toString(NotificationMode.values())));
    }
}
