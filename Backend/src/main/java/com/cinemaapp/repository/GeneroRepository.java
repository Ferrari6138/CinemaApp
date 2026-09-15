package com.cinemaapp.repository;

import com.cinemaapp.models.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GeneroRepository extends JpaRepository<Genero, Long> {
    Optional<Genero> findByNome(String nome);
    Optional<Genero> findByNomeIgnoreCase(String nome);
    boolean existsByNome(String nome);
}
