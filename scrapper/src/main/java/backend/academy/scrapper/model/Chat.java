package backend.academy.scrapper.model;

import java.util.ArrayList;
import java.util.List;

public record Chat(long chatId, List<LinkInfo> linksToFollow) {
    public Chat(long chatId) {
        this(chatId, new ArrayList<>());
    }

    public boolean addLink(LinkInfo link) {
        if (containsUrl(link.linkId())) {
            return false;
        }
        linksToFollow.add(link);
        return true;
    }

    public boolean removeLink(Long linkId) {
        return linksToFollow.remove(linksToFollow.stream()
                .filter(link -> link.linkId().equals(linkId))
                .findFirst()
                .orElse(null));
    }

    public boolean containsUrl(Long linkId) {
        return linksToFollow.stream().anyMatch(link -> link.linkId().equals(linkId));
    }
}
