package com.cinemaapp.config;

import com.cinemaapp.models.Filme;
import com.cinemaapp.models.Usuario;
import com.cinemaapp.repository.FilmeRepository;
import com.cinemaapp.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository,
                                   FilmeRepository filmeRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // Criar usuários iniciais
            if (usuarioRepository.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail("admin@cinema.com");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");

                Usuario cliente = new Usuario();
                cliente.setNome("Cliente Teste");
                cliente.setEmail("cliente@cinema.com");
                cliente.setSenha(passwordEncoder.encode("cliente123"));
                cliente.setRole("USER");

                usuarioRepository.saveAll(Arrays.asList(admin, cliente));
            }

            // Criar filmes iniciais
            if (filmeRepository.count() == 0) {
                Filme filme1 = new Filme();
                filme1.setTitulo("O Poderoso Chefão");
                filme1.setDescricao("Uma família mafiosa luta para estabelecer sua supremacia nos EUA depois da segunda guerra mundial.");
                filme1.setHorario(LocalTime.of(19, 30));
                filme1.setDuracao(175);
                filme1.setClassificacao("16+");

                Filme filme2 = new Filme();
                filme2.setTitulo("Interestelar");
                filme2.setDescricao("Uma equipe de exploradores viaja através de um buraco de minhoca no espaço.");
                filme2.setHorario(LocalTime.of(21, 0));
                filme2.setDuracao(169);
                filme2.setClassificacao("12+");

                filmeRepository.saveAll(Arrays.asList(filme1, filme2));
            }
        };
    }
}
