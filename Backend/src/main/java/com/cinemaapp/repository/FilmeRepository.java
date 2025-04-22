package com.cinemaapp.repository;

import com.cinemaapp.models.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalTime;
import java.util.List;

public interface FilmeRepository extends JpaRepository<Filme, Long> {

    List<Filme> findByHorarioBetween(LocalTime inicio, LocalTime fim);


}