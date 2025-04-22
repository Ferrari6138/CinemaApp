package com.cinemaapp.service;

import com.cinemaapp.models.Reserva;
import com.cinemaapp.models.Usuario;
import com.cinemaapp.models.Filme;
import com.cinemaapp.repository.ReservaRepository;
import com.cinemaapp.repository.UsuarioRepository;
import com.cinemaapp.repository.FilmeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FilmeRepository filmeRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioRepository usuarioRepository,
                          FilmeRepository filmeRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.filmeRepository = filmeRepository;
    }

    @Transactional
    public Reserva criarReserva(Long filmeId, Integer quantidade, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado"));

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setFilme(filme);
        reserva.setQuantidade(quantidade);
        reserva.setDataReserva(LocalDateTime.now());
        reserva.setStatus("CONFIRMADA");

        return reservaRepository.save(reserva);
    }



    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public void cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        reserva.setStatus("CANCELADA");
        reservaRepository.save(reserva);
    }

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public List<Reserva> findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return reservaRepository.findByUsuarioIdAndStatus(usuario.getId(),"CONFIRMADA");
    }

}