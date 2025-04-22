package com.cinemaapp.service;

import com.cinemaapp.models.Filme;
import com.cinemaapp.repository.FilmeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class FilmeService {

    private final FilmeRepository filmeRepository;

    public FilmeService(FilmeRepository filmeRepository) {
        this.filmeRepository = filmeRepository;
    }

    public List<Filme> findAll() {
        return filmeRepository.findAll();
    }

    public Optional<Filme> findById(Long id) {
        return filmeRepository.findById(id);
    }

    public List<Filme> findByHorarioBetween(LocalTime inicio, LocalTime fim) {
        return filmeRepository.findByHorarioBetween(inicio, fim);
    }

    public Filme save(Filme filme) {
        return filmeRepository.save(filme);
    }

    public void deleteById(Long id) {
        filmeRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return filmeRepository.existsById(id);
    }

    public void adicionarFilmesDeTeste() {
        Filme filme1 = new Filme();
        filme1.setTitulo("O Poderoso Chefão");
        filme1.setDescricao("Filme sobre a máfia.");
        filme1.setHorario(LocalTime.of(20, 30));
        filme1.setDuracao(175);
        filme1.setClassificacao("18");
        filmeRepository.save(filme1);

        Filme filme2 = new Filme();
        filme2.setTitulo("Interstellar");
        filme2.setDescricao("Viagem épica no espaço.");
        filme2.setHorario(LocalTime.of(21, 00));
        filme2.setDuracao(169);
        filme2.setClassificacao("12");
        filmeRepository.save(filme2);
    }

}

