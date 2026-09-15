package com.cinemaapp.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbMovieSummary {

    private Long id;
    private String title;
    private String overview;
    private Boolean adult;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public Boolean getAdult() { return adult; }
    public void setAdult(Boolean adult) { this.adult = adult; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public List<Integer> getGenreIds() { return genreIds; }
    public void setGenreIds(List<Integer> genreIds) { this.genreIds = genreIds; }
}
