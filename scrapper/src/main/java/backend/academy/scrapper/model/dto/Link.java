package backend.academy.scrapper.model.dto;

import java.sql.Timestamp;

public record Link(Long id, String url, Timestamp lastModified) {}
