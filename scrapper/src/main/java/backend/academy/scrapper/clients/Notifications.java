package backend.academy.scrapper.clients;

import java.sql.Timestamp;
import java.util.Optional;

public record Notifications(String message, Timestamp updatedAt, Optional<String> user) {
    public Notifications(String message, Timestamp updatedAt) {
        this(message, updatedAt, Optional.empty());
    }
}
