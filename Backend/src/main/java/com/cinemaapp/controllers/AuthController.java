package com.cinemaapp.controllers;

import com.cinemaapp.models.Usuario;
import com.cinemaapp.service.PasswordResetService;
import com.cinemaapp.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;

    public AuthController(UsuarioService usuarioService, PasswordResetService passwordResetService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordResetService = passwordResetService;
        this.passwordEncoder = passwordEncoder;
    }

    // Exibe o formulário de login
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

    // Exibe o formulário de registro
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/register";
    }

    // Processa o registro do usuário
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validação básica do formulário
        if (result.hasErrors()) {
            model.addAttribute("usuario", usuario);
            return "auth/register";
        }

        // Validação da complexidade da senha
        if (!usuario.getSenha().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$")) {
            result.rejectValue("senha", "error.usuario", "A senha deve conter letras maiúsculas, minúsculas e números");
            model.addAttribute("usuario", usuario);
            return "auth/register";
        }

        try {
            // Verifica se o usuário já existe
            if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
                result.rejectValue("email", "error.usuario", "Este email já está cadastrado.");
                model.addAttribute("usuario", usuario);
                return "auth/register";
            }

            // Garante a criptografia da senha antes de registrar
            String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
            usuario.setSenha(senhaCriptografada);

            // Define a role padrão se não estiver definida
            if (usuario.getRole() == null || usuario.getRole().isEmpty()) {
                usuario.setRole("USER");
            }

            usuarioService.save(usuario);

            // Mensagem de sucesso
            redirectAttributes.addFlashAttribute("success", "Registro realizado com sucesso! Faça login.");
            return "redirect:/auth/login?registerSuccess=true";

        } catch (DataIntegrityViolationException e) {
            // Trata erros de duplicidade (email já cadastrado)
            redirectAttributes.addFlashAttribute("error", "Email já está cadastrado.");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/auth/register";
        } catch (Exception e) {
            // Trata outros erros
            redirectAttributes.addFlashAttribute("error", "Erro durante o registro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/auth/register";
        }
    }

    // Realiza o logout do usuário
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/auth/login?logout=true";
    }

    // Exibe o formulário de recuperação de senha
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuario = usuarioService.findByEmail(email);
        if (usuario.isPresent()) {
            passwordResetService.sendResetLink(usuario.get());
            redirectAttributes.addFlashAttribute("message", "E-mail de recuperação enviado!");
        } else {
            redirectAttributes.addFlashAttribute("error", "E-mail não encontrado.");
        }
        return "redirect:/auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("newPassword") String newPassword,
                                       RedirectAttributes redirectAttributes) {
        if (passwordResetService.updatePassword(token, newPassword)) {
            redirectAttributes.addFlashAttribute("message", "Senha atualizada com sucesso!");
            return "redirect:/auth/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Token inválido ou expirado.");
            return "redirect:/auth/forgot-password";
        }
    }

}
