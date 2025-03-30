package backend.academy.dto;

import java.sql.Timestamp;

public record LinkUpdatedAt(String url, Long updatedAt) {
    public LinkUpdatedAt(String url, Timestamp updatedAt) {
        this(url, updatedAt.getTime());
    }
}
