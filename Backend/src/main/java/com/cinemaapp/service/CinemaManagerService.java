package com.cinemaapp.service;

import com.cinemaapp.models.Cinema;
import com.cinemaapp.models.Filme;
import com.cinemaapp.models.Genero;
import com.cinemaapp.models.Sessao;
import com.cinemaapp.repository.CinemaRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Automação que mantém o catálogo "vivo": importa lançamentos recentes do TMDB,
 * tira de cartaz os filmes que ele mesmo importou e que não estão mais em cartaz
 * no TMDB, e garante que todo filme em cartaz tenha sessões futuras suficientes -
 * como faria um gestor de cinema cuidando da programação.
 */
@Service
public class CinemaManagerService {

    private static final Logger log = LoggerFactory.getLogger(CinemaManagerService.class);

    private final TmdbClient tmdbClient;
    private final FilmeRepository filmeRepository;
    private final GeneroRepository generoRepository;
    private final SessaoRepository sessaoRepository;
    private final CinemaRepository cinemaRepository;

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
                                 GeneroRepository generoRepository, SessaoRepository sessaoRepository,
                                 CinemaRepository cinemaRepository) {
        this.tmdbClient = tmdbClient;
        this.filmeRepository = filmeRepository;
        this.generoRepository = generoRepository;
        this.sessaoRepository = sessaoRepository;
        this.cinemaRepository = cinemaRepository;
    }

    public record Resultado(int filmesImportados, int filmesRemovidos, int sessoesCriadas) {}

    @Transactional
    public Resultado executar() {
        List<TmdbMovieSummary> emCartazTmdb = tmdbClient.buscarFilmesEmCartaz();

        int filmesImportados = importarFilmesRecentes(emCartazTmdb);
        int filmesRemovidos = atualizarStatusCartaz(emCartazTmdb);
        int sessoesCriadas = gerarSessoesFuturas();

        log.info("Gestor de cinema: {} filme(s) importado(s), {} filme(s) tirado(s) de cartaz, {} sessão(ões) criada(s).",
                filmesImportados, filmesRemovidos, sessoesCriadas);
        return new Resultado(filmesImportados, filmesRemovidos, sessoesCriadas);
    }

    @Transactional
    public int importarFilmesRecentes(List<TmdbMovieSummary> emCartazTmdb) {
        int importados = 0;

        for (TmdbMovieSummary resumo : emCartazTmdb) {
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
            filme.setEmCartaz(true);
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

    /**
     * Sincroniza o status "em cartaz" apenas dos filmes que o próprio gestor importou
     * (tmdbId != null). Filmes cadastrados manualmente pelo admin nunca são tocados aqui.
     */
    @Transactional
    public int atualizarStatusCartaz(List<TmdbMovieSummary> emCartazTmdb) {
        Set<Long> idsEmCartaz = new HashSet<>();
        for (TmdbMovieSummary resumo : emCartazTmdb) {
            if (resumo.getId() != null) idsEmCartaz.add(resumo.getId());
        }

        int removidos = 0;
        for (Filme filme : filmeRepository.findAll()) {
            if (filme.getTmdbId() == null) continue; // cadastrado manualmente, não mexe

            boolean deveEstarEmCartaz = idsEmCartaz.contains(filme.getTmdbId());
            boolean estaEmCartaz = filme.isEmCartaz();

            if (deveEstarEmCartaz && !estaEmCartaz) {
                filme.setEmCartaz(true);
                filmeRepository.save(filme);
            } else if (!deveEstarEmCartaz && estaEmCartaz) {
                filme.setEmCartaz(false);
                filmeRepository.save(filme);
                desativarSessoesFuturas(filme);
                removidos++;
            }
        }
        return removidos;
    }

    private void desativarSessoesFuturas(Filme filme) {
        List<Sessao> futuras = sessaoRepository.findFuturasByFilmeId(filme.getId(), LocalDateTime.now());
        for (Sessao sessao : futuras) {
            sessao.setAtiva(false);
            sessaoRepository.save(sessao);
        }
    }

    @Transactional
    public int gerarSessoesFuturas() {
        LocalDateTime agora = LocalDateTime.now();
        List<Filme> filmes = filmeRepository.findAll().stream()
                .filter(Filme::isEmCartaz)
                .toList();
        List<Cinema> cinemas = cinemaRepository.findAll();
        if (cinemas.isEmpty()) {
            log.warn("Nenhum cinema cadastrado - gestor não pode gerar sessões automaticamente.");
            return 0;
        }
        int totalCriadas = 0;

        for (Filme filme : filmes) {
            int futuras = sessaoRepository.findFuturasByFilmeId(filme.getId(), agora).size();
            int faltam = minimoSessoesFuturas - futuras;
            if (faltam <= 0) continue;

            int criadas = 0;
            LocalDate dia = LocalDate.now().plusDays(1);
            LocalDate limite = LocalDate.now().plusDays(diasSessoesFuturas);

            busca:
            while (criadas < faltam && !dia.isAfter(limite)) {
                for (String horarioStr : horarios) {
                    for (Cinema cinema : cinemas) {
                        for (String sala : salas) {
                            if (criadas >= faltam) break busca;

                            LocalDateTime dataHora = dia.atTime(LocalTime.parse(horarioStr.trim()));
                            if (sessaoRepository.existsByDataHoraAndSalaAndCinemaId(dataHora, sala, cinema.getId())) {
                                continue; // sala já ocupada nesse horário, nesse cinema
                            }

                            Sessao sessao = new Sessao();
                            sessao.setFilme(filme);
                            sessao.setCinema(cinema);
                            sessao.setDataHora(dataHora);
                            sessao.setSala(sala);
                            sessao.setCapacidade(capacidadePadrao);
                            sessao.setAtiva(true);
                            sessaoRepository.save(sessao);
                            criadas++;
                            totalCriadas++;
                        }
                    }
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
