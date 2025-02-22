package backend.academy.scrapper.config;

import backend.academy.scrapper.ScrapperConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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
}
