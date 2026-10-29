package se.iths.sofia.webshopgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Skapar HTTP-klienten som gatewayen använder för anrop till mikrotjänsterna.
 */
@Configuration
public class HttpClientConfig {

    /**
     * Skapar en HTTP-klient med fem sekunders timeout för att upprätta en anslutning.
     * Detta är inte en timeout för hela svaret.
     *
     * @return den gemensamma HTTP-klienten
     */
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }
}
