package com.cinemaapp.repository;

import com.cinemaapp.models.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    Optional<Cinema> findByNome(String nome);
}
