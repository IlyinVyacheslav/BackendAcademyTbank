package backend.academy.scrapper.repository;

import backend.academy.scrapper.model.Link;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class LinkRepository {
    private final AtomicLong URL_ID = new AtomicLong(0);
    private final Map<Long, Link> LINKS = new ConcurrentHashMap<>();

    public Link getLink(long id) {
        return LINKS.get(id);
    }

    public Link getLinkByUrl(String url) {
        return LINKS.values().stream()
                .filter(link -> link.url().equals(url))
                .findAny()
                .orElse(null);
    }

    public Link addLink(String url) {
        Link link = new Link(URL_ID.incrementAndGet(), url, null);
        LINKS.put(link.id(), link);
        return link;
    }

    public List<Link> getAllLinks() {
        return new ArrayList<>(LINKS.values());
    }

    public void updateLink(Link link) {
        LINKS.put(link.id(), link);
    }
}
