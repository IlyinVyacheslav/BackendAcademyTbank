package backend.academy.scrapper.service.digest;

import backend.academy.dto.LinkUpdate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigestStorage {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "digest:";

    public void addToDigest(LinkUpdate update) {
        String key = PREFIX + update.id();
        redisTemplate.opsForList().rightPush(key, update);
    }

    public List<LinkUpdate> getAndClearAllDigests() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");

        if (keys == null || keys.isEmpty()) return List.of();

        List<LinkUpdate> allUpdates = new ArrayList<>();

        for (String key : keys) {
            List<Object> rawList = redisTemplate.opsForList().range(key, 0, -1);
            redisTemplate.delete(key);
            if (rawList != null) {
                rawList.stream()
                        .filter(LinkUpdate.class::isInstance)
                        .map(LinkUpdate.class::cast)
                        .forEach(allUpdates::add);
            }
        }

        return allUpdates;
    }
}
