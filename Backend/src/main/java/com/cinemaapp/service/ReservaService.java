package com.cinemaapp.service;

import com.cinemaapp.models.Reserva;
import com.cinemaapp.models.Sessao;
import com.cinemaapp.models.Usuario;
import com.cinemaapp.repository.ReservaRepository;
import com.cinemaapp.repository.SessaoRepository;
import com.cinemaapp.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SessaoRepository sessaoRepository;
    private final SessaoService sessaoService;

    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioRepository usuarioRepository,
                          SessaoRepository sessaoRepository,
                          SessaoService sessaoService) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sessaoRepository = sessaoRepository;
        this.sessaoService = sessaoService;
    }

    @Transactional
    public Reserva criarReserva(Long sessaoId, String assentosSelecionados, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Sessao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));

        // Valida se os assentos já não estão ocupados
        Set<String> ocupados = sessaoService.getAssentosOcupados(sessaoId);
        String[] assentos = assentosSelecionados.split(",");
        for (String assento : assentos) {
            if (ocupados.contains(assento.trim())) {
                throw new RuntimeException("Assento " + assento.trim() + " já está ocupado.");
            }
        }

        int quantidade = assentos.length;
        BigDecimal valorTotal = sessao.getFilme().getPreco()
                .multiply(BigDecimal.valueOf(quantidade));

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setSessao(sessao);
        reserva.setQuantidade(quantidade);
        reserva.setAssentos(assentosSelecionados.trim());
        reserva.setDataReserva(LocalDateTime.now());
        reserva.setStatus("CONFIRMADA");
        reserva.setValorTotal(valorTotal);

        return reservaRepository.save(reserva);
    }

    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    public List<Reserva> findReservasAtivasByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return reservaRepository.findByUsuarioIdAndStatus(usuario.getId(), "CONFIRMADA");
    }

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public List<Reserva> findRecentes() {
        return reservaRepository.findTop10ByOrderByDataReservaDesc();
    }

    @Transactional
    public void cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        reserva.setStatus("CANCELADA");
        reservaRepository.save(reserva);
    }

    public Long countConfirmadas() {
        return reservaRepository.countConfirmadas();
    }

    public BigDecimal sumReceita() {
        return reservaRepository.sumReceita();
    }
}
