package com.cinemaapp.repository;

import com.cinemaapp.models.Reserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @EntityGraph(attributePaths = {"sessao", "sessao.filme"})
    List<Reserva> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"sessao", "sessao.filme"})
    List<Reserva> findByUsuarioIdAndStatus(Long usuarioId, String status);

    @EntityGraph(attributePaths = {"sessao", "sessao.filme", "usuario"})
    List<Reserva> findAll();

    @EntityGraph(attributePaths = {"sessao", "sessao.filme", "usuario"})
    List<Reserva> findTop10ByOrderByDataReservaDesc();

    List<Reserva> findBySessaoIdAndStatus(Long sessaoId, String status);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.status = 'CONFIRMADA'")
    Long countConfirmadas();

    @Query("SELECT COALESCE(SUM(r.valorTotal), 0) FROM Reserva r WHERE r.status = 'CONFIRMADA'")
    BigDecimal sumReceita();

    @Query("SELECT COALESCE(SUM(r.quantidade), 0) FROM Reserva r WHERE r.sessao.id = :sessaoId AND r.status = 'CONFIRMADA'")
    Integer countAssentosOcupados(@Param("sessaoId") Long sessaoId);
}
