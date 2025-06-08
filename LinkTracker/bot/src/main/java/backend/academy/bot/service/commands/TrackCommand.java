package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackCommand implements Command {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean shouldBeReplied() {
        return true;
    }

    @Override
    public String getCommand() {
        return "/track";
    }

    @Override
    public String getDescription() {
        return "Добавить ссылку";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {
        redisTemplate.delete(String.valueOf(chatId));
        return SEND_LINK_MESSAGE;
    }
}
