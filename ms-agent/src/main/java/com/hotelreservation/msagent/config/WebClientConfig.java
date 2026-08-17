package com.hotelreservation.msagent.config;

import com.hotelreservation.msagent.config.properties.CatalogProperties;
import com.hotelreservation.msagent.config.properties.GeminiProperties;
import com.hotelreservation.msagent.constants.ConfigConstants;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/** Configuration for the reactive {@link WebClient} instances used to call external services. */
@Configuration
public class WebClientConfig {

  /**
   * Builds the {@link WebClient} used to call the Gemini Interactions API, preconfigured with the
   * API key, revision, and timeout headers.
   *
   * @param geminiProperties the Gemini connection and timeout settings
   * @return the configured Gemini web client
   */
  @Bean
  public WebClient geminiWebClient(GeminiProperties geminiProperties) {
    HttpClient httpClient =
        HttpClient.create()
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(geminiProperties.timeoutMs()))
            .doOnConnected(
                conn ->
                    conn.addHandlerLast(
                        new ReadTimeoutHandler(
                            geminiProperties.timeoutMs(), TimeUnit.MILLISECONDS)));

    return WebClient.builder()
        .baseUrl(geminiProperties.baseUrl())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .defaultHeader(ConfigConstants.Gemini.X_API_HEADER, geminiProperties.apiKey())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(
            ConfigConstants.Gemini.API_REVISION_HEADER, ConfigConstants.Gemini.API_REVISION_VALUE)
        .build();
  }

  /**
   * Builds the {@link WebClient} used to call ms-catalog.
   *
   * @param catalogProperties the ms-catalog connection settings
   * @return the configured catalog web client
   */
  @Bean
  public WebClient catalogWebClient(CatalogProperties catalogProperties) {
    HttpClient httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .doOnConnected(
                conn -> conn.addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS)));

    return WebClient.builder()
        .baseUrl(catalogProperties.baseUrl())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }
}
