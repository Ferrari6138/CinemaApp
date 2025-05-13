package com.cinemaapp.controllers;

import com.cinemaapp.models.Usuario;
import com.cinemaapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Método utilitário para obter o usuário autenticado
    private Usuario getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioService.findByEmail(email).orElse(null);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.registrarUsuario(usuario);
            return ResponseEntity.ok(novoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Usuario usuario = getAuthenticatedUser();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("user", usuario);
        return "usuarios/perfil";
    }



    @GetMapping("/configuracoes")
    public String configuracoes(Model model) {
        Usuario usuario = getAuthenticatedUser();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("user", usuario);
        return "usuarios/configuracoes";
    }


    @PostMapping("/configuracoes/avatar")
    public String atualizarAvatar(@RequestParam("avatar") MultipartFile file) {
        Usuario usuario = getAuthenticatedUser();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        if (file.isEmpty()) {
            return "redirect:/usuarios/configuracoes?error=avatar";
        }

        String uploadDir = "src/main/resources/static/uploads/";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            Path filePath = Paths.get(uploadDir, fileName);
            file.transferTo(filePath.toFile());
            usuario.setAvatar(fileName);
            usuarioService.save(usuario);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/usuarios/configuracoes?error=upload";
        }
        return "redirect:/usuarios/perfil";
    }

    @PostMapping("/configuracoes/senha")
    public String atualizarSenha(@RequestParam("novaSenha") String novaSenha,
                                 @RequestParam("confirmaSenha") String confirmaSenha) {
        Usuario usuario = getAuthenticatedUser();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        if (!novaSenha.equals(confirmaSenha)) {
            return "redirect:/usuarios/configuracoes?error=senha";
        }

        // Chama o método do serviço para atualizar a senha já criptografada
        usuarioService.atualizarSenha(usuario, novaSenha);
        return "redirect:/usuarios/perfil";
    }



}
