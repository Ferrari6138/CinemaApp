package com.cinemaapp.repository;

import com.cinemaapp.models.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    @Query("SELECT s FROM Sessao s JOIN FETCH s.filme LEFT JOIN FETCH s.cinema WHERE s.id = :id")
    Optional<Sessao> findByIdWithFilme(@Param("id") Long id);

    List<Sessao> findByFilmeIdAndAtivaTrue(Long filmeId);

    @Query("SELECT s FROM Sessao s WHERE s.filme.id = :filmeId AND s.ativa = true AND s.dataHora > :agora ORDER BY s.dataHora ASC")
    List<Sessao> findFuturasByFilmeId(@Param("filmeId") Long filmeId, @Param("agora") LocalDateTime agora);

    @Query("SELECT s FROM Sessao s WHERE s.ativa = true ORDER BY s.dataHora ASC")
    List<Sessao> findAllAtivas();

    // usado pelo gestor automático: uma sala de um cinema não pode ter duas sessões
    // (de filmes diferentes ou não) no mesmo horário
    boolean existsByDataHoraAndSalaAndCinemaId(LocalDateTime dataHora, String sala, Long cinemaId);

    // backfill: sessões antigas criadas antes do campo cinema existir
    List<Sessao> findByCinemaIsNull();
}
