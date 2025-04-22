package com.cinemaapp.repository;

import com.cinemaapp.models.Reserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @EntityGraph(attributePaths = {"filme"})
    List<Reserva> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"filme"})
    List<Reserva> findByFilmeId(Long filmeId);

    @EntityGraph(attributePaths = {"filme"})
    List<Reserva> findByStatus(String status);

    @EntityGraph(attributePaths = {"filme"})
    List<Reserva> findAll();
    @EntityGraph(attributePaths = {"filme"})
    List<Reserva> findByUsuarioIdAndStatus(Long usuarioId, String status);

}
