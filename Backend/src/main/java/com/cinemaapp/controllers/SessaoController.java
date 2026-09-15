package com.cinemaapp.controllers;

import com.cinemaapp.models.Sessao;
import com.cinemaapp.repository.CinemaRepository;
import com.cinemaapp.service.FilmeService;
import com.cinemaapp.service.SessaoService;
import com.cinemaapp.service.UsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/sessoes")
public class SessaoController {

    private final SessaoService sessaoService;
    private final FilmeService filmeService;
    private final UsuarioService usuarioService;
    private final CinemaRepository cinemaRepository;

    public SessaoController(SessaoService sessaoService, FilmeService filmeService,
                             UsuarioService usuarioService, CinemaRepository cinemaRepository) {
        this.sessaoService = sessaoService;
        this.filmeService = filmeService;
        this.usuarioService = usuarioService;
        this.cinemaRepository = cinemaRepository;
    }

    private com.cinemaapp.models.Usuario getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioService.findByEmail(auth.getName()).orElse(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nova/{filmeId}")
    public String formNova(@PathVariable Long filmeId, Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("filme", filmeService.findById(filmeId).orElseThrow());
        model.addAttribute("sessao", new Sessao());
        model.addAttribute("cinemas", cinemaRepository.findAll());
        return "sessoes/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/nova/{filmeId}")
    public String salvar(
            @PathVariable Long filmeId,
            @RequestParam Long cinemaId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dataHora,
            @RequestParam String sala,
            @RequestParam Integer capacidade,
            RedirectAttributes ra) {

        Sessao sessao = new Sessao();
        sessao.setFilme(filmeService.findById(filmeId).orElseThrow());
        sessao.setCinema(cinemaRepository.findById(cinemaId).orElseThrow());
        sessao.setDataHora(dataHora);
        sessao.setSala(sala);
        sessao.setCapacidade(capacidade);
        sessao.setAtiva(true);
        sessaoService.save(sessao);

        ra.addFlashAttribute("success", "Sessão criada com sucesso!");
        return "redirect:/filmes/" + filmeId;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id, RedirectAttributes ra) {
        Sessao sessao = sessaoService.findById(id).orElseThrow();
        Long filmeId = sessao.getFilme().getId();
        sessaoService.deleteById(id);
        ra.addFlashAttribute("success", "Sessão removida.");
        return "redirect:/filmes/" + filmeId;
    }
}
