package backend.academy.bot.config;

import backend.academy.bot.BotConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BotAppConfiguration {
    private static final String BASE_SCRAPPER_URL = "http://localhost:8081";

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(BASE_SCRAPPER_URL).build();
    }

    @Bean
    public TelegramBot telegramBot(BotConfig botConfig) {
        return new TelegramBot(botConfig.telegramToken());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
