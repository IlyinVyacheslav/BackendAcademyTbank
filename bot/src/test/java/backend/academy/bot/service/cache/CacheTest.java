package backend.academy.bot.service.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.clients.ScrapperClient;
import backend.academy.bot.service.commands.ListCommand;
import backend.academy.bot.service.commands.TrackCommand;
import backend.academy.bot.service.commands.UntrackCommand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import reactor.core.publisher.Mono;

public class CacheTest {
    private final String chatId = "123";
    private final Long chatIdLong = 123L;
    private RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private ScrapperClient scrapperClient;
    private ListCommand listCommand;
    private TrackCommand trackCommand;
    private UntrackCommand untrackCommand;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        scrapperClient = mock(ScrapperClient.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        listCommand = new ListCommand(redisTemplate);
        trackCommand = new TrackCommand(redisTemplate);
        untrackCommand = new UntrackCommand(redisTemplate);
    }

    @Test
    void testCacheStoresData() {
        when(valueOperations.get(chatId)).thenReturn(null);
        when(scrapperClient.getAllLinks(chatIdLong)).thenReturn(Mono.just(List.of("http://example.com")));
        String expectedResult = "[http://example.com]";

        String result = listCommand.execute(scrapperClient, chatIdLong);

        verify(valueOperations, times(1)).get(chatId);
        verify(valueOperations, times(1)).set(chatId, expectedResult);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testCacheReturnsStoredData() {
        String testResult = "[http://example.com]";
        when(valueOperations.get(chatId)).thenReturn(testResult);

        String result = listCommand.execute(scrapperClient, chatIdLong);

        verify(scrapperClient, never()).getAllLinks(any());
        verify(valueOperations, never()).set(chatId, testResult);
        assertThat(result).isEqualTo(testResult);
    }

    @Test
    void testTrackCommandCacheInvalidation() {
        trackCommand.execute(scrapperClient, chatIdLong);

        verify(redisTemplate, times(1)).delete(chatId);
    }

    @Test
    void testUntrackCommandCacheInvalidation() {
        untrackCommand.execute(scrapperClient, chatIdLong);

        verify(redisTemplate, times(1)).delete(chatId);
    }
}
