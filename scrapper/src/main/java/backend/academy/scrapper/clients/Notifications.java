package backend.academy.scrapper.clients;

import java.sql.Timestamp;

public record Notifications(String message, Timestamp updatedAt) {}
