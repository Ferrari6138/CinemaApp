package com.cinemaapp.repository;

import com.cinemaapp.models.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface FilmeRepository extends JpaRepository<Filme, Long> {

    @Query("SELECT f FROM Filme f WHERE LOWER(f.titulo) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(f.descricao) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Filme> search(@Param("q") String query);

    @Query("SELECT DISTINCT f FROM Filme f JOIN f.generos g WHERE g.id = :generoId")
    List<Filme> findByGeneroId(@Param("generoId") Long generoId);

    @Query("SELECT f FROM Filme f LEFT JOIN FETCH f.sessoes s LEFT JOIN FETCH s.cinema WHERE f.id = :id")
    Optional<Filme> findByIdWithSessoes(@Param("id") Long id);

    boolean existsByTmdbId(Long tmdbId);
}
