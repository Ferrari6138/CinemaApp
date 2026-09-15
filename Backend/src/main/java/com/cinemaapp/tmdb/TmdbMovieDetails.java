package com.cinemaapp.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbMovieDetails {

    private Long id;
    private Integer runtime;
    private List<TmdbGenre> genres;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getRuntime() { return runtime; }
    public void setRuntime(Integer runtime) { this.runtime = runtime; }

    public List<TmdbGenre> getGenres() { return genres; }
    public void setGenres(List<TmdbGenre> genres) { this.genres = genres; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbGenre {
        private Integer id;
        private String name;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
