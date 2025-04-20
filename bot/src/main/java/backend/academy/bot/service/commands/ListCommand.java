package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import backend.academy.logger.LoggerHelper;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ListCommand implements Command {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String getCommand() {
        return "/list";
    }

    @Override
    public String getDescription() {
        return "Список отслеживаемых ссылок";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {
        String urlList = redisTemplate.opsForValue().get(String.valueOf(chatId));
        if (urlList != null) {
            LoggerHelper.info("List was found in cache", Map.of("List", urlList));
            return urlList;
        }
        Optional<String> byChatId = Optional.ofNullable(scrapperClient
                .getAllLinks(chatId)
                .map(links -> {
                    if (links == null || links.isEmpty()) {
                        return EMPTY_LIST_MESSAGE;
                    } else {
                        return links.toString();
                    }
                })
                .onErrorResume(error -> {
                    LoggerHelper.error("Error while getting links", error);
                    return Mono.error(error);
                })
                .block(timeout));
        byChatId.ifPresent(l -> redisTemplate.opsForValue().set(String.valueOf(chatId), l));
        return byChatId.orElse(null);
    }
}
