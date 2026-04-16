package com.cinemaapp.controllers;

import com.cinemaapp.models.Sessao;
import com.cinemaapp.models.Usuario;
import com.cinemaapp.service.ReservaService;
import com.cinemaapp.service.SessaoService;
import com.cinemaapp.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final SessaoService sessaoService;
    private final UsuarioService usuarioService;

    public ReservaController(ReservaService reservaService, SessaoService sessaoService, UsuarioService usuarioService) {
        this.reservaService = reservaService;
        this.sessaoService = sessaoService;
        this.usuarioService = usuarioService;
    }

    private Usuario getUser(String email) {
        return usuarioService.findByEmail(email).orElse(null);
    }

    @GetMapping("/sessao/{sessaoId}")
    public String mostrarAssentos(@PathVariable Long sessaoId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        Sessao sessao = sessaoService.findById(sessaoId).orElseThrow();
        model.addAttribute("user", getUser(userDetails.getUsername()));
        model.addAttribute("sessao", sessao);
        model.addAttribute("assentosOcupados", sessaoService.getAssentosOcupados(sessaoId));
        model.addAttribute("disponiveis", sessaoService.getAssentosDisponiveis(sessaoId));
        return "reservas/assentos";
    }

    @PostMapping("/nova")
    public String criarReserva(
            @RequestParam Long sessaoId,
            @RequestParam String assentos,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes ra) {
        try {
            reservaService.criarReserva(sessaoId, assentos, userDetails.getUsername());
            ra.addFlashAttribute("success", "Reserva confirmada com sucesso!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/sessao/" + sessaoId;
        }
        return "redirect:/reservas/minhas";
    }

    @GetMapping("/minhas")
    public String minhasReservas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("user", getUser(userDetails.getUsername()));
        model.addAttribute("reservas", reservaService.findReservasAtivasByEmail(userDetails.getUsername()));
        return "reservas/list";
    }

    @GetMapping("/usuario/{usuarioId}")
    public String reservasPorUsuario(@PathVariable Long usuarioId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        model.addAttribute("user", getUser(userDetails.getUsername()));
        model.addAttribute("reservas", reservaService.findByUsuarioId(usuarioId));
        return "reservas/list";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarReserva(@PathVariable Long id, RedirectAttributes ra) {
        reservaService.cancelarReserva(id);
        ra.addFlashAttribute("success", "Reserva cancelada.");
        return "redirect:/reservas/minhas";
    }
}
