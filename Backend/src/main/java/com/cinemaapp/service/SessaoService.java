package com.cinemaapp.service;

import com.cinemaapp.models.Reserva;
import com.cinemaapp.models.Sessao;
import com.cinemaapp.repository.ReservaRepository;
import com.cinemaapp.repository.SessaoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final ReservaRepository reservaRepository;

    public SessaoService(SessaoRepository sessaoRepository, ReservaRepository reservaRepository) {
        this.sessaoRepository = sessaoRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Sessao> findAll() {
        return sessaoRepository.findAll();
    }

    public Optional<Sessao> findById(Long id) {
        return sessaoRepository.findByIdWithFilme(id);
    }

    public List<Sessao> findFuturasByFilme(Long filmeId) {
        return sessaoRepository.findFuturasByFilmeId(filmeId, LocalDateTime.now());
    }

    public Sessao save(Sessao sessao) {
        return sessaoRepository.save(sessao);
    }

    public void deleteById(Long id) {
        sessaoRepository.deleteById(id);
    }

    public Set<String> getAssentosOcupados(Long sessaoId) {
        Set<String> ocupados = new HashSet<>();
        List<Reserva> reservas = reservaRepository.findBySessaoIdAndStatus(sessaoId, "CONFIRMADA");
        for (Reserva r : reservas) {
            if (r.getAssentos() != null && !r.getAssentos().isBlank()) {
                ocupados.addAll(Arrays.asList(r.getAssentos().split(",")));
            }
        }
        return ocupados;
    }

    public int getAssentosDisponiveis(Long sessaoId) {
        Sessao sessao = sessaoRepository.findById(sessaoId).orElseThrow();
        Integer ocupados = reservaRepository.countAssentosOcupados(sessaoId);
        return sessao.getCapacidade() - (ocupados != null ? ocupados : 0);
    }
}
