package backend.academy.scrapper.config;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.clients.bot.BotClient;
import backend.academy.scrapper.clients.bot.BotClientHttp;
import backend.academy.scrapper.clients.bot.BotClientKafka;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ScrapperAppConfiguration {
    private static final String BASE_BOT_URL = "http://localhost:8080";

    @Bean
    public WebClient botWebClient() {
        return WebClient.builder().baseUrl(BASE_BOT_URL).build();
    }

    @Bean
    public WebClient gitHubWebClient(ScrapperConfig scrapperConfig) {
        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + scrapperConfig.githubToken())
                .build();
    }

    @Bean
    public WebClient stackOverflowWebClient(ScrapperConfig scrapperConfig) {
        return WebClient.builder().baseUrl("https://api.stackexchange.com").build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.message-transport", havingValue = "Kafka")
    public BotClient botClientKafka(KafkaTemplate<Long, String> kafkaTemplate, ScrapperConfig scrapperConfig) {
        return new BotClientKafka(kafkaTemplate, new ObjectMapper(), scrapperConfig);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.message-transport", havingValue = "HTTP", matchIfMissing = true)
    public BotClient botClientHttp(WebClient botWebClient) {
        return new BotClientHttp(botWebClient);
    }
}
