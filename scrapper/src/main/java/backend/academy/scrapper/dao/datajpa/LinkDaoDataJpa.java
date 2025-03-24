package backend.academy.scrapper.dao.datajpa;

import backend.academy.scrapper.dao.LinkDao;
import backend.academy.scrapper.dao.datajpa.repo.LinkRepo;
import backend.academy.scrapper.exc.LinkNotFoundException;
import backend.academy.scrapper.model.dto.Link;
import backend.academy.scrapper.model.entity.ChatEntity;
import backend.academy.scrapper.model.entity.LinkEntity;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Profile("data-jpa")
@Repository
@RequiredArgsConstructor
public class LinkDaoDataJpa implements LinkDao {
    private final LinkRepo linkRepo;

    @Override
    public Long addLink(String url) {
        LinkEntity newLink = new LinkEntity();
        newLink.url(url);
        newLink.lastModified(new Timestamp(System.currentTimeMillis()));
        return linkRepo.saveAndFlush(newLink).linkId();
    }

    @Transactional
    @Override
    public void addLinkToChat(long chatId, long linkId) {
        LinkEntity link = linkRepo.findById(linkId).orElseThrow(() -> new LinkNotFoundException(linkId));
        ChatEntity chat = new ChatEntity();
        chat.chatId(chatId);
        link.chats().add(chat);
        linkRepo.saveAndFlush(link);
    }

    @Override
    public Long getLinkIdByUrl(String url) {
        return linkRepo.findByUrl(url).map(LinkEntity::linkId).orElse(null);
    }

    @Override
    public String getLinkUrlById(Long linkId) {
        return linkRepo.findById(linkId).map(LinkEntity::url).orElse(null);
    }

    @Transactional
    @Override
    public boolean removeLinkFromChatById(long chatId, long linkId) {
        LinkEntity link = linkRepo.findById(linkId).orElseThrow(() -> new LinkNotFoundException(linkId));
        boolean wasRemoved = link.chats().removeIf(chat -> chat.chatId() == chatId);
        if (wasRemoved) {
            linkRepo.saveAndFlush(link);
            return true;
        }
        return false;
    }

    @Override
    public void updateLink(long linkId, Timestamp lastModified) {
        LinkEntity link = linkRepo.findById(linkId).orElseThrow(() -> new LinkNotFoundException(linkId));
        link.lastModified(lastModified);
        linkRepo.saveAndFlush(link);
    }

    @Override
    public List<Long> findLinksByChatId(Long chatId) {
        return linkRepo.findLinkIdsByChatId(chatId);
    }

    @Override
    public List<Link> getAllLinks() {
        return linkRepo.findAll().stream()
                .map(link -> new Link(link.linkId(), link.url(), link.lastModified()))
                .toList();
    }

    @Transactional
    @Override
    public List<Long> getAllChatIdsByLinkId(Long linkId) {
        Optional<LinkEntity> linkEntity = linkRepo.findById(linkId);
        return linkEntity
                .map(link -> link.chats().stream().map(ChatEntity::chatId).toList())
                .orElse(List.of());
    }
}
