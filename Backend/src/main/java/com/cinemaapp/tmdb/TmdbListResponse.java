package com.cinemaapp.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbListResponse {

    private List<TmdbMovieSummary> results;

    public List<TmdbMovieSummary> getResults() { return results; }
    public void setResults(List<TmdbMovieSummary> results) { this.results = results; }
}
