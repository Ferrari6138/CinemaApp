package com.cinemaapp.service;

import com.cinemaapp.models.Filme;
import com.cinemaapp.repository.FilmeRepository;
import org.springframework.stereotype.Service;
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
        return filmeRepository.findByIdWithSessoes(id);
    }

    public List<Filme> search(String query) {
        if (query == null || query.isBlank()) return findAll();
        return filmeRepository.search(query);
    }

    public List<Filme> findByGenero(Long generoId) {
        return filmeRepository.findByGeneroId(generoId);
    }

    public Filme save(Filme filme) {
        return filmeRepository.save(filme);
    }

    public void deleteById(Long id) {
        filmeRepository.deleteById(id);
    }

    public long count() {
        return filmeRepository.count();
    }
}
