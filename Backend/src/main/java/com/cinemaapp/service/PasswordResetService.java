
package com.cinemaapp.service;

import com.cinemaapp.models.PasswordResetToken;
import com.cinemaapp.models.Usuario;
import com.cinemaapp.repository.PasswordResetTokenRepository;
import com.cinemaapp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void sendResetLink(Usuario usuario) {
        // Gera um token único
        String token = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(30);

        // Cria e configura o token de redefinição de senha
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setExpirationDate(expiration);

        // Salva o token no banco
        tokenRepository.deleteByUsuario(usuario);
        tokenRepository.save(resetToken);

        // Envia o e-mail de recuperação
        String link = "http://localhost:8080/auth/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(usuario.getEmail());
        message.setSubject("Recuperação de Senha - CinemaApp");
        message.setText("Clique no link para redefinir sua senha: " + link);
        mailSender.send(message);
    }

    public boolean updatePassword(String token, String newPassword) {
        Optional<PasswordResetToken> optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isPresent() && !optionalToken.get().isExpired()) {
            Usuario usuario = optionalToken.get().getUsuario();
            usuario.setSenha(passwordEncoder.encode(newPassword));
            usuarioRepository.save(usuario);
            tokenRepository.delete(optionalToken.get());
            return true;
        }
        return false;
    }
}
