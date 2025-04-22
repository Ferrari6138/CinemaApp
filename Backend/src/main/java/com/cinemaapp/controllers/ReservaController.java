package com.cinemaapp.controllers;

import com.cinemaapp.models.Reserva;
import com.cinemaapp.service.ReservaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.cinemaapp.service.FilmeService;

import java.util.List;


@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final FilmeService filmeService;

    public ReservaController(ReservaService reservaService, FilmeService filmeService) {
        this.reservaService = reservaService;
        this.filmeService = filmeService;
        ;
    }

    @PostMapping("/nova")
    public String criarReservaViaForm(@RequestParam("filmeId") Long filmeId,
                                      @RequestParam("quantidade") Integer quantidade,
                                      @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        reservaService.criarReserva(filmeId, quantidade, user.getUsername());
        return "redirect:/reservas/usuario";
    }

    @GetMapping("/nova/{filmeId}")
    public String mostrarFormularioReserva(@PathVariable Long filmeId, Model model) {
        model.addAttribute("filme", filmeService.findById(filmeId).orElseThrow());
        model.addAttribute("reserva", new Reserva());
        return "reservas/form";
    }



    @GetMapping("/usuario/{usuarioId}")
    public String listarReservasPorUsuario(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("reservas", reservaService.findByUsuarioId(usuarioId));
        return "reservas/list";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarReserva(@PathVariable Long id,
                                  @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {

        reservaService.cancelarReserva(id);
        return "redirect:/reservas/usuario";
    }


    @GetMapping
    public String listarTodasReservas(Model model) {
        model.addAttribute("reservas", reservaService.findAll());
        return "reservas/list";
    }

    @GetMapping("/usuario")
    public String listarReservasDoUsuarioLogado(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user, Model model) {
        List<Reserva> reservas = reservaService.findByEmail(user.getUsername());
        model.addAttribute("reservas", reservas);
        return "reservas/list";
    }
}