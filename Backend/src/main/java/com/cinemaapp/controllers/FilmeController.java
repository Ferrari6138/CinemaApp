package com.cinemaapp.controllers;

import com.cinemaapp.models.Filme;
import com.cinemaapp.service.FilmeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/filmes")
public class FilmeController {

    private final FilmeService filmeService;

    public FilmeController(FilmeService filmeService) {
        this.filmeService = filmeService;
    }

    @GetMapping
    public String listarFilmes(Model model) {
        model.addAttribute("filmes", filmeService.findAll());
        return "filmes/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String mostrarFormularioNovoFilme(Model model) {
        model.addAttribute("filme", new Filme());
        return "filmes/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/novo")
    public String salvarFilme(@ModelAttribute Filme filme) {
        filmeService.save(filme);
        return "redirect:/filmes";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        Filme filme = filmeService.findById(id).orElse(null);
        if (filme == null) {
            model.addAttribute("error", "Filme não encontrado para edição!");
            return "redirect:/filmes";
        }
        model.addAttribute("filme", filme);
        return "filmes/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/editar/{id}")
    public String atualizarFilme(@PathVariable Long id, @ModelAttribute Filme filmeAtualizado) {
        Filme filmeExistente = filmeService.findById(id).orElse(null);
        if (filmeExistente == null) {
            return "redirect:/filmes"; // Se o filme não existir, volta pra lista
        }

        // Atualizar os campos
        filmeExistente.setTitulo(filmeAtualizado.getTitulo());
        filmeExistente.setDescricao(filmeAtualizado.getDescricao());
        filmeExistente.setHorario(filmeAtualizado.getHorario());
        filmeExistente.setDuracao(filmeAtualizado.getDuracao());
        filmeExistente.setClassificacao(filmeAtualizado.getClassificacao());

        filmeService.save(filmeExistente);
        return "redirect:/filmes";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/deletar")
    public String deletarFilme(@PathVariable Long id) {
        filmeService.deleteById(id);
        return "redirect:/filmes";
    }

    @GetMapping("/{id}")
    public String detalhesFilme(@PathVariable Long id, Model model) {
        Filme filme = filmeService.findById(id).orElse(null);
        if (filme == null) {
            model.addAttribute("error", "Filme não encontrado!");
            return "redirect:/filmes";
        }
        model.addAttribute("filme", filme);
        return "filmes/detalhes";
    }
}
