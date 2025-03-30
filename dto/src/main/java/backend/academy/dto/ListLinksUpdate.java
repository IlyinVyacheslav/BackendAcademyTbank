package backend.academy.dto;

import java.util.List;

public record ListLinksUpdate(List<LinkUpdatedAt> updates, int size) {}
