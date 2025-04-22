package com.cinemaapp.controllers;

import com.cinemaapp.models.Usuario;
import com.cinemaapp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(required = false) Boolean error,
            @RequestParam(required = false) Boolean logout,
            @RequestParam(required = false) Boolean registerSuccess,
            Model model) {

        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", "Credenciais inválidas");
        }

        if (Boolean.TRUE.equals(logout)) {
            model.addAttribute("message", "Logout realizado com sucesso");
        }

        if (Boolean.TRUE.equals(registerSuccess)) {
            model.addAttribute("message", "Registro realizado com sucesso! Faça login.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validação básica do formulário
        if (result.hasErrors()) {
            model.addAttribute("usuario", usuario); // Mantém os dados preenchidos
            return "auth/register";
        }

        // Validação adicional (opcional)
        if (!usuario.getSenha().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$")) {
            result.rejectValue("senha", "error.usuario", "A senha deve conter letras maiúsculas, minúsculas e números");
            model.addAttribute("usuario", usuario);
            return "auth/register";
        }

        try {

            // Define role padrão se não estiver definida
            if (usuario.getRole() == null || usuario.getRole().isEmpty()) {
                usuario.setRole("USER");
            }

            usuarioService.registrarUsuario(usuario);

            // Mensagem de sucesso que persiste após redirecionamento
            redirectAttributes.addFlashAttribute("success",
                    "Registro realizado com sucesso! Faça login para continuar.");

            return "redirect:/auth/login?registerSuccess=true";

        } catch (DataIntegrityViolationException e) {
            // Trata erros de constraint (email duplicado)
            redirectAttributes.addFlashAttribute("error",
                    "Este email já está cadastrado. Por favor, use outro email.");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/auth/register";

        } catch (Exception e) {
            // Trata outros erros genéricos
            redirectAttributes.addFlashAttribute("error",
                    "Ocorreu um erro durante o registro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/auth/register";
        }
    }
}