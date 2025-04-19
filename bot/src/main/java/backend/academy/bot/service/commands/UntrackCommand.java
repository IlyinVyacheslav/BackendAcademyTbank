package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand implements Command {
    private final RedisTemplate<Long, String> redisTemplate;

    @Autowired
    public UntrackCommand(RedisTemplate<Long, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean shouldBeReplied() {
        return true;
    }

    @Override
    public String getCommand() {
        return "/untrack";
    }

    @Override
    public String getDescription() {
        return "Удалить ссылку";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {
        redisTemplate.delete(chatId);
        return UNTRACK_LINK_MESSAGE;
    }
}
