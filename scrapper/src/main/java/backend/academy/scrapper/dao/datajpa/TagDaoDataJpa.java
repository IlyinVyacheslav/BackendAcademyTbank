package backend.academy.scrapper.dao.datajpa;

import backend.academy.scrapper.dao.TagDao;
import backend.academy.scrapper.dao.datajpa.repo.TagRepo;
import backend.academy.scrapper.model.entity.ChatEntity;
import backend.academy.scrapper.model.entity.LinkEntity;
import backend.academy.scrapper.model.entity.TagEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("data-jpa")
@Repository
@RequiredArgsConstructor
public class TagDaoDataJpa implements TagDao {
    private final TagRepo tagRepo;

    @Override
    public void addTag(Long chatId, Long linkId, String tag) {
        TagEntity newTag = new TagEntity();
        newTag.tag(tag);

        LinkEntity link = new LinkEntity();
        link.linkId(linkId);
        newTag.link(link);

        ChatEntity chat = new ChatEntity();
        chat.chatId(chatId);
        newTag.chat(chat);

        tagRepo.saveAndFlush(newTag);
    }

    @Override
    public List<String> getAllTagsByChatIdAndLinkId(Long chatId, Long linkId) {
        return tagRepo.findAllByChat_ChatIdAndLink_LinkId(chatId, linkId).stream()
                .map(TagEntity::tag)
                .toList();
    }

    @Override
    public void removeAllTagsFromChatByLinkId(Long chatId, Long linkId) {
        List<TagEntity> tags = tagRepo.findAllByChat_ChatIdAndLink_LinkId(chatId, linkId);
        tags.forEach(tagRepo::delete);
    }
}
