package backend.academy.scrapper.model.dto;

import java.sql.Timestamp;

public record Link(Long linkId, String url, Timestamp lastModified) {}
