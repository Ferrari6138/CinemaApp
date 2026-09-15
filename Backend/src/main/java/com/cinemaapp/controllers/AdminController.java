package com.cinemaapp.controllers;

import com.cinemaapp.service.CinemaManagerService;
import com.cinemaapp.service.FilmeService;
import com.cinemaapp.service.ReservaService;
import com.cinemaapp.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FilmeService filmeService;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final CinemaManagerService cinemaManagerService;

    public AdminController(FilmeService filmeService, ReservaService reservaService,
                            UsuarioService usuarioService, CinemaManagerService cinemaManagerService) {
        this.filmeService = filmeService;
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
        this.cinemaManagerService = cinemaManagerService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", usuarioService.findByEmail(auth.getName()).orElse(null));
        model.addAttribute("totalFilmes", filmeService.count());
        model.addAttribute("totalReservas", reservaService.countConfirmadas());
        model.addAttribute("receita", reservaService.sumReceita());
        model.addAttribute("totalUsuarios", usuarioService.count());
        model.addAttribute("reservasRecentes", reservaService.findRecentes());
        return "admin/dashboard";
    }

    @PostMapping("/gestor/executar")
    public String executarGestor(RedirectAttributes ra) {
        CinemaManagerService.Resultado resultado = cinemaManagerService.executar();
        ra.addFlashAttribute("success",
                resultado.filmesImportados() + " filme(s) importado(s) do TMDB, "
                        + resultado.sessoesCriadas() + " sessão(ões) criada(s).");
        return "redirect:/admin/dashboard";
    }
}
