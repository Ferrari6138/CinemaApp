package com.cinemaapp.service;

import com.cinemaapp.models.Filme;
import com.cinemaapp.models.Genero;
import com.cinemaapp.models.Sessao;
import com.cinemaapp.repository.FilmeRepository;
import com.cinemaapp.repository.GeneroRepository;
import com.cinemaapp.repository.SessaoRepository;
import com.cinemaapp.tmdb.TmdbClient;
import com.cinemaapp.tmdb.TmdbMovieDetails;
import com.cinemaapp.tmdb.TmdbMovieSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Automação que mantém o catálogo "vivo": importa lançamentos recentes do TMDB
 * e garante que todo filme tenha sessões futuras suficientes, como faria um
 * gestor de cinema cuidando da programação.
 */
@Service
public class CinemaManagerService {

    private static final Logger log = LoggerFactory.getLogger(CinemaManagerService.class);

    private final TmdbClient tmdbClient;
    private final FilmeRepository filmeRepository;
    private final GeneroRepository generoRepository;
    private final SessaoRepository sessaoRepository;

    @Value("${app.cinema.preco-padrao:28.00}")
    private BigDecimal precoPadrao;

    @Value("${app.cinema.minimo-sessoes-futuras:3}")
    private int minimoSessoesFuturas;

    @Value("${app.cinema.dias-sessoes-futuras:14}")
    private int diasSessoesFuturas;

    @Value("${app.cinema.salas:Sala 1,Sala 2,Sala 3,Sala 4}")
    private List<String> salas;

    @Value("${app.cinema.horarios:14:00,17:00,20:00}")
    private List<String> horarios;

    @Value("${app.cinema.capacidade-padrao:100}")
    private int capacidadePadrao;

    public CinemaManagerService(TmdbClient tmdbClient, FilmeRepository filmeRepository,
                                 GeneroRepository generoRepository, SessaoRepository sessaoRepository) {
        this.tmdbClient = tmdbClient;
        this.filmeRepository = filmeRepository;
        this.generoRepository = generoRepository;
        this.sessaoRepository = sessaoRepository;
    }

    public record Resultado(int filmesImportados, int sessoesCriadas) {}

    @Transactional
    public Resultado executar() {
        int filmesImportados = importarFilmesRecentes();
        int sessoesCriadas = gerarSessoesFuturas();
        log.info("Gestor de cinema: {} filme(s) importado(s) do TMDB, {} sessão(ões) criada(s).",
                filmesImportados, sessoesCriadas);
        return new Resultado(filmesImportados, sessoesCriadas);
    }

    @Transactional
    public int importarFilmesRecentes() {
        List<TmdbMovieSummary> resumos = tmdbClient.buscarFilmesEmCartaz();
        int importados = 0;

        for (TmdbMovieSummary resumo : resumos) {
            if (resumo.getId() == null || resumo.getTitle() == null
                    || filmeRepository.existsByTmdbId(resumo.getId())) {
                continue;
            }

            Filme filme = new Filme();
            filme.setTmdbId(resumo.getId());
            filme.setTitulo(resumo.getTitle());
            filme.setDescricao(resumo.getOverview());
            filme.setAno(extrairAno(resumo.getReleaseDate()));
            filme.setClassificacao(Boolean.TRUE.equals(resumo.getAdult()) ? "18" : "Livre");
            filme.setPreco(precoPadrao);
            if (resumo.getPosterPath() != null && !resumo.getPosterPath().isBlank()) {
                filme.setImagem("https://image.tmdb.org/t/p/w500" + resumo.getPosterPath());
            }

            TmdbMovieDetails detalhes = tmdbClient.buscarDetalhes(resumo.getId());
            if (detalhes != null) {
                filme.setDuracao(detalhes.getRuntime());
                filme.setGeneros(mapearGeneros(detalhes.getGenres()));
            }

            filmeRepository.save(filme);
            importados++;
        }
        return importados;
    }

    @Transactional
    public int gerarSessoesFuturas() {
        LocalDateTime agora = LocalDateTime.now();
        List<Filme> filmes = filmeRepository.findAll();
        int totalCriadas = 0;

        for (int i = 0; i < filmes.size(); i++) {
            Filme filme = filmes.get(i);
            int futuras = sessaoRepository.findFuturasByFilmeId(filme.getId(), agora).size();
            int faltam = minimoSessoesFuturas - futuras;
            if (faltam <= 0) continue;

            String sala = salas.get(i % salas.size());
            int criadas = 0;
            LocalDate dia = LocalDate.now().plusDays(1);
            LocalDate limite = LocalDate.now().plusDays(diasSessoesFuturas);

            while (criadas < faltam && !dia.isAfter(limite)) {
                for (String horarioStr : horarios) {
                    if (criadas >= faltam) break;
                    LocalTime horario = LocalTime.parse(horarioStr.trim());
                    LocalDateTime dataHora = dia.atTime(horario);

                    if (sessaoRepository.existsByFilmeIdAndDataHoraAndSala(filme.getId(), dataHora, sala)) {
                        continue;
                    }

                    Sessao sessao = new Sessao();
                    sessao.setFilme(filme);
                    sessao.setDataHora(dataHora);
                    sessao.setSala(sala);
                    sessao.setCapacidade(capacidadePadrao);
                    sessao.setAtiva(true);
                    sessaoRepository.save(sessao);
                    criadas++;
                    totalCriadas++;
                }
                dia = dia.plusDays(1);
            }
        }
        return totalCriadas;
    }

    private List<Genero> mapearGeneros(List<TmdbMovieDetails.TmdbGenre> generosTmdb) {
        List<Genero> generos = new ArrayList<>();
        if (generosTmdb == null) return generos;
        for (TmdbMovieDetails.TmdbGenre g : generosTmdb) {
            if (g.getName() == null || g.getName().isBlank()) continue;
            Genero genero = generoRepository.findByNomeIgnoreCase(g.getName())
                    .orElseGet(() -> generoRepository.save(new Genero(g.getName())));
            generos.add(genero);
        }
        return generos;
    }

    private Integer extrairAno(String releaseDate) {
        if (releaseDate == null || releaseDate.length() < 4) return null;
        try {
            return Integer.parseInt(releaseDate.substring(0, 4));
        } catch (NumberFormatException | DateTimeParseException e) {
            return null;
        }
    }
}
