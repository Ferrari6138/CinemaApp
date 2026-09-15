package com.cinemaapp.controllers;

import com.cinemaapp.models.Usuario;
import com.cinemaapp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public UsuarioController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    private Usuario getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioService.findByEmail(auth.getName()).orElse(null);
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Usuario usuario = getAuthenticatedUser();
        if (usuario == null) return "redirect:/auth/login";
        model.addAttribute("user", usuario);
        return "usuarios/perfil";
    }

    @GetMapping("/configuracoes")
    public String configuracoes(Model model) {
        Usuario usuario = getAuthenticatedUser();
        if (usuario == null) return "redirect:/auth/login";
        model.addAttribute("user", usuario);
        return "usuarios/configuracoes";
    }

    @PostMapping("/configuracoes/avatar")
    public String atualizarAvatar(@RequestParam("avatar") MultipartFile file, RedirectAttributes ra) {
        Usuario usuario = getAuthenticatedUser();
        if (usuario == null) return "redirect:/auth/login";
        if (file.isEmpty()) {
            ra.addFlashAttribute("error", "Selecione uma imagem.");
            return "redirect:/usuarios/configuracoes";
        }
        try {
            String nomeOriginal = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String fileName = System.currentTimeMillis() + "_" + nomeOriginal;
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath);
            usuario.setAvatar(fileName);
            usuarioService.save(usuario);
            ra.addFlashAttribute("success", "Avatar atualizado com sucesso!");
        } catch (IOException e) {
            ra.addFlashAttribute("error", "Erro ao fazer upload: " + e.getMessage());
        }
        return "redirect:/usuarios/perfil";
    }

    @PostMapping("/configuracoes/senha")
    public String atualizarSenha(
            @RequestParam("senhaAtual") String senhaAtual,
            @RequestParam("novaSenha") String novaSenha,
            @RequestParam("confirmaSenha") String confirmaSenha,
            RedirectAttributes ra) {
        Usuario usuario = getAuthenticatedUser();
        if (usuario == null) return "redirect:/auth/login";
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            ra.addFlashAttribute("error", "Senha atual incorreta.");
            return "redirect:/usuarios/configuracoes";
        }
        if (!novaSenha.equals(confirmaSenha)) {
            ra.addFlashAttribute("error", "As senhas não coincidem.");
            return "redirect:/usuarios/configuracoes";
        }
        if (!novaSenha.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$")) {
            ra.addFlashAttribute("error", "A senha deve conter letras maiúsculas, minúsculas e números.");
            return "redirect:/usuarios/configuracoes";
        }
        usuarioService.atualizarSenha(usuario, novaSenha);
        ra.addFlashAttribute("success", "Senha atualizada com sucesso!");
        return "redirect:/usuarios/perfil";
    }
}
