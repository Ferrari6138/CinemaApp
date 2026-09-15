package com.cinemaapp.tmdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

@Component
public class TmdbClient {

    private static final Logger log = LoggerFactory.getLogger(TmdbClient.class);
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    private final RestClient restClient;

    @Value("${app.tmdb.api-key:}")
    private String apiKey;

    public TmdbClient() {
        this.restClient = RestClient.builder().baseUrl(BASE_URL).build();
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    /** Filmes em cartaz nos cinemas, segundo o TMDB (região Brasil, idioma pt-BR). */
    public List<TmdbMovieSummary> buscarFilmesEmCartaz() {
        if (!isConfigured()) {
            log.info("TMDB desabilitado: nenhuma api-key configurada (app.tmdb.api-key / TMDB_API_KEY).");
            return Collections.emptyList();
        }
        try {
            TmdbListResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/movie/now_playing")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "pt-BR")
                            .queryParam("region", "BR")
                            .queryParam("page", 1)
                            .build())
                    .retrieve()
                    .body(TmdbListResponse.class);
            return response != null && response.getResults() != null
                    ? response.getResults()
                    : Collections.emptyList();
        } catch (RestClientException e) {
            log.warn("Falha ao buscar filmes em cartaz no TMDB: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /** Detalhes completos de um filme (duração e gêneros), necessários para importar corretamente. */
    public TmdbMovieDetails buscarDetalhes(Long tmdbId) {
        if (!isConfigured()) return null;
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/movie/{id}")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "pt-BR")
                            .build(tmdbId))
                    .retrieve()
                    .body(TmdbMovieDetails.class);
        } catch (RestClientException e) {
            log.warn("Falha ao buscar detalhes do filme TMDB {}: {}", tmdbId, e.getMessage());
            return null;
        }
    }
}
