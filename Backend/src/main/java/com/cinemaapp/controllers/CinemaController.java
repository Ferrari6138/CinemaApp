package com.cinemaapp.controllers;

import com.cinemaapp.models.Cinema;
import com.cinemaapp.models.Usuario;
import com.cinemaapp.repository.CinemaRepository;
import com.cinemaapp.service.UsuarioService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/cinemas")
@PreAuthorize("hasRole('ADMIN')")
public class CinemaController {

    private final CinemaRepository cinemaRepository;
    private final UsuarioService usuarioService;

    public CinemaController(CinemaRepository cinemaRepository, UsuarioService usuarioService) {
        this.cinemaRepository = cinemaRepository;
        this.usuarioService = usuarioService;
    }

    private Usuario getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioService.findByEmail(auth.getName()).orElse(null);
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("cinemas", cinemaRepository.findAll());
        return "admin/cinemas/list";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("cinema", new Cinema());
        return "admin/cinemas/form";
    }

    @PostMapping("/novo")
    public String salvar(@RequestParam String nome, @RequestParam(required = false) String endereco,
                         RedirectAttributes ra) {
        Cinema cinema = new Cinema(nome, endereco);
        cinemaRepository.save(cinema);
        ra.addFlashAttribute("success", "Cinema cadastrado com sucesso!");
        return "redirect:/admin/cinemas";
    }

    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Cinema cinema = cinemaRepository.findById(id).orElse(null);
        if (cinema == null) return "redirect:/admin/cinemas";
        model.addAttribute("user", getUser());
        model.addAttribute("cinema", cinema);
        return "admin/cinemas/form";
    }

    @PostMapping("/editar/{id}")
    public String atualizar(@PathVariable Long id, @RequestParam String nome,
                            @RequestParam(required = false) String endereco, RedirectAttributes ra) {
        Cinema cinema = cinemaRepository.findById(id).orElse(null);
        if (cinema == null) return "redirect:/admin/cinemas";
        cinema.setNome(nome);
        cinema.setEndereco(endereco);
        cinemaRepository.save(cinema);
        ra.addFlashAttribute("success", "Cinema atualizado com sucesso!");
        return "redirect:/admin/cinemas";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            cinemaRepository.deleteById(id);
            ra.addFlashAttribute("success", "Cinema removido.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Não é possível remover: existem sessões cadastradas nesse cinema.");
        }
        return "redirect:/admin/cinemas";
    }
}
